package org.blackbox;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class gui extends Application {

  private static final double HEX_SIZE = 30; // Size of the hexagon
  private static final double HEX_HEIGHT = Math.sqrt(3) * HEX_SIZE;
  private static Pane root;
  private static hexagonManager hexManager;

  public static void setHexagonManager(hexagonManager hexManager) {
    gui.hexManager = hexManager;
  }

  public static double getHexHeight() {
    return HEX_HEIGHT;
  }

  public static double getHexSize() {
    return HEX_SIZE;
  }

  public static void addCircle(Circle circle) {
    root.getChildren().add(circle);
  }

  public static void main(String[] args) {
    launch(args);
  }

  // Method to create a hexagon at a given position
  private Hexagon createHexagon(int x, int y, int z, double posX, double posY) {
    Hexagon hexagon = new Hexagon(x, y, z);
    for (int i = 0; i < 6; i++) {
      hexagon
          .getPoints()
          .addAll(
              posX + HEX_SIZE * Math.cos((i * Math.PI / 3) + Math.PI / 6),
              posY + HEX_SIZE * Math.sin((i * Math.PI / 3) + Math.PI / 6));
    }
    hexagon.setFill(Color.LIGHTGRAY);
    hexagon.setStroke(Color.BLACK);
    hexManager.addHexagon(x, y, z, hexagon);
    return hexagon;
  }

  // Method to generate the hexagonal grid
  private void generateGrid(Pane root) {
    double centerX = root.getWidth() / 2;
    double centerY = root.getHeight() / 2;

    for (int x = -4; x <= 4; x++) {
      for (int y = Math.max(-4, -x - 4); y <= Math.min(4, -x + 4); y++) {
        int z = -x - y;
        double posX = HEX_HEIGHT * (x + y / 2.0) + centerX;
        double posY = 1.5 * HEX_SIZE * y + centerY;
        Hexagon hex = createHexagon(x, y, z, posX, posY);
        root.getChildren().add(hex);

        // Used to print out the grid locations on the hexagons
        // Create a new Text object for the grid location
        Text gridLocation = new Text(posX, posY, x + "," + y + "," + z);
        gridLocation.setFont(new Font(10)); // Set the font size
        gridLocation.setX(posX - HEX_SIZE / 2); // Adjust the x position
        gridLocation.setY(posY); // Adjust the y position

        root.getChildren().add(gridLocation);
      }
    }
  }

  @Override
  public void start(Stage primaryStage) {
    root = new Pane();
    Scene scene = new Scene(root, 1000, 1000);

    generateGrid(root); // Move this line before setting the revealButton's action

    Button revealButton = new Button("Reveal Atoms");
    revealButton.setLayoutX(10); // Set the x position of the button
    revealButton.setLayoutY(10); // Set the y position of the button

    revealButton.setOnAction(
        e -> {
          game myGame = new game(hexManager);
          myGame.atomSelection();
          myGame.atomReveal();

          revealButton.setDisable(true); // Disable the button after it's clicked
          revealButton.setText("Revealed"); // Change the text of the button
          revealButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        });

    root.getChildren().add(revealButton); // adds the button to the root pane

    primaryStage.setTitle("BlackBox Game");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  // Hexagon class
  public static class Hexagon extends Polygon {
    private int x, y, z;

    public Hexagon(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
  }
}
