package org.blackbox;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.scene.paint.Color;

public class hexagonManager {
  // Store hexagons in a map for easy access
  private Map<String, gui.Hexagon> hexagons;

  // Constructor initializes the hexagons map
  public hexagonManager() {
    hexagons = new HashMap<>();
  }

  // Adds hexagon to a map settings it co-ordinates as it's key allowing for fast and easy access
  public void addHexagon(int x, int y, int z, gui.Hexagon hexagon) {
    String key = x + "," + y + "," + z;
    hexagons.put(key, hexagon);
  }

  // Get all hexagon locations from the map
  public Set<String> getAllHexagonLocations() {
    return hexagons.keySet();
  }

  // Get a hexagon from the map using its coordinates
  public gui.Hexagon getHexagon(int x, int y, int z) {
    String key = x + "," + y + "," + z;
    return hexagons.get(key);
  }

  // Alter the color of a hexagon
  public void alterHexagon(int x, int y, int z, Color newColor) {
    gui.Hexagon hex = getHexagon(x, y, z);
    if (hex != null) {
      hex.setFill(newColor);
    }
  }
}
