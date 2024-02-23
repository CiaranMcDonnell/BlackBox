package org.blackbox;

import java.util.List;
import java.util.stream.Collectors;
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

public class GUI extends Application {

  private static final double HEX_SIZE = 30; // Size of the hexagon
  private static final double HEX_HEIGHT = Math.sqrt(3) * HEX_SIZE;
  private static Pane root;
  private static HexagonManager hexManager;
  private static GUI instance;
  private static boolean started = false;

  public static boolean isStarted() {
    return started;
  }

  public static double getHexHeight() {
    return HEX_HEIGHT;
  }

  public static double getHexSize() {
    return HEX_SIZE;
  }

  public static void addCircle(Circle circle) {
    if (circle == null) {
      throw new NullPointerException("Circle cannot be null");
    }
    root.getChildren().add(circle);
  }

  public static void main(String[] args) {
    launch(args);
  }

  public static HexagonManager getHexagonManager() {
    return hexManager;
  }

  public static void setHexagonManager(HexagonManager hexManager) {
    if (hexManager == null) {
      throw new NullPointerException(" HexagonManager cannot be null");
    }
    GUI.hexManager = hexManager;
  }

  public static GUI getInstance() {
    if (instance == null) {
      instance = new GUI();
    }
    return instance;
  }

  // returns a list of the circles
  public static List<Circle> getCircles() {
    return root.getChildren().stream()
        .filter(node -> node instanceof Circle)
        .map(node -> (Circle) node)
        .collect(Collectors.toList());
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

    Button revealButton = new Button("Reveal Atoms");
    revealButton.setLayoutX(10); // Set the x position of the button
    revealButton.setLayoutY(10); // Set the y position of the button
    generateGrid(root);
    revealButton.setOnAction(
        e -> {
          Game myGame = new Game(hexManager);
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

  public Object getRoot() {
    return root;
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
