package Visual;

import java.util.HashMap;

/**
 * Represents a site meter
 */
public class SiteImpl implements Site {
  private HashMap<Long, Double> timeToWatts;
  private int siteID;
  private int buildingID;

  public SiteImpl(int siteID, int buildingID) {
    this.siteID = siteID;
    this.buildingID = buildingID;
    timeToWatts = new HashMap<>();
  }

  @Override
  public double wattage(long time) {
    long roundedTime = roundToNearestTime(1386547200, 1418168700, 900, time);
    return timeToWatts.get(roundedTime);
  }

  @Override
  public int siteID() {
    return siteID;
  }

  @Override
  public int buildingID() {
    return buildingID;
  }

  @Override
  public void addTimeWatt(long time, double watt) {
    if (timeToWatts.get(time) != null) {
      throw new IllegalStateException("Time already set with wattage !!!");
    }
    timeToWatts.put(time, watt);
  }

  @Override
  public String toString() {
    return "[sID: " + Integer.toString(siteID) + " bID: " + Integer.toString(buildingID) + "]";
  }

  @Override
  public HashMap<Long, Double> relation() {
    return timeToWatts;
  }

  /**
   * Rounds the given int into the given range by the given difference
   *
   * @param low     the lower boundary of the range
   * @param high    the higher boundary of the range
   * @param diff    the difference
   * @param toRound the number to round
   * @return the rounded number
   */
  public static long roundToNearestTime(long low, long high, long diff, long toRound) {
    if (low >= high || diff >= high - low) {
      throw new IllegalArgumentException("Improper range or difference");
    }
    if (toRound <= low) {
      return low;
    } else if (toRound >= high) {
      return high;
    } else if ((toRound - low) % diff == 0) {
      return toRound;
    } else if ((toRound - low) % diff > diff / 2) {
      return toRound + (diff - ((toRound - low) % diff));
    } else {
      return toRound - ((toRound - low) % diff);
    }
  }
}
