package org.blackbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class game {
  private List<String> atomLocations;
  private hexagonManager hexManager;

  public game(hexagonManager hexManager) {
    atomLocations = new ArrayList<>();
    this.hexManager = hexManager;
  }

  // Selects the locations of the atoms using the hashmap of the hexagons which is then randomized
  // and the first six locations are added to the atomLocations list
  public void atomSelection() {
    List<String> validHexes = new ArrayList<>(hexManager.getAllHexagonLocations());
    System.out.println(
        "Selecting atoms. Total hexagons: " + hexManager.getAllHexagonLocations().size());
    if (validHexes.isEmpty()) {
      System.out.println("No hexagon locations found.");
      return;
    }
    Collections.shuffle(validHexes); // Shuffle the list to get random locations
    for (int i = 0; i < 6; i++) {
      atomLocations.add(validHexes.get(i)); // Add the first six locations to atomLocations
    }
  }

  // splits the location string into x, y, and z coordinates and then alters the hexagon at that co-ordinate
  // changing the color and adding a dotted circle to the gui
  public void atomReveal() {
    for (String location : atomLocations) {
      String[] coordinates = location.split(",");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      int z = Integer.parseInt(coordinates[2]);
      hexManager.alterHexagon(x, y, z, Color.RED);

      // Calculate the position of the hexagon
      double posX = gui.getHexHeight() * (x + y / 2.0) + 500; // 500 is the center of the grid
      double posY = 1.5 * gui.getHexSize() * y + 500; // 500 is the center of the grid

      // Create a new Circle for the atom
      Circle atomCircle = new Circle(posX, posY, gui.getHexHeight());
      atomCircle.setStroke(Color.BLACK);
      atomCircle.setStrokeWidth(2);
      atomCircle.setStrokeType(StrokeType.INSIDE);
      atomCircle.getStrokeDashArray().addAll(5d, 10d); // Set the stroke to a dotted pattern
      atomCircle.setFill(Color.TRANSPARENT); // Set the fill to transparent

      // Add the Circle to the root Pane
      gui.addCircle(atomCircle);
    }
  }
}
