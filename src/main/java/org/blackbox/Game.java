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
   * This method iterates over the atom locations, splits the location string into coordinates, and
   * uses these coordinates to alter the corresponding hexagon in the HexagonManager. It then
   * calculates the position of the hexagon and creates a new Circle at that position. The Circle is
   * styled and added to the GUI.
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
      atomCircle.setStroke(Color.WHITE);
      atomCircle.setStrokeWidth(2);
      atomCircle.setStrokeType(StrokeType.INSIDE);
      atomCircle.getStrokeDashArray().addAll(5d, 10d); // Set the stroke to a dotted pattern
      atomCircle.setFill(Color.TRANSPARENT); // Set the fill to transparent

      // Add the Circle to the root Pane
      GUI.addCircle(atomCircle);
    }
  }

  /**
   * Returns a map of the effective range of each atom. The effective range is defined as the
   * neighboring locations of each atom. This method iterates over the atom locations, splits the
   * location string into coordinates, and uses these coordinates to get the neighboring locations
   * from the HexagonManager. If the coordinates are valid, the location and its neighbors are added
   * to the map.
   *
   * @return a map where the keys are the atom locations and the values are lists of neighboring
   * locations
   */
  public Map<String, List<String>> atomsEffectiveRange() {
    Map<String, List<String>> atomNeighbors = new HashMap<>();
    for (String location : atomLocations) {
      String[] coordinates = location.split(",");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      int z = Integer.parseInt(coordinates[2]);
      List<String> neighbors = hexManager.getNeighborLocations(x, y, z);

      if (isValidCoordinate(x, y, z)) {
        atomNeighbors.put(location, neighbors);
      }
      System.out.println("Neighbors of " + location + ": " + neighbors);
    }
    return atomNeighbors;
  }

  private boolean isValidCoordinate(int x, int y, int z) {
    String coordinate = x + "," + y + "," + z;
    List<String> validHexes = new ArrayList<>(hexManager.getAllHexagonLocations());
    return validHexes.contains(coordinate);
  }

  /**
   * Stores the entry points for each hexagon in the grid. This method iterates over all valid
   * hexagon locations, splits each location string into coordinates, and uses these coordinates to
   * determine the entry points for each hexagon. The entry points are determined based on the
   * position of the hexagon in the grid. If the hexagon is on the edge of the grid, it will have
   * two entry points, otherwise it will have none. The entry points are stored in a LinkedHashSet
   * to ensure uniqueness, and then converted back to a List before storing. The hexagon location
   * and its entry points are then added to the entryPoints map.
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
      System.out.println(
              "Entry Point: " + entry.getKey() + ", Degree and Hex Location: " + entry.getValue());
    }
  }

  public List<Integer> getEntryPoints(String hex) {
    return entryPoints.get(hex);
  }

  public Map<String, List<Integer>> getEntryPointsMap() {
    return entryPoints;
  }

  public void handleButtonClick(ButtonData buttonData) {
    String originHex = buttonData.hex();
    int degree = buttonData.degree();
    traversalRules(originHex, degree);
  }

  public void traversalRules(String originHex, int degree) {
    String direction = null;
    Map<Integer, String> degreeToDirectionMap = new HashMap<>();
    // Implement traversal rules for degree 300
    direction =
            switch (degree) {
              case 0 -> "-1, 0, +1"; // Implement traversal rules for degree 0
              case 60 -> "0, -1, +1"; // Implement traversal rules for degree 60
              case 120 -> "+1, -1, 0"; // Implement traversal rules for degree 120
              case 180 -> "+1, 0, -1"; // Implement traversal rules for degree 180
              case 240 -> "0, +1, -1"; // Implement traversal rules for degree 240
              case 300 -> "-1, +1, 0";
              default -> direction;
            };
    if (direction == null) {
      throw new IllegalArgumentException("Invalid degree: " + originHex);
    }
    collisionDetection(originHex, direction);
  }

  public void collisionDetection(String originHex, String direction) {
    String[] coordinates = originHex.split(",");
    int x = Integer.parseInt(coordinates[0]);
    int y = Integer.parseInt(coordinates[1]);
    int z = Integer.parseInt(coordinates[2]);

    System.out.println("Origin Hex: " + originHex);
    System.out.println("Direction: " + direction);

    int[] directionValues = directionSelection(direction);
    int dx = directionValues[0];
    int dy = directionValues[1];
    int dz = directionValues[2];

    Map<String, List<String>> atomNeighbors = atomsEffectiveRange();
    if (atomLocations.contains(originHex)
            || atomNeighbors.values().stream().anyMatch(neighbors -> neighbors.contains(originHex))) {
      System.out.println("Collision detected at origin: " + originHex);
      atomEncounter(originHex, direction, originHex);
      hexManager.alterHexagon(x, y, z, Color.WHITE);
      return;
    }

    while (x >= -4 && x <= 4 && y >= -4 && y <= 4 && z >= -4 && z <= 4) {
      String currentHex = x + "," + y + "," + z;
      System.out.println("Current Hex: " + currentHex);

      // Update atomNeighbors map in each iteration

      if (atomLocations.contains(currentHex)) {
        System.out.println("Collision detected at: " + currentHex + " in atomLocations");
        atomEncounter(originHex, direction, currentHex);
        return;
      }
      // Check if any of the lists of neighbors contain the current hex
      if (atomNeighbors.values().stream().anyMatch(neighbors -> neighbors.contains(currentHex))) {
        System.out.println("Collision detected at: " + currentHex + " in atomNeighbors");
        atomEncounter(originHex, direction, currentHex);
        return;
      }
      x += dx;
      y += dy;
      z += dz;
    }
    System.out.println("No collision detected");
    noAtomEncounter(originHex, direction); // No collision detected
    return;
  }

  public void noAtomEncounter(String originHex, String direction) {
    // Extract the x, y, and z coordinates from the originHex string
    String[] coordinates = originHex.split(",");
    int x = Integer.parseInt(coordinates[0]);
    int y = Integer.parseInt(coordinates[1]);
    int z = Integer.parseInt(coordinates[2]);
    hexManager.alterHexagon(x, y, z, Color.PURPLE);
    // Calculate the direction to move in
    int[] directionValues = directionSelection(direction);
    int dx = directionValues[0];
    int dy = directionValues[1];
    int dz = directionValues[2];

    // Move in the direction until you reach the edge of the grid
    while (x >= -4 && x <= 4 && y >= -4 && y <= 4 && z >= -4 && z <= 4) {
      // Calculate potential new coordinates
      int potentialX = x + dx;
      int potentialY = y + dy;
      int potentialZ = z + dz;

      // Only update x, y, and z if the new coordinates are valid
      if (isValidCoordinate(potentialX, potentialY, potentialZ)) {
        x = potentialX;
        y = potentialY;
        z = potentialZ;
      } else {
        // If the new coordinates are not valid, break the loop
        break;
      }
    }
    hexManager.alterHexagon(x, y, z, Color.PURPLE);
    // The x, y, and z coordinates of the last hexagon in the traversal are now stored in x, y, and
    // z
    System.out.println("Last hexagon in traversal: " + x + "," + y + "," + z);
  }

  public void atomEncounter(String originHex, String direction, String currentHex) {
    // Split the origin coordinates
    String[] originCoordinates = originHex.split(",");
    int originX = Integer.parseInt(originCoordinates[0]);
    int originY = Integer.parseInt(originCoordinates[1]);
    int originZ = Integer.parseInt(originCoordinates[2]);

    // Split the current coordinates
    String[] currentCoordinates = currentHex.split(",");
    int currentX = Integer.parseInt(currentCoordinates[0]);
    int currentY = Integer.parseInt(currentCoordinates[1]);
    int currentZ = Integer.parseInt(currentCoordinates[2]);

    // Get the direction values
    int[] directionValues = directionSelection(direction);
    int dx = directionValues[0];
    int dy = directionValues[1];
    int dz = directionValues[2];

    // Calculate the next position
    int nextX = currentX + dx;
    int nextY = currentY + dy;
    int nextZ = currentZ + dz;

    String nextPos = nextX + "," + nextY + "," + nextZ;

    // Check if the next position is in the atom locations
    if (atomLocations.contains(nextPos)) {
      directHit(originX, originY, originZ);
    }
    nextPos = nextX + "," + nextY + "," + nextZ;
    else if (atomLocations.contains(nextPos)) {
      deflectionHit(originHex, direction, currentHex);
    }

  }

  public void directHit(int x, int y, int z){
    hexManager.alterHexagon(x, y, z, Color.GREEN);
  }
  public void deflectionHit(String originHex, String direction, String currentHex){

  }
  public int[] directionSelection(String direction){
    // Implement direction selection logic
    int dx = 0, dy = 0, dz = 0;
    switch (direction) {
      case "-1, 0, +1":
        dx = -1;
        dz = 1;
        break;
      case "0, -1, +1":
        dy = -1;
        dz = 1;
        break;
      case "+1, -1, 0":
        dx = 1;
        dy = -1;
        break;
      case "+1, 0, -1":
        dx = 1;
        dz = -1;
        break;
      case "0, +1, -1":
        dy = 1;
        dz = -1;
        break;
      case "-1, +1, 0":
        dx = -1;
        dy = 1;
        break;
    }
    return new int[]{dx, dy, dz};
  }
}

