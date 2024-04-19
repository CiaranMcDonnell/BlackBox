package org.blackbox;

        import java.util.*;
        import javafx.scene.paint.Color;

/**
 * The HexagonManager class manages the hexagons in the game.
 * It stores the hexagons in a map for easy access and provides methods to manipulate and retrieve information about the hexagons.
 */
public class HexagonManager {

  // Directions for neighboring hexagons
  private static final int[][] DIRECTIONS = {
          {+1, -1, 0}, {+1, 0, -1}, {0, +1, -1},
          {-1, +1, 0}, {-1, 0, +1}, {0, -1, +1}
  };
  // Map to store hexagons for easy access
  private Map<String, GUI.Hexagon> hexagons;

  // Constructor initializes the hexagons map.
  public HexagonManager() {
    hexagons = new HashMap<>();
  }

  //Returns a list of neighboring locations for the given coordinates.
  public List<String> getNeighborLocations(int x, int y, int z) {
    List<String> neighbors = new ArrayList<>();
    for (int[] direction : DIRECTIONS) {
      int newX = x + direction[0];
      int newY = y + direction[1];
      int newZ = z + direction[2];
      String neighborKey = newX + "," + newY + "," + newZ;
      if (hexagons.containsKey(neighborKey)) {
        neighbors.add(neighborKey);
      }
    }
    return neighbors;
  }

  // Adds hexagon to a map settings it co-ordinates as it's key allowing for fast and easy access
  public void addHexagon(int x, int y, int z, GUI.Hexagon hexagon) {
    String key = x + "," + y + "," + z;
    hexagons.put(key, hexagon);
  }

  // Returns a set of all hexagon locations in the map.
  public Set<String> getAllHexagonLocations() {
    return hexagons.keySet();
  }

  // Get a hexagon from the map using its coordinates
  public GUI.Hexagon getHexagon(int x, int y, int z) {
    String key = x + "," + y + "," + z;
    return hexagons.get(key);
  }

  // Alter the color of a hexagon
  public void alterHexagon(int x, int y, int z, Color newColor) {
    GUI.Hexagon hex = getHexagon(x, y, z);
    if (hex != null) {
      Color transparentColor = Color.color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), 0.7);
      hex.setFill(transparentColor);
    }
  }
}
