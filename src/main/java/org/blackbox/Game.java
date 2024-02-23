package org.blackbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class Game {
  private List<String> atomLocations;
  private HexagonManager hexManager;
  private boolean atomsSelected;
  private GUI gui;

  public Game(HexagonManager hexManager) {
    atomLocations = new ArrayList<>();
    this.hexManager = hexManager;
    this.atomsSelected = false;
  }

  public HexagonManager getHexagonManager() {
    return this.hexManager;
  }

  // Selects the locations of the atoms using the hashmap of the hexagons which is then randomized
  // and the first six locations are added to the atomLocations list
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
    atomsSelected = true; // ensures that only 6 atoms can be selected in any single game run
  }

  public List<String> getAtomLocations() {
    return atomLocations;
  }

  // splits the location string into x, y, and z coordinates and then alters the hexagon at that
  // co-ordinate
  // changing the color and adding a dotted circle to the gui
  public void atomReveal() {
    for (String location : atomLocations) {
      String[] coordinates = location.split(",");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      int z = Integer.parseInt(coordinates[2]);
      hexManager.alterHexagon(x, y, z, Color.RED);

      // Calculate the position of the hexagon
      double posX = GUI.getHexHeight() * (x + y / 2.0) + 500; // 500 is the center of the grid
      double posY = 1.5 * GUI.getHexSize() * y + 500; // 500 is the center of the grid

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
}
