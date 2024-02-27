package org.blackbox;

import java.util.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

/**
 * The Game class represents a single game session. It manages the game state, including the
 * locations of atoms and entry points.
 */
public class Game {
  private Map<String, List<Integer>> entryPoints;
  private List<String> atomLocations;
  private HexagonManager hexManager;
  private boolean atomsSelected;
  private GUI gui;

  /**
   * Constructs a new Game with the given HexagonManager.
   *
   * @param hexManager the HexagonManager for the game
   */
  public Game(HexagonManager hexManager) {
    this.entryPoints = new HashMap<>();
    atomLocations = new ArrayList<>();
    this.hexManager = hexManager;
    this.atomsSelected = false;
  }

  public HexagonManager getHexagonManager() {
    return this.hexManager;
  }

  /**
   * Selects the locations of the atoms using the hashmap of the hexagons which is then randomized
   * and the first six locations are added to the atomLocations list.
   */
  public void atomSelection() {
    List<String> validHexes = new ArrayList<>(hexManager.getAllHexagonLocations());
    List<String> edgeHexes =
        validHexes.stream().filter(hex -> hex.contains("4") || hex.contains("-4")).toList();
    if (validHexes.isEmpty()) {
      throw new IllegalStateException("Hexagons can not be found.");
    }
    List<String> shuffledHexes = new ArrayList<>(validHexes); // Create a new list to shuffle
    Collections.shuffle(shuffledHexes); // Shuffle the list to get random locations
    for (int i = 0; i < 6; i++) {
      atomLocations.add(shuffledHexes.get(i)); // Add the first six locations to atomLocations
    }
    Map<String, List<String>> atomNeighbors = atomsEffectiveRange();
    for (Map.Entry<String, List<String>> entry : atomNeighbors.entrySet()) {
      System.out.println(
          "Atom Location: " + entry.getKey() + ", Effective Range: " + entry.getValue());
    }

    atomsSelected = true; // ensures that only 6 atoms can be selected in any single game run
  }

  public List<String> getAtomLocations() {
    return atomLocations;
  }

  /**
   * Reveals the atoms by changing the color of the hexagons and adding a dotted circle to the GUI.
   * This method iterates over the atom locations, splits the location string into coordinates,
   * and uses these coordinates to alter the corresponding hexagon in the HexagonManager.
   * It then calculates the position of the hexagon and creates a new Circle at that position.
   * The Circle is styled and added to the GUI.
   */
  public void atomReveal() {
    for (String location : atomLocations) {
      String[] coordinates = location.split(",");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      int z = Integer.parseInt(coordinates[2]);

      // Use the coordinates to alter the corresponding hexagon in the HexagonManager
      hexManager.alterHexagon(x, y, z, Color.RED);

      // Calculate the position of the hexagon
      double posX = GUI.getHexHeight() * (x + y / 2.0) + (GUI.GUI_SIZE / 2);
      double posY = 1.5 * GUI.getHexSize() * y + (GUI.GUI_SIZE / 2);

      // Create a new Circle for the atom
      Circle atomCircle = new Circle(posX, posY, GUI.getHexHeight());
      atomCircle.setStroke(Color.BLACK);
      atomCircle.setStrokeWidth(2);
      atomCircle.setStrokeType(StrokeType.INSIDE);
      atomCircle.getStrokeDashArray().addAll(5d, 10d); // Set the stroke to a dotted pattern
      atomCircle.setFill(Color.TRANSPARENT); // Set the fill to transparent

      // Add the Circle to the root Pane
      GUI.addCircle(atomCircle);
    }
  }

  /**
   * Returns a map of the effective range of each atom.
   * The effective range is defined as the neighboring locations of each atom.
   * This method iterates over the atom locations, splits the location string into coordinates,
   * and uses these coordinates to get the neighboring locations from the HexagonManager.
   * If the coordinates are valid, the location and its neighbors are added to the map.
   *
   * @return a map where the keys are the atom locations and the values are lists of neighboring locations
   */
  public Map<String, List<String>> atomsEffectiveRange() {
    Map<String, List<String>> atomNeighbors = new HashMap<>();
    for (String location : atomLocations) {
      String[] coordinates = location.split(",");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      int z = Integer.parseInt(coordinates[2]);
      if (isValidCoordinate(x, y, z)) {
        List<String> neighbors = hexManager.getNeighborLocations(x, y, z);
        atomNeighbors.put(location, neighbors);
      }
    }
    return atomNeighbors;
  }

  private boolean isValidCoordinate(int x, int y, int z) {
    String coordinate = x + "," + y + "," + z;
    List<String> validHexes = new ArrayList<>(hexManager.getAllHexagonLocations());
    return validHexes.contains(coordinate);
  }

  /**
   * Stores the entry points for each hexagon in the grid.
   * This method iterates over all valid hexagon locations, splits each location string into coordinates,
   * and uses these coordinates to determine the entry points for each hexagon.
   * The entry points are determined based on the position of the hexagon in the grid.
   * If the hexagon is on the edge of the grid, it will have two entry points, otherwise it will have none.
   * The entry points are stored in a LinkedHashSet to ensure uniqueness, and then converted back to a List before storing.
   * The hexagon location and its entry points are then added to the entryPoints map.
   */
  public void storeEntryPoints() {
    List<String> validHexes = new ArrayList<>(hexManager.getAllHexagonLocations());
    // Second loop to assign entry points
    for (String hex : validHexes) {
      String[] coordinates = hex.split(",");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      int z = Integer.parseInt(coordinates[2]);

      // Use a Linked HashSet to ensure uniqueness
      Set<Integer> hexEntryPointsSet = new LinkedHashSet<>();
      if (x == -GUI.HIGHEST_COORDINATE) {
        hexEntryPointsSet.addAll(Arrays.asList(120, 180));
      }
      if (z == GUI.HIGHEST_COORDINATE) {
        hexEntryPointsSet.addAll(Arrays.asList(180, 240));
      }
      if (y == -GUI.HIGHEST_COORDINATE) {
        hexEntryPointsSet.addAll(Arrays.asList(240, 300));
      }
      if (x == GUI.HIGHEST_COORDINATE) {
        hexEntryPointsSet.addAll(Arrays.asList(300, 0));
      }
      if (z == -GUI.HIGHEST_COORDINATE) {
        hexEntryPointsSet.addAll(Arrays.asList(0, 60));
      }
      if (y == GUI.HIGHEST_COORDINATE) {
        hexEntryPointsSet.addAll(Arrays.asList(60, 120));
      }
      // Convert the Set back to a List before storing
      List<Integer> hexEntryPoints = new ArrayList<>(hexEntryPointsSet);
      if (!hexEntryPoints.isEmpty()) {
        entryPoints.put(hex, hexEntryPoints);
      }
    }
    for (Map.Entry<String, List<Integer>> entry : entryPoints.entrySet()) {
      System.out.println("Hex: " + entry.getKey() + ", Entry Points: " + entry.getValue());
    }
  }

  public List<Integer> getEntryPoints(String hex) {
    return entryPoints.get(hex);
  }

  public Map<String, List<Integer>> getEntryPointsMap() {
    return entryPoints;
  }
}
