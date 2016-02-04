package Visual;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represnts a latitude and longitude coordinate
 */
public class LatLong {
  private double latitude;
  private double longitude;

  /**
   * Constructs a latitude and longitude from the given points.
   *
   * @param latitude  angular distance north and south of poles
   * @param longitude angular distance to measure east and west.
   */
  public LatLong(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
   * Returns the first value of the pair, the latitude
   *
   * @return the latitude
   */
  public double lat() {
    return latitude;
  }

  /**
   * Returns the second value of the pair, the longitude
   *
   * @return the longitude
   */
  public double longitude() {
    return longitude;
  }

  /**
   * Returns the Visual.LatLong value of the string
   *
   * @param s the string representation of the lat long
   * @return the Visual.LatLong representing the string information
   * @throws NumberFormatException if the string is formatted wrong
   */
  public static LatLong valueOf(String s) {
    if (s == null) {
      throw new NullPointerException();
    }
    int length = s.length();
    int comma = s.indexOf(',');
    if (length < 6 || s.charAt(0) != '[' || s.charAt(length - 1) != ']' || comma == -1) {
      throw new NumberFormatException();
    }
    String lat = s.substring(1, comma);
    String lon = s.substring(comma + 2, length - 1);

    Double latitude = Double.valueOf(lat);
    Double longitude = Double.valueOf(lon);

    return new LatLong(latitude, longitude);
  }

  /**
   * Parses the line of latLong coordinates into an array of Visual.LatLong
   *
   * @param s the line of lat long coordinates
   * @return an array of lat longs
   * @throws NumberFormatException if the string is formatted wrong
   */
  public static LatLong[] valueOfAll(String s) {
    Objects.requireNonNull(s);
    int length = s.length();
    if (length < 6) {
      throw new NumberFormatException();
    }
    String[] resultStrings = CsvReader.splitIgnoreBetween(s, '[', ']');
    ArrayList<LatLong> resultLatLongs = new ArrayList<>();
    for (String part : resultStrings) {
      if (part.charAt(0) == ' ') {
        resultLatLongs.add(valueOf(part.substring(1, part.length())));
      } else {
        resultLatLongs.add(valueOf(part));
      }
    }
    return resultLatLongs.toArray(new LatLong[resultLatLongs.size()]);
  }

  @Override
  public String toString() {
    return "[" + Double.toString(latitude) + ", " + Double.toString(longitude) + "]";
  }
}
