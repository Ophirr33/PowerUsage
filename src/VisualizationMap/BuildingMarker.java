package VisualizationMap;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.utils.MapPosition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import Visual.Building;
import Visual.LatLong;
import processing.core.PGraphics;

/**
 * A marker used to represent a building on the map
 */
public final class BuildingMarker extends SimplePolygonMarker implements Comparable<BuildingMarker> {
  Building building;
  private Color c;
  BuildingMarker(Building b/*, Color c*/) {
    Objects.requireNonNull(b, "Building must be set");
    building = b;
    List<Location> locations = new ArrayList<>();
    for (LatLong l : b.outline()) {
      Location loc = new Location(l.longitude(), l.lat());
      locations.add(loc);
    }
    this.addLocations(locations);
    c = new Color(255, 255, 255, 0);
  }

  @Override
  public int compareTo(BuildingMarker other) {
    Objects.requireNonNull(other);
    return this.building.compareTo(other.building);
  }

  public void draw(PGraphics pg, List<MapPosition> mapPositions) {
    // marker
    pg.pushStyle();
    pg.strokeWeight(2);
    pg.stroke(0, 0, 0);
    pg.fill(c.r, c.g, c.b, c.alpha);
    if (this.isSelected()) {
      pg.fill(255, 255, 255, 255);
    }
    pg.beginShape();
    for (MapPosition mapPosition : mapPositions) {
      pg.vertex(mapPosition.x, mapPosition.y);
    }
    pg.endShape();
    pg.popStyle();
  }

  /**
   * Sets the fill color
   * @param c the new fill color
   */
  public void newColor(Color c) {
    this.c = c;
  }

  /**
   * Balances out all of the colors according to the current time, from min to max.
   *
   * @param markers the list of markers to balance out
   * @param time    the time to balance with
   * @throws IllegalArgumentException if there are more than 255 markers
   */
  static void balanceColors(ArrayList<BuildingMarker> markers, long time) {
    if (markers.size() > 255) {
      throw new IllegalArgumentException("Too many markers, can only balance a total" +
              "of 255 markers");
    }
    for (BuildingMarker marker : markers) {
      marker.building.changeTime(time);
    }
    Collections.sort(markers);
    for (int i = 0; i < markers.size(); i++) {
      BuildingMarker bm = markers.get(i);
      if (bm.building.currentWattage() == 0) {
        bm.newColor(new Color(0, 128, 255, 225));
      } else {
        int diff = 255 / markers.size();
        bm.newColor(new Color(255, 255 - (diff * i), 0, 175));
      }
    }
  }

  /**
   * Creates an array of doubles for last specified number of readings differing among the
   * specified number of times.
   * @param minTime the minimum point which there are no times past
   * @param timeDiff how much to offset the times by
   * @param numberOfReadings how many times to count down
   * @return the array of watts for the last specified times.
   */
  double[] lastReadings(long minTime, long timeDiff, int numberOfReadings) {
    if (numberOfReadings < 1) {
      throw new IllegalArgumentException("There must be a number of readings");
    }
    if (timeDiff < 1) {
      throw new IllegalArgumentException("Must have a positive integer for time difference");
    }
    double[] result = new double[numberOfReadings];
    for (int i = 0; i < numberOfReadings; i++) {
      long time = building.currentTime() - (timeDiff * i);
      if (time < minTime) {
        result[i] = -1.0;
      } else {
        result[i] = building.wattage(time);
      }
    }
    return result;
  }

  /**
   * Identifies the maximum and minimum value in the array, and scales them down
   * to [0, 1]
   * @param toScale the array of doubles to scale
   * @return an array of values between [0, 1] representing the relative size to the max
   * and min value of the original array
   */
  double[] scaleDoubles(double[] toScale) {
    double max = 0.0;
    double min = 0.0;
    if (toScale.length > 0) {
      max = toScale[0];
      min = toScale[0];
    }
    for (double d : toScale) {
      if (d > max) {
        max = d;
      }
      if (d < min) {
        min = d;
      }
    }
    max -= min;
    double[] result = new double[toScale.length];
    if (max == 0) {
      return result;
    }
    for (int i = 0; i < toScale.length; i++) {
      result[i] = (toScale[i] - min) / max;
    }
    return result;
  }

  /**
   * Creates the string array containing the last week worth of power readings
   * @param minTime the minimum time that there are no readings past.
   * @return the week's worth of readings in a string array.
   */
  String[] lastWeek(long minTime) {
    String[] result = new String[7];
    long currentTime = this.building.currentTime();
    long timeDiff = 86400;
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d");
    for (int i = 0; i < 7; i++) {
      if (i == 0) {
        String watt = Double.toString(building.currentWattage());
        if (watt.length() > 9) {
          watt = watt.substring(0, 9);
        }
        watt += " Watts";
        result[i] = "Today: " + watt;
      } else if ((currentTime - (timeDiff * i)) < minTime) {
        result[i] = "No such time: ";
      } else {
        String watt = Double.toString(building.wattage(currentTime - (timeDiff * i)));
        if (watt.length() > 9) {
          watt = watt.substring(0, 9);
        }
        watt += " Watts";
        result[i] = sdf.format((currentTime - (timeDiff * i)) * 1000) + ": " + watt;
      }
    }
    return result;
  }

  /**
   * Converts the given string date into unix time.
   * @param givenDate the string to parse. Must be in "mmddyyyy"
   * @return the unix time
   */
  static long stringToUnix(String givenDate) {
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    try {
      Date date = dateFormat.parse(givenDate);
      long unixTime = date.getTime() / 1000;
      System.out.println(unixTime);
      return unixTime;
    } catch (ParseException e) {
      throw new IllegalArgumentException("String must be formatted as \"DAYMONTHYEAR in" +
              " numbers\"");
    }
  }

  static String unixTostring(long unix) {
    DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' hh:mm");
    return dateFormat.format(unix * 1000);
  }

  /**
   * Clamps the given int between [0, 255]
   * @param i the number to clamp
   * @return the clamped number
   */
  private static int clamp(int i) {
    if (i > 255) {
      return 255;
    } else if (i < 0) {
      return 0;
    } else {
      return i;
    }
  }


  /**
   * A class to hold rgb values.
   */
  static class Color {
    public final int r;
    public final int g;
    public final int b;
    public final int alpha;

    Color(int r, int g, int b, int alpha) {
      this.r = clamp(r);
      this.g = clamp(g);
      this.b = clamp(b);
      this.alpha = clamp(alpha);
    }
  }
}
