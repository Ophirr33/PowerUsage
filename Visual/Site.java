package Visual;

import java.util.HashMap;

/**
 * Represents a site
 */
public interface Site {
  /**
   * The wattage of electricity the site was using at the specified time
   *
   * @return the wattage at the specified time
   * @param time
   */
  double wattage(long time);

  /**
   * Return the identifying number of the site
   *
   * @return the siteID
   */
  int siteID();

  /**
   * Return the identifying number of the building
   *
   * @return the buildingID that this site is in
   */
  int buildingID();

  /**
   * Sets a relation between the specified time and watt
   */
  void addTimeWatt(long time, double watt);

  /**
   * Returns a hashMapping relation between the time and wattage for this site
   *
   * @return the hashmap that contains the relation between time and wattage
   */
  HashMap<Long, Double> relation();
}
