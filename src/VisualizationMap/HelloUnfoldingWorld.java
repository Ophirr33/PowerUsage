package VisualizationMap;

import Visual.Building;
import Visual.CsvReader;
import VisualizationMap.BuildingMarker;
import processing.core.PApplet;
import processing.core.PFont;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Hello Unfolding World.
 *
 * Download the distribution with examples for many more examples and features.
 */
public class HelloUnfoldingWorld extends PApplet {

  UnfoldingMap map;
  ArrayList<Building> buildings;
  ArrayList<BuildingMarker> buildingMarkers;
  PFont myFont;
  BuildingMarker hitMarker;
  String entry;
  boolean cycle = false;

  public static void main(String args[]) {
    PApplet.main(new String[]{"VisualizationMap.HelloUnfoldingWorld"});
  }

  public void setup() {
    size(1600, 900, OPENGL);
    smooth();
    map = new UnfoldingMap(this, new Google.GoogleMapProvider());
    MapUtils.createDefaultEventDispatcher(this, map);
    myFont = createFont("Arial", 16);
    entry = "[Type to Start]";
    try {
      buildings = CsvReader.build(
              "buildingDB.csv",
              "siteDB.csv",
              "measureDB_parallel.csv");
      buildingMarkers = new ArrayList<>();
      for (Building b : buildings) {
        BuildingMarker bm = new BuildingMarker(b);
        buildingMarkers.add(bm);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (BuildingMarker bm : buildingMarkers) {
      map.addMarker(bm);
    }
    BuildingMarker.balanceColors(buildingMarkers, 1386547200);
    Location northeasternUniversity = new Location(42.33939817882002, -71.08961267153299);
    map.zoomAndPanTo(17, northeasternUniversity);
    map.setZoomRange(15, 19);
    map.setPanningRestriction(northeasternUniversity, 1);
  }

  public void draw() {
    background(0);
    map.draw();
    if(cycle) {
      BuildingMarker.balanceColors(buildingMarkers, buildings.get(0).currentTime() + 9000L);
    }
    pushStyle();
    fill(160, 160, 160);
    stroke(0, 102, 51);
    strokeWeight(5);
    rect(width / 2 - 170, 30, 400, 100);
    textFont(createFont("Arial", 26));
    strokeWeight(2);
    fill(0, 0, 0);
    text(entry, width / 2 - 15, 40, 200, 200);
    text("'C' to cycle", 200, 200, 150, 50);
    text(BuildingMarker.unixTostring(buildings.get(0).currentTime()), width / 2 - 150,
            75, 400, 200);
    textFont(createFont("Arial", 14));
    text("Enter Date as MM/DD/YYYY:", width / 2 - 150, 40, 100, 200);
    popStyle();
    if (buildings.get(0).currentTime() >= 1418168700) {
      cycle = false;
    }
    if (hitMarker != null && hitMarker.isSelected()) {
      pushStyle();
      if (myFont != null) {
        textFont(myFont);
      }
      int x = width - width / 4;
      int y = height / 6;
      int infoWidth = 300;
      int infoHeight = 500;
      fill(160, 160, 160, 255);
      rect(x, y, infoWidth, infoHeight);
      strokeWeight(20);
      stroke(0, 102, 51);
      rect(x, y, infoWidth, infoHeight);
      fill(0, 0, 0);
      text(hitMarker.building.name(), x + 25, y + 25, infoWidth, infoHeight);
      text("Footprint: " + Integer.toString(hitMarker.building.footprint()),
              x + 25, y + 50, infoWidth, infoHeight);
      String wattage = Double.toString(hitMarker.building.currentWattage());
      if (wattage.length() > 9) {
        wattage = wattage.substring(0, 9);
      }
      text("Current Wattage: " + wattage, x + 25, y + 75, infoWidth, infoHeight);
      text("Power usage for last seven days: ", x + 25, y + 100, infoWidth, infoHeight);
      textFont(createFont("Arial", 14));
      String[] times = hitMarker.lastWeek(1386547200);
      text(times[0], x + 25, y + 125, infoWidth, infoHeight);
      text(times[1], x + 25, y + 175, infoWidth, infoHeight);
      text(times[2], x + 25, y + 225, infoWidth, infoHeight);
      text(times[3], x + 25, y + 275, infoWidth, infoHeight);
      text(times[4], x + 25, y + 325, infoWidth, infoHeight);
      text(times[5], x + 25, y + 375, infoWidth, infoHeight);
      text(times[6], x + 25, y + 425, infoWidth, infoHeight);
      double[] rectangleScales = hitMarker.scaleDoubles(
              hitMarker.lastReadings(1386547200, 86400, 7)
      );
      stroke(0, 0, 0);
      strokeWeight(2);
      fill(150, 10, 0);
      rect(x + 50, y + 150, (int) (10 + rectangleScales[0] * 190), 20);
      rect(x + 50, y + 200, (int) (10 + rectangleScales[1] * 190), 20);
      rect(x + 50, y + 250, (int) (10 + rectangleScales[2] * 190), 20);
      rect(x + 50, y + 300, (int) (10 + rectangleScales[3] * 190), 20);
      rect(x + 50, y + 350, (int) (10 + rectangleScales[4] * 190), 20);
      rect(x + 50, y + 400, (int)(10 + rectangleScales[5] * 190), 20);
      rect(x + 50, y + 450, (int)(10 + rectangleScales[6] * 190), 20);
      popStyle();
    }
  }

  public void mouseMoved() {
    Marker foundMarker = map.getFirstHitMarker(mouseX, mouseY);
    if (foundMarker != null && foundMarker instanceof BuildingMarker) {
      hitMarker = (BuildingMarker) foundMarker;
      for (Marker marker : map.getMarkers()) {
        marker.setSelected(false);
      }
      hitMarker.setSelected(true);
    } else {
      for (Marker marker : map.getMarkers()) {
        marker.setSelected(false);
      }
    }
  }

  public void keyPressed() {
    if (entry.equals("[Type to Start]")) {
      entry = "";
    }
    if(((key >= '0' && key <= '9') || key == '/') && !(entry.length() > 9)) {
      entry += key;
    } else if ((key == BACKSPACE || key == DELETE)&& entry.length() > 0) {
      entry = entry.substring(0, entry.length() - 1);
    } else if ((key == ENTER || key == RETURN)&& entry.length() == 10) {
      BuildingMarker.balanceColors(buildingMarkers, BuildingMarker.stringToUnix(entry));
      entry = "";
    } else if ((key == 'c') || key == 'C') {
      cycle = !(cycle);
    } else {}
  }
}

