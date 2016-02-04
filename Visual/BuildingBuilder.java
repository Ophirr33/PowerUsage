package Visual;

import java.util.HashMap;
import java.util.Objects;

/**
 * A helper class used to create an instance of a Building, allows for incremental
 * building
 */
public class BuildingBuilder {
  private String name;
  private int bID = -1;
  private Site[] sites;
  private int footprint;
  private int perimeter;
  private double area;
  private LatLong centroid;
  private LatLong[] outline;
  private long currentTime;

  /**
   * Creates the initial, empty builder
   */
  public BuildingBuilder() {}

  /**
   * Sets the name
   * @param name the name of the building
   * @return this builder
   */
  public BuildingBuilder name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the building ID
   * @param bID the id of the building
   * @return this builder
   */
  public BuildingBuilder bID(int bID) {
    this.bID = bID;
    return this;
  }

  /**
   * Sets the sites
   * @param sites the electric meters of the building
   * @return this builder
   */
  public BuildingBuilder sites(Site[] sites) {
    this.sites = sites;
    return this;
  }

  /**
   * Sets the footprint
   * @param footprint the footprint of the building
   * @return this builder
   */
  public BuildingBuilder footprint(int footprint) {
    this.footprint = footprint;
    return this;
  }

  /**
   * Sets the perimeter of the building
   * @param perimeter the perimeter of the building
   * @return this builder
   */
  public BuildingBuilder perimeter(int perimeter) {
    this.perimeter = perimeter;
    return this;
  }

  /**
   * Sets the perimeter of the building
   * @param area the area of the building
   * @return this builder
   */
  public BuildingBuilder area(double area) {
    this.area = area;
    return this;
  }

  /**
   * Sets the centroid of the building
   * @param centroid the latitude and longitude of the center point of the building
   * @return this builder
   */
  public BuildingBuilder centroid(LatLong centroid) {
    this.centroid = centroid;
    return this;
  }

  /**
   * Sets the outline of the building
   * @param outline the latitude and longitude of the outlines of the building
   * @return
   */
  public BuildingBuilder outline(LatLong[] outline) {
    this.outline = outline;
    return this;
  }

  /**
   * Sets the current time for the building
   * @param currentTime the current time in unix
   * @return this builder
   */
  public BuildingBuilder currentTime(long currentTime) {
    this.currentTime = currentTime;
    return this;
  }

  /**
   * Returns the current building ID for identification purposes
   * @return the current building ID
   * @throws IllegalStateException if this is called before an ID has been set
   */
  public int identify() {
    if (bID == -1) {
      throw new IllegalStateException("Builder has not been given an id yet!");
    }
    return bID;
  }

  /**
   * Builds a building from the given parameters.
   * @return the building
   * @throws NullPointerException if any of the params have not been set
   */
  public Building build() {
    Objects.requireNonNull(this.name, "Name not Initialized");
    Objects.requireNonNull(this.sites, "Sites not Initialized");
    Objects.requireNonNull(this.centroid, "Centroid not Initialized");
    Objects.requireNonNull(this.outline, "Outline not Initialized");
    return new BuildingImpl(this.name, this.bID, this.sites, this.footprint, this.perimeter,
            this.area, this.centroid, this.outline, this.currentTime);
  }
}
