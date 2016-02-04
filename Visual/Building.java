package Visual;

/**
 * Represents a building.
 */
public interface Building extends Comparable<Building>{
  /**
   * @return the name of the building
   */
  String name();

  /**
   * An int that represents the building itself
   *
   * @return the buildling id
   */
  int buildingID();

  /**
   * Some buildings have more than one site id
   *
   * @return the int that represents the value of the meter id
   */
  Site[] sites();

  /**
   * Returns the carbon footprint of the building
   *
   * @return the footprint
   */
  int footprint();

  /**
   * Returns the perimeter of the buildling
   *
   * @return the perimeter
   */
  int perimeter();

  /**
   * Returns the area of the building
   *
   * @return the area
   */
  double area();

  /**
   * Returns the latitude and longitude of the center of the building
   *
   * @return the centroid
   */
  LatLong centroid();

  /**
   * Returns the latitude and longitude points that make up the corners of the buildling
   *
   * @return the outline
   */
  LatLong[] outline();

  /**
   * Returns the current time stamp of the building
   *
   * @return the current unix time stamp
   */
  long currentTime();

  /**
   * Changes the currentTime of the building to the given unixTime
   * @param time the nex unix time of the building
   */
  void changeTime(long time);

  /**
   * The current wattage of electricity the building is using.
   *
   * @return the wattage
   */
  double currentWattage();

  /**
   * The wattage of electricity the building was using at the specified time
   *
   * @return the wattage at the specified time
   * @param time
   */
  double wattage(long time);
}
