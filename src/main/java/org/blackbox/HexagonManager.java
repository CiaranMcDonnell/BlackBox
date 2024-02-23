package org.blackbox;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.scene.paint.Color;

public class HexagonManager {
  // Store hexagons in a map for easy access
  private Map<String, GUI.Hexagon> hexagons;

  // Constructor initializes the hexagons map
  public HexagonManager() {
    hexagons = new HashMap<>();
  }

  // Adds hexagon to a map settings it co-ordinates as it's key allowing for fast and easy access
  public void addHexagon(int x, int y, int z, GUI.Hexagon hexagon) {
    String key = x + "," + y + "," + z;
    hexagons.put(key, hexagon);
  }

  // Get all hexagon locations from the map
  public Set<String> getAllHexagonLocations() {
    return hexagons.keySet();
  }

  // returns a list of all the hexagons from the map for unit testing
  public Collection<GUI.Hexagon> getAllHexagons() {
    return hexagons.values();
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
      hex.setFill(newColor);
    }
  }
}
