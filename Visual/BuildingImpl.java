package Visual;

import java.util.Arrays;
import java.util.Objects;

/**
 * An implementation of the building class that reads data from CVS readers.
 */
public class BuildingImpl implements Building {

  private String name;
  private int bID;
  private Site[] sites;
  private int footprint;
  private int perimeter;
  private double area;
  private LatLong centroid;
  private LatLong[] outline;
  private long currentTime;


  /**
   * A public constructor that constructs the building from the parameters
   * @param name the name of the building
   * @param bID the id of the building
   * @param sites the electric meters of the building
   * @param footprint the footprint of the building
   * @param perimeter the perimeter of the building
   * @param area the area of the building
   * @param centroid the center of the building in latitude/longitude
   * @param outline the outline of the building in latitude/longitude
   * @param currentTime the current time of the building
   * @throws NullPointerException if any of the parameters have not been set.
   */
  public BuildingImpl(String name, int bID, Site[] sites, int footprint, int perimeter,
                      double area, LatLong centroid, LatLong[] outline, long currentTime)
  {
    Objects.requireNonNull(name, "Name not Initialized");
    Objects.requireNonNull(sites, "Sites not Initialized");
    Objects.requireNonNull(centroid, "Centroid not Initialized");
    Objects.requireNonNull(outline, "Outline not Initialized");
    Objects.requireNonNull(currentTime, "Current Time not Initialized");
    this.name = name;
    this.bID = bID;
    this.sites = sites;
    this.footprint = footprint;
    this.perimeter = perimeter;
    this.area = area;
    this.centroid = centroid;
    this.outline = outline;
    this.currentTime = currentTime;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public int buildingID() {
    return bID;
  }

  @Override
  public Site[] sites() {
    return sites;
  }

  @Override
  public int footprint() {
    return footprint;
  }

  @Override
  public int perimeter() {
    return perimeter;
  }

  @Override
  public double area() {
    return area;
  }

  @Override
  public LatLong centroid() {
    return centroid;
  }

  @Override
  public LatLong[] outline() {
    return outline;
  }

  @Override
  public long currentTime() {
    return currentTime;
  }

  @Override
  public void changeTime(long time) {
    long roundedTime = SiteImpl.roundToNearestTime(1386547200, 1418168700, 900, time);
    currentTime = roundedTime;
  }

  @Override
  public double currentWattage() {
    return wattage(currentTime);
  }

  @Override
  public double wattage(long time) {
    int siteNum = sites.length;
    double total = 0;
    if (siteNum < 1) {
      return 0;
    }
    for (Site s : sites) {
      total += s.wattage(time);
    }
    return total / siteNum;
  }

  @Override
  public String toString() {
    return "Visual.Building: [" + name + ", " + Integer.toString(bID) + "], Sites: " +
            Arrays.toString(sites) + ", Specs: [" + "footprint: " + Integer.toString(footprint) +
            ", perimeter: " + Integer.toString(perimeter) + ", area: " + Double.toString(area) +
            "] Coordinates: " + centroid.toString() + ", " + Arrays.toString(outline) +
            ", Data: [" + Long.toString(currentTime) + ", " + Double.toString(currentWattage()) +
            "Watts]";
  }

  @Override
  public int compareTo(Building other) {
    return Double.compare(currentWattage(), other.currentWattage());
  }
}
