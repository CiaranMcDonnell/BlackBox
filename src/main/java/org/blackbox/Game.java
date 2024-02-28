package org.blackbox;

import java.util.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Pair;

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
    Map<Integer, Pair<Integer, String>> entryPointMap = new HashMap<>();
    int counter = 1;

    for (String hex : validHexes) {
      String[] coordinates = hex.split(",");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      int z = Integer.parseInt(coordinates[2]);

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

      List<Integer> hexEntryPoints = new ArrayList<>(hexEntryPointsSet);
      if (!hexEntryPoints.isEmpty()) {
        entryPoints.put(hex, hexEntryPoints);
      }

      for (Integer point : hexEntryPoints) {
        Pair<Integer, String> degreeHexPair = new Pair<>(point, hex);
        entryPointMap.put(counter, degreeHexPair);
        counter++;
      }
    }

    // Print the entryPointMap to the console
    for (Map.Entry<Integer, Pair<Integer, String>> entry : entryPointMap.entrySet()) {
      System.out.println("Entry Point: " + entry.getKey() + ", Degree and Hex Location: " + entry.getValue());
    }
  }

  public List<Integer> getEntryPoints(String hex) {
    return entryPoints.get(hex);
  }

  public Map<String, List<Integer>> getEntryPointsMap() {
    return entryPoints;
  }

//  public void rayShooting(){
//
//
//  }










//  public void displayEntryPoints() {
//    int counter = 1;
//
//    Comparator<String> comparator = (o1, o2) -> {
//      String[] coordinates1 = o1.split(",");
//      String[] coordinates2 = o2.split(",");
//      int z1 = Integer.parseInt(coordinates1[2]);
//      int x1 = Integer.parseInt(coordinates1[0]);
//      int y1 = Integer.parseInt(coordinates1[1]);
//      int z2 = Integer.parseInt(coordinates2[2]);
//      int x2 = Integer.parseInt(coordinates2[0]);
//      int y2 = Integer.parseInt(coordinates2[1]);
//
//      // Compare based on the order: maxvalue z -> maxvalue x -> -maxvalue y -> -maxvalue z -> maxvalue x -> -maxvalue y
//      if (z1 != z2) {
//        return (z2 == GUI.HIGHEST_COORDINATE ? 1 : -1);
//      } else if (x1 != x2) {
//        return (x2 == GUI.HIGHEST_COORDINATE ? 1 : -1);
//      } else if (y1 != y2) {
//        return (y1 == -GUI.HIGHEST_COORDINATE ? 1 : -1);
//      } else if (z1 != z2) {
//        return (z1 == -GUI.HIGHEST_COORDINATE ? 1 : -1);
//      } else if (x1 != x2) {
//        return (x1 == GUI.HIGHEST_COORDINATE ? 1 : -1);
//      } else {
//        return (y2 == -GUI.HIGHEST_COORDINATE ? 1 : -1);
//      }
//    };
//
//    // Create a TreeMap with the entryPoints map to sort it
//    Map<String, List<Integer>> sortedEntryPoints = new TreeMap<>(comparator);
//    sortedEntryPoints.putAll(entryPoints);
//
//    // Create a map to store the entry point, degree and hex location
//    Map<String, Map<Integer, String>> entryPointMap = new HashMap<>();
//
//    for (Map.Entry<String, List<Integer>> entry : sortedEntryPoints.entrySet()) {
//      String[] coordinates = entry.getKey().split(",");
//      int x = Integer.parseInt(coordinates[0]);
//      int y = Integer.parseInt(coordinates[1]);
//      int z = Integer.parseInt(coordinates[2]);
//
//      // Calculate the position of the hexagon
//      double posX = GUI.getHexHeight() * (x + y / 2.0) + (GUI.GUI_SIZE / 2);
//      double posY = 1.5 * GUI.getHexSize() * y + (GUI.GUI_SIZE / 2);
//
//      // Adjust the offset factor based on the number of entry points
//      double offsetFactor = 1.2; // Decrease this value to bring the labels closer to the edge
//
//      for (Integer point : entry.getValue()) {
//        // Convert the entry point from degrees to radians
//        double angle = Math.toRadians(point);
//
//        // Calculate the position of the label
//        double labelPosX = posX + offsetFactor * GUI.getHexSize() * Math.cos(angle);
//        double labelPosY = posY + offsetFactor * GUI.getHexSize() * Math.sin(angle);
//
//        // Create a new Label for the entry point
//        javafx.scene.control.Label entryPointLabel = new javafx.scene.control.Label(String.valueOf(counter));
//        entryPointLabel.setLayoutX(labelPosX);
//        entryPointLabel.setLayoutY(labelPosY);
//
//        // Add the Label to the root Pane
//        GUI.addLabel(entryPointLabel);
//
//        Map<Integer, String> degreeHexMap = new HashMap<>();
//
//        // Add the degree and hex location to the map
//        degreeHexMap.put(point, entry.getKey());
//
//        entryPointMap.put(String.valueOf(counter), degreeHexMap);
//
//        counter++;
//      }
//    }
//
//    // Print the entryPointMap to the console
//    for (Map.Entry<String, Map<Integer, String>> entry : entryPointMap.entrySet()) {
//      System.out.println("Entry Point: " + entry.getKey() + ", Degree and Hex Location: " + entry.getValue());
//    }
  }

