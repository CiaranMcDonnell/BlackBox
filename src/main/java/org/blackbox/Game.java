package org.blackbox;

import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Pair;

/**
 * The Game class represents a single game session. It manages the game state, including the
 * locations of atoms and entry points.
 */
public class Game {
  private final Map<String, List<Integer>> entryPoints;
  public int raysShot = 0, atomsGuesses = 0, atomsHit = 0, atomsMissed;
  private int score;
  private int deflectionCounter = 0;
  private final List<String> atomLocations;
  private final HexagonManager hexManager;
  private final GUI gui;
  private final List<String> ignoredAtoms;
  private String storedOriginHex;
  public enum EncounterType {
    NO_ENCOUNTER,
    DIRECT_HIT,
    DEFLECTION,
    DOUBLE_HIT
  }
  public EncounterType lastEncounterType = EncounterType.NO_ENCOUNTER;
  /**
   * Constructs a new Game with the given HexagonManager.
   *
   * @param hexManager the HexagonManager for the game
   */
  public Game(HexagonManager hexManager, GUI gui) {
    this.gui = gui;
    this.entryPoints = new HashMap<>();
    atomLocations = new ArrayList<>();
    this.hexManager = hexManager;
    this.ignoredAtoms = new ArrayList<>();
  }

  /**
   * Selects the locations of the atoms using the hashmap of the hexagons which is then randomized
   * and the first six locations are added to the atomLocations list.
   */
  public void atomSelection() {
    List<String> validHexes = new ArrayList<>(hexManager.getAllHexagonLocations());
    if (validHexes.isEmpty()) {
      throw new IllegalStateException("Hexagons can not be found.");
    }
    List<String> shuffledHexes = new ArrayList<>(validHexes); // Create a new list to shuffle
    Collections.shuffle(shuffledHexes); // Shuffle the list to get random locations
    for (int i = 0; i < 6; i++) {
      atomLocations.add(shuffledHexes.get(i)); // Add the first six locations to atomLocations
    }
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
   *     locations
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
    lastEncounterType = EncounterType.NO_ENCOUNTER;
    deflectionCounter = 0;
    String originHex = buttonData.hex();
    storedOriginHex = originHex;
    int degree = buttonData.degree();
    traversalRules(originHex, degree);
  }

  public void traversalRules(String originHex, int degree) {
    String direction = null;
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
    collisionDetection(originHex, direction, true);
  }
  public void handleEncounter(EncounterType encounterType) {
    lastEncounterType = encounterType; // Store the last encounter type

    switch (encounterType) {
      case DEFLECTION:
        // Handle deflection
        break;
      case DIRECT_HIT:
        // Handle direct hit
        break;
      case NO_ENCOUNTER:
      default:
        // Handle no encounter
        break;
    }
  }

  public void collisionDetection(String startingHex, String direction, boolean originCheck) {
    String[] coordinates = startingHex.split(",");
    int x = Integer.parseInt(coordinates[0]);
    int y = Integer.parseInt(coordinates[1]);
    int z = Integer.parseInt(coordinates[2]);

    System.out.println("Origin Hex: " + startingHex);
    System.out.println("Direction: " + direction);

    int[] directionValues = directionSelection(direction);
    int dx = directionValues[0];
    int dy = directionValues[1];
    int dz = directionValues[2];

    Map<String, List<String>> atomNeighbors = atomsEffectiveRange();
    if (atomLocations.contains(storedOriginHex)
        || atomNeighbors.values().stream().anyMatch(neighbors -> neighbors.contains(storedOriginHex))
            && getEntryPointsMap().containsKey(storedOriginHex) && originCheck) {
      System.out.println("Collision detected at origin: " + startingHex);
      hexManager.alterHexagon(x, y, z, Color.WHITE);
      return;
    }

    Polyline polyline = new Polyline();
    polyline.setStrokeWidth(3);
    polyline.setStroke(Color.CYAN);

    while (x >= -GUI.HIGHEST_COORDINATE && x <= GUI.HIGHEST_COORDINATE && y >= -GUI.HIGHEST_COORDINATE && y <= GUI.HIGHEST_COORDINATE
            && z >= -GUI.HIGHEST_COORDINATE && z <= GUI.HIGHEST_COORDINATE) {
      String currentHex = x + "," + y + "," + z;
      System.out.println("Current Hex: " + currentHex);
      double currentXCenter = GUI.getHexHeight() * (x + y / 2.0) + (GUI.GUI_SIZE / 2);
      double currentYCenter = 1.5 * GUI.getHexSize() * y + (GUI.GUI_SIZE / 2);
      // Add the current hexagon's center to the Polyline
      polyline.getPoints().addAll(currentXCenter, currentYCenter);

      // Update atomNeighbors map in each iteration
      if (ignoredAtoms.contains(currentHex)) {
        x += dx;
        y += dy;
        z += dz;
        ignoredAtoms.remove(currentHex);
        continue;
      }
      // Check if any of the lists of neighbors contain the current hex
      for (Map.Entry<String, List<String>> entry : atomNeighbors.entrySet()) {
        if (entry.getValue().contains(currentHex)) {
          String specificAtomNeighbor = entry.getKey();
          System.out.println("Collision detected at: " + currentHex + " in atomNeighbors");
          atomEncounter(startingHex, direction, currentHex, specificAtomNeighbor);
          GUI.polylinePane.getChildren().add(polyline);
          return;
        }
      }
      x += dx;
      y += dy;
      z += dz;
    }
    GUI.polylinePane.getChildren().add(polyline);
    System.out.println("No collision detected");
    noAtomEncounter(startingHex, direction); // No collision detected
  }

  public void noAtomEncounter(String originHex, String direction) {
    // Extract the x, y, and z coordinates from the originHex string
    String[] coordinates = originHex.split(",");
    int originX = Integer.parseInt(coordinates[0]);
    int originY = Integer.parseInt(coordinates[1]);
    int originZ = Integer.parseInt(coordinates[2]);
    // Calculate the direction to move in
    int[] directionValues = directionSelection(direction);
    int dx = directionValues[0];
    int dy = directionValues[1];
    int dz = directionValues[2];

    int[] lastHex = findLastHex(originX, originY, originZ, dx, dy, dz);
    int lastX = lastHex[0];
    int lastY = lastHex[1];
    int lastZ = lastHex[2];
    if(EncounterType.NO_ENCOUNTER == lastEncounterType){
      hexDisplayer(storedOriginHex, lastX, lastY, lastZ, direction);
    }

    // The x, y, and z coordinates of the last hexagon in the traversal are now stored in x, y, and
    // z
    System.out.println("Last hexagon in traversal: " + lastX + "," + lastY + "," + lastZ);
    hexDisplayer(storedOriginHex, lastX, lastY, lastZ, direction);
  }

  public void atomEncounter(String originHex, String direction, String currentHex, String specificAtom) {

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
    String deflectionPosLowerCornerRight = (currentX) + "," + (currentY -1) + "," + (currentZ + 1);
    String deflectionPosLowerCornerLeft = (currentX + 1) + "," + (currentY - 1) + "," + currentZ;
    String deflectionPosUpperCornerRight = (currentX - 1) + "," + (currentY + 1) + "," + (currentZ);
    String deflectionPosUpperCornerLeft = (currentX) + "," + (currentY + 1) + "," + (currentZ - 1);
    String deflectionPosRight = (currentX + 1) + "," + currentY + "," + (currentZ - 1);
    String deflectionPosLeft = (currentX - 1) + "," + currentY + "," + (currentZ + 1);
    //ignoredAtoms.clear();
    System.out.println("Specific Atom = " + specificAtom);
    // Check if the hex is within 2 or more atom Neighbors
    if(isInMultipleNeighbors(currentHex) ){
      System.out.println("Two Atoms approached, Reflection");
      if(atomLocations.contains(deflectionPosLeft)){
          doubleAtomHit(direction, currentHex, originHex, atomLocations.contains(deflectionPosUpperCornerRight) || atomLocations.contains(deflectionPosUpperCornerLeft), false, true);
      }
      else doubleAtomHit(direction, currentHex, originHex, atomLocations.contains(deflectionPosUpperCornerRight) || atomLocations.contains(deflectionPosUpperCornerLeft), atomLocations.contains(deflectionPosRight), false);
    }
    // Check if the next position is in the atom locations
    else if(atomLocations.contains(nextPos)) {
      directHit(storedOriginHex);
      System.out.println("Direct Hit: " + nextPos);
    }
    // lower deflection
    else if(specificAtom.equals(deflectionPosRight)){
      System.out.println("Direct Right Deflection");
      deflectionHit(direction, currentHex, originHex, dx != 1, true);
    }
    else if(specificAtom.equals(deflectionPosLeft)){
      System.out.println("Direct Left Deflection");
      deflectionHit(direction, currentHex, originHex, dx != 1, true);
    }
    else if (specificAtom.equals(deflectionPosLowerCornerLeft) || specificAtom.equals(deflectionPosLowerCornerRight)) {
      System.out.println("Lower Deflection");
      boolean upper = false;
      deflectionHit(direction, currentHex, originHex, upper, false);
    }
    // upper deflection
    else if (specificAtom.equals(deflectionPosUpperCornerRight) || specificAtom.equals(deflectionPosUpperCornerLeft)) {
      System.out.println("Upper Deflection");
      boolean upper = true;
      deflectionHit(direction, currentHex, originHex, upper, false);
    }
    else {
      System.out.println("No collision detected");
      noAtomEncounter(originHex, direction);
    }
  }

  public void directHit(String storedOriginHex) {
    //handleEncounter(EncounterType.DIRECT_HIT);
    String[] coordinates = storedOriginHex.split(",");
    int x = Integer.parseInt(coordinates[0]);
    int y = Integer.parseInt(coordinates[1]);
    int z = Integer.parseInt(coordinates[2]);
    hexManager.alterHexagon(x, y, z, Color.GREEN);
  }

  // Temporary Implementation of Deflection, collision after deflection not yet implemented.
  public void deflectionHit(String direction, String currentHex, String originHex, boolean upper, boolean side) {
    deflectionCounter++;
    handleEncounter(EncounterType.DEFLECTION);
    // Calculate the direction to move in
    ignoredAtoms.add(currentHex);
    if(side){
        System.out.println("Normal Side true");
        System.out.println("Pre Direction: " + direction);
        direction =
                switch (direction) {
                    case "-1, 0, +1", "-1, +1, 0" ->
                            "0, +1, -1"; // Implement traversal rules for side deflection
                    case "0, -1, +1" ->
                            "+1, -1, 0"; // Implement traversal rules for side deflection
                    case "+1, -1, 0" ->
                            "0, -1, +1"; // Implement traversal rules for side deflection
                    case "+1, 0, -1", "0, +1, -1" ->
                            "-1, +1, 0"; // Implement traversal rules for side deflection
                    case "0, +1 , -1" ->
                            "-1, 0, +1"; // Implement traversal rules for side deflection
                    default -> direction;
                };
    }
    else if (upper) {
      System.out.println("Upper true");
      System.out.println("Pre Direction: " + direction);
      direction =
              switch (direction) {
                case "0, -1, +1" ->
                        "-1, +1, 0"; // Implement traversal rules for upper deflection
                case "+1, -1, 0", "0, +1, -1" ->
                        "+1, 0, -1"; // Implement traversal rules for upper deflection
                case "-1, 0, +1" ->
                        "0, -1, +1"; // Implement traversal rules for upper deflection
                case "+1, 0, -1" ->
                        "+1, -1, 0"; // Implement traversal rules for upper deflection
                case "0, +1 , -1" ->
                        "+1, +1, 0"; // Implement traversal rules for upper deflection
                  case "-1, +1, 0" ->
                        "-1, 0, +1"; // Implement traversal rules for upper deflection
                default -> direction;
              };
    } else {
      System.out.println("Upper false");
      System.out.println("Pre Direction: " + direction);
      direction =
              switch (direction) {
                case "+1, 0, -1" ->
                        "0, +1, -1"; // Implement traversal rules for lower deflection
                case "0, +1, -1", "0, -1, +1" ->
                        "-1, 0, +1"; // Implement traversal rules for lower deflection
                case "-1, +1, 0" ->
                        "+1, -1, 0"; // Implement traversal rules for lower deflection
                case "-1, 0, +1" ->
                        "-1, +1, 0"; // Implement traversal rules for lower deflection
                case "+1, -1, 0" ->
                        "+1, 0, -1"; // Implement traversal rules for lower deflection
                  default -> direction;
              };
    }

    System.out.println("Direction: " + direction);
    collisionDetection(currentHex, direction, false);
  }

  public void doubleAtomHit(String direction, String currentHex, String originHex, Boolean upper, Boolean leftSide, Boolean rightSide){
    handleEncounter(EncounterType.DOUBLE_HIT);
    // Calculate the direction to move in
    if(reversalChecker(currentHex, direction)){
        System.out.println("Reversal Detected");
        String[] coordinatesOrigin = storedOriginHex.split(",");
        int originX = Integer.parseInt(coordinatesOrigin[0]);
        int originY = Integer.parseInt(coordinatesOrigin[1]);
        int originZ = Integer.parseInt(coordinatesOrigin[2]);
        hexManager.alterHexagon(originX, originY, originZ, Color.YELLOW);
        return;
    }

    ignoredAtoms.add(currentHex);
    System.out.println("Pre Direction: " + direction);
    if(leftSide && upper){
        System.out.print("Left Side true upper true");
        direction =
                switch (direction) {
                    case "-1, 0, +1" ->
                            "+1, -1, 0"; // Implement traversal rules for 120 degree change
                    case "0, -1, +1", "-1, +1, 0" ->
                            "+1, 0, -1"; // Implement traversal rules for 120 degree change
                    case "+1, -1, 0", "0, +1, -1" ->
                            "-1, 0, +1"; // Implement traversal rules for 120 degree change
                    case "+1, 0, -1" ->
                            "0, -1, +1"; // Implement traversal rules for 120 degree change
                    default -> direction;
                };
    }
    else if(rightSide && upper){
        System.out.print("Right Side true upper true");
        direction =
                switch (direction) {
                  case "-1, 0, +1" -> "0, +1, -1"; // Implement traversal rules for 120 degree change
                  case "0, -1, +1" -> "+1, 0, -1"; // Implement traversal rules for 120 degree change
                  case "+1, -1, 0", "0, +1 , -1" -> "-1, 0, +1"; // Implement traversal rules for 120 degree change
                  case "+1, 0, -1" -> "-1, +1, 0"; // Implement traversal rules for 120 degree change
                    case "0, +1, -1" -> "+1, -1, 0"; // Implement traversal rules for 120 degree change
                  case "-1, +1, 0" -> "+1, 0, -1"; // Implement traversal rules for 120 degree
                    default -> direction;
                };
    }
    else if(leftSide && !upper){
        System.out.print("Left Side true");
        direction =
                switch (direction) {
                    case "-1, 0, +1" ->
                            "0, +1, -1"; // Implement traversal rules for 120 degree change
                    case "0, -1, +1" ->
                            "+1, 0, -1"; // Implement traversal rules for 120 degree change
                    case "+1, -1, 0", "0, +1 , -1" ->
                            "-1, 0, +1"; // Implement traversal rules for 120 degree change
                    case "+1, 0, -1" ->
                            "-1, +1, 0"; // Implement traversal rules for 120 degree change
                    case "0, +1, -1", "-1, +1, 0" ->
                            "+1, -1, 0"; // Implement traversal rules for 120 degree change
                    default -> direction;
                };
    }
    else if(rightSide && !upper){
        System.out.print("Right Side true");
        direction =
                switch (direction) {
                    case "-1, 0, +1" ->
                            "0, +1, -1"; // Implement traversal rules for 120 degree change
                    case "0, -1, +1" ->
                            "+1, 0, -1"; // Implement traversal rules for 120 degree change
                    case "+1, -1, 0", "0, +1 , -1" ->
                            "-1, 0, +1"; // Implement traversal rules for 120 degree change
                    case "+1, 0, -1" ->
                            "-1, +1, 0"; // Implement traversal rules for 120 degree change
                    case "0, +1, -1", "-1, +1, 0" ->
                            "+1, -1, 0"; // Implement traversal rules for 120 degree change
                    default -> direction;
                };
    }
    else if(upper){
      System.out.print("Upper true");
      direction =
              switch (direction) {
                case "-1, 0, +1", "0, +1, -1" ->
                        "+1, -1, 0"; // Implement traversal rules for 120 degree change
                case "0, -1, +1", "+1, -1, 0" ->
                        "0, +1, -1"; // Implement traversal rules for 120 degree change
                  case "+1, 0, -1", "-1, +1, 0" ->
                        "0, -1, +1"; // Implement traversal rules for 120 degree change
                case "0, +1 , -1" ->
                        "-1, 0, +1"; // Implement traversal rules for 120 degree change
                  default -> direction;
              };
    }
    else{
      System.out.print("Upper false\n");
      direction =
              switch (direction) {
                case "-1, 0, +1", "+1, -1, 0" ->
                        "0, +1, -1"; // Implement traversal rules for 120 degree change
                case "0, -1, +1", "+1, 0, -1" ->
                        "-1, +1, 0"; // Implement traversal rules for 120 degree change
                  case "0, +1 , -1", "0, +1, -1" ->
                        "-1, 0, +1"; // Implement traversal rules for 120 degree change
                  case "-1, +1, 0" ->
                        "0, -1, +1"; // Implement traversal rules for 120 degree change
                default -> direction;
              };
    }
    System.out.println("Direction: " + direction);
    collisionDetection(currentHex, direction, false);
  }

  public boolean reversalChecker(String currentHex, String direction){
    String[] coordinates = currentHex.split(",");
    int x = Integer.parseInt(coordinates[0]);
    int y = Integer.parseInt(coordinates[1]);
    int z = Integer.parseInt(coordinates[2]);
    switch(direction){
      case "-1, 0, +1":
        if(atomLocations.contains((x) + "," + (y-1) + "," + (z+1)) && atomLocations.contains((x-1) + "," + (y+1) + "," + z)){
          System.out.println("Reversal Detected");
          return true;
        }
        break;
      case "0, -1, +1":
        if(atomLocations.contains((x-1) + "," + (y) + "," + (z+1)) && atomLocations.contains((x + 1) + "," + (y-1) + "," + z)){
          System.out.println("Reversal Detected");
          return true;
        }
        break;
      case "+1, -1, 0":
        if(atomLocations.contains((x) + "," + (y-1) + "," + (z+1)) && atomLocations.contains((x+1) + "," + (y) + "," + (z-1))){
          System.out.println("Reversal Detected");
          return true;
        }
        break;
      case "+1, 0, -1":
        if(atomLocations.contains((x+1) + "," + (y-1) + "," + (z)) && atomLocations.contains((x) + "," + (y+1) + "," + (z-1))){
          System.out.println("Reversal Detected");
          return true;
        }
        break;
      case "0, +1, -1":
        if(atomLocations.contains((x-1) + "," + (y+1) + "," + (z)) && atomLocations.contains((x + 1) + "," + (y) + "," + (z-1))){
          System.out.println("Reversal Detected");
          return true;
        }
        break;
      case "-1, +1, 0":
        if(atomLocations.contains((x-1) + "," + (y) + "," + (z+1)) && atomLocations.contains((x) + "," + (y+1) + "," + (z-1))){
          System.out.println("Reversal Detected");
          return true;
        }
        break;
    }
    System.out.println("No Reversal Detected");
    return false;
  }

  public int[] directionSelection(String direction) {
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
    return new int[] {dx, dy, dz};
  }
  public void hexDisplayer(String originHex, int lastX, int lastY, int lastZ, String direction) {
    String[] coordinatesOrigin = originHex.split(",");
    int originX = Integer.parseInt(coordinatesOrigin[0]);
    int originY = Integer.parseInt(coordinatesOrigin[1]);
    int originZ = Integer.parseInt(coordinatesOrigin[2]);
    int degree = getDegree(direction);
    String lastCoordinates = lastX + "," + lastY + "," + lastZ;
    System.out.print("Encounter Type: " + lastEncounterType);
    switch (lastEncounterType) {
      case DEFLECTION:
        // Handle deflection
        gui.disableButtonAt(lastCoordinates, degree);
        if (deflectionCounter == 1) {
          hexManager.alterHexagon(originX, originY, originZ, Color.BLUE);
          hexManager.alterHexagon(lastX, lastY, lastZ, Color.BLUE);
        }
        else{
          System.out.print("Deflection Counter: " + deflectionCounter);
          hexManager.alterHexagon(originX, originY, originZ, Color.DEEPPINK);
          hexManager.alterHexagon(lastX, lastY, lastZ, Color.DEEPPINK);
        }
        break;
      case DIRECT_HIT:
        hexManager.alterHexagon(originX, originY, originZ, Color.GREEN);
        break;
      case DOUBLE_HIT:
        gui.disableButtonAt(lastCoordinates, degree);
        hexManager.alterHexagon(originX, originY, originZ, Color.BROWN);
        hexManager.alterHexagon(lastX, lastY, lastZ, Color.BROWN);
        break;
      case NO_ENCOUNTER:
        gui.disableButtonAt(lastCoordinates, degree);
        hexManager.alterHexagon(originX, originY, originZ, Color.PURPLE);
        hexManager.alterHexagon(lastX, lastY, lastZ, Color.PURPLE);
      default:
        // Handle no encounter
        gui.disableButtonAt(lastCoordinates, degree);
        hexManager.alterHexagon(originX, originY, originZ, Color.PURPLE);
        hexManager.alterHexagon(lastX, lastY, lastZ, Color.PURPLE);
        break;
    }
  }

  private static int getDegree(String direction) {
    int degree;

    degree = switch (direction) {
      case "+1, 0, -1" -> 0; // Reverse traversal rules for degree 0
      case "0, +1, -1" -> 60; // Reverse traversal rules for degree 60
      case "-1, +1, 0" -> 120; // Reverse traversal rules for degree 120
      case "-1, 0, +1" -> 180; // Reverse traversal rules for degree 180
      case "0, -1, +1" -> 240; // Reverse traversal rules for degree 240
      case "+1, -1, 0" -> 300; // Reverse traversal rules for degree 300
      default -> throw new IllegalArgumentException("Invalid direction: " + direction);
    };
    return degree;
  }


  public boolean isInMultipleNeighbors(String currentHex) {
    Map<String, List<String>> atomNeighbors = atomsEffectiveRange();
    int counter = 0;

    for(List<String> neighbors : atomNeighbors.values()){
      if(neighbors.contains(currentHex)){
        counter++;
      }
    }
    return counter >= 2;
  }

  public int[] findLastHex(int x, int y, int z, int dx, int dy, int dz){
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
    return new int[] {x, y, z};
  }

  public void scoreTracker(){
    Map<String, Integer> orangeHexButtons = gui.getOrangeHexButtons();
    for (Map.Entry<String, Integer> entry : orangeHexButtons.entrySet()) {
      String key = entry.getKey();
        atomsGuesses++;
        if (atomLocations.contains(key)) {
          atomsHit++;
        }
    }
        atomsMissed = atomsGuesses - atomsHit;
        System.out.println("Atoms Hit: " + atomsHit);
        System.out.println("Atoms Guesses: " + atomsGuesses);
        System.out.println("Rays Shot Final: " + raysShot);
        score = (raysShot + (5 * (atomsGuesses - atomsHit)));
        System.out.println("Score: " + score);
  }

  public int getScore(){
    return score;
  }
}


