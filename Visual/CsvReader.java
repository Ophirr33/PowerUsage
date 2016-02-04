package Visual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class to read the csv files needed to populate the buildings
 */
public class CsvReader {
  /**
   * Reads the building cvs file and populates the list of builders with initial variables To be
   * called first out of the reading files
   *
   * @param fileName the name of the file
   * @return the list of builders
   */
  static ArrayList<BuildingBuilder> readBuildingFile(String fileName) throws IOException {
    int bID = 0;
    int name = 1;
    int perimeter = 5;
    int area = 6;
    int footprint = 7;
    int centroid = 8;
    int outline = 9;
    BufferedReader fileReader;
    Objects.requireNonNull(fileName);
    ArrayList<BuildingBuilder> result = new ArrayList<>();
    try {
      fileReader = new BufferedReader(new FileReader(fileName));
      // Skip line
      fileReader.readLine();
      String line = fileReader.readLine();
      // Read through the file until there are no more lines to read
      while (line != null) {
        String[] cells = splitIgnoreGiven(line, '"');
        if (cells.length > 0) {
          BuildingBuilder b = new BuildingBuilder();
          b.bID(Integer.valueOf(cells[bID])).name(cells[name]).perimeter(Integer.valueOf(cells[perimeter]));
          b.area(Double.valueOf(cells[area])).footprint(Integer.valueOf(cells[footprint]));
          b.centroid(LatLong.valueOf(cells[centroid].substring(1, cells[centroid].length() - 1)));
          b.outline(LatLong.valueOfAll(cells[outline].substring(2, cells[outline].length() - 2)));
          b.currentTime(1399451400);
          result.add(b);
        }
        line = fileReader.readLine();
      }
      return result;
    } catch (IOException e) {
      System.out.println("Error in readBuildingFile !!!");
      e.printStackTrace();
    } finally {
      return result;
    }
  }

  /**
   * Reads the site file and populates the list of sites
   *
   * @param fileName the site file
   * @return the list of builders
   */
  static ArrayList<Site> readSiteFile(String fileName) throws IOException {
    int sID = 0;
    int bID = 3;
    BufferedReader fileReader;
    Objects.requireNonNull(fileName);
    ArrayList<Site> sites = new ArrayList<>();
    try {
      fileReader = new BufferedReader(new FileReader(fileName));
      // Skip header
      fileReader.readLine();
      String line;
      // Read through the file until there are no more lines to read
      while ((line = fileReader.readLine()) != null) {
        String[] cells = splitIgnoreGiven(line, '"');
        if (cells.length > 3) {
          Site site = new SiteImpl(Integer.valueOf(cells[sID]), Integer.valueOf(cells[bID]));
          sites.add(site);
        }
      }
      return sites;
    } catch (IOException e) {
      System.out.println("Error in readSiteFile !!!");
      e.printStackTrace();
    } finally {
      return sites;
    }

  }

  /**
   * Populates the given list of sites with the time to watt mappings
   *
   * @param fileName the measure file
   * @param sites    the sites to populate
   * @return the list of sites, now with mappings
   */
  static ArrayList<Site> readMeasureFile(String fileName, ArrayList<Site> sites)
          throws IOException {
    int time = 0;
    BufferedReader fileReader;
    Objects.requireNonNull(fileName);
    try {
      fileReader = new BufferedReader(new FileReader(fileName));
      // Skip header
      fileReader.readLine();
      String line;
      // read through the file until there are no more lines to read
      while ((line = fileReader.readLine()) != null) {
        String[] cells = splitIgnoreGiven(line, '"');
        if (cells.length > 0) {
          long unixTime = Long.valueOf(cells[0]);
          for (Site s : sites) {
            int cellLocation = s.siteID() + 1;
            if (cells[cellLocation].equals("")) {
              s.addTimeWatt(unixTime, 0);
            } else {
              s.addTimeWatt(unixTime, Double.valueOf(cells[cellLocation]));
            }
          }
        }
      }
      return sites;
    } catch (IOException e) {
      System.out.println("Error in readMeasureFile !!!");
      e.printStackTrace();
    } finally {
      return sites;
    }
  }

  /**
   * Builds an arraylist of buildings from the given csv files
   *
   * @return the arraylist of buildings
   */
  public static ArrayList<Building> build(String buildingName, String siteName, String
          measureName) throws IOException {
    ArrayList<BuildingBuilder> initialBuildings =
            readBuildingFile(buildingName);
    ArrayList<Site> initialSites = readSiteFile(siteName);
    ArrayList<Site> linkedSites = readMeasureFile(measureName, initialSites);
    linkSitesToBuildings(linkedSites, initialBuildings);
    ArrayList<Building> result = new ArrayList<>(initialBuildings.size());
    for (BuildingBuilder builder : initialBuildings) {
      result.add(builder.build());
    }
    return result;
  }

  /**
   * Links sites with to their specified builders
   *
   * @param sites    the sites
   * @param builders the builders
   */
  static void linkSitesToBuildings(ArrayList<Site> sites,
                                   ArrayList<BuildingBuilder> builders) {
    for (BuildingBuilder b : builders) {
      ArrayList<Site> group = new ArrayList<>();
      for (Site s : sites) {
        if (s.buildingID() == b.identify()) {
          group.add(s);
        }
      }
      b.sites(group.toArray(new Site[group.size()]));
    }
  }


  /**
   * Splits a string at commas, ignores commas within quotations
   *
   * @return an array of strings that have been split.
   */
  public static String[] splitIgnoreGiven(String s, char toIgnore) {
    Objects.requireNonNull(s);
    int length = s.length();
    if (length <= 1) {
      return new String[]{s};
    }
    ArrayList<String> result = new ArrayList<>();
    int delimeter = 0;
    boolean visited = false;
    for (int i = 0; i < length; i += 1) {
      if (s.charAt(i) == toIgnore && !(i == length - 1)) {
        visited = !visited;
      } else if (s.charAt(i) == ',' && i == length - 1 && !visited) {
        result.add(s.substring(delimeter, i));
        result.add("");
      } else if (i == length - 1) {
        result.add(s.substring(delimeter, length));
      } else if (s.charAt(i) == ',' && !visited) {
        result.add(s.substring(delimeter, i));
        delimeter = i + 1;
      }
    }
    return result.toArray(new String[result.size()]);
  }

  /**
   * Splits the string at commas, ignoring the values between the opening and closing char
   *
   * @param s      the string to split
   * @param opener the opening character
   * @param closer the closing character
   * @return the array containing the split strings.
   */
  public static String[] splitIgnoreBetween(String s, char opener, char closer) {
    Objects.requireNonNull(s);
    if (opener == closer) {
      return splitIgnoreGiven(s, opener);
    }
    int length = s.length();
    if (length <= 1) {
      return new String[]{s};
    }
    ArrayList<String> result = new ArrayList<>();
    int delimeter = 0;
    boolean visited = false;
    for (int i = 0; i < length; i++) {
      if (s.charAt(i) == opener && !(i == length - 1)) {
        visited = true;
      } else if (s.charAt(i) == closer && !(i == length - 1)) {
        visited = false;
      } else if (s.charAt(i) == ',' && i == length - 1 && !visited) {
        result.add(s.substring(delimeter, i));
        result.add("");
      } else if (i == length - 1) {
        result.add(s.substring(delimeter, length));
      } else if (s.charAt(i) == ',' && !visited) {
        result.add(s.substring(delimeter, i));
        delimeter = i + 1;
      }
    }
    return result.toArray(new String[result.size()]);
  }
}
