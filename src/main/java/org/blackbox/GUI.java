package org.blackbox;

import java.util.List;
import java.util.Map;
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

/**
 * GUI class that extends the Application class from JavaFX. This class is responsible for creating
 * and managing the graphical user interface of the application.
 */
public class GUI extends Application {
  Game myGame = new Game(hexManager);
  // Constants for the GUI
  public static final int HIGHEST_COORDINATE =
      4; // Highest coordinate value which also sets the over-all size of the grid
  public static final float GUI_SIZE = 1200;
  private static final double HEX_SIZE = 55; // Size of the individual hexagon
  private static final double HEX_HEIGHT =
      Math.sqrt(3) * HEX_SIZE; // Height of the individual hexagon
  // Instance variables for the GUI
  private static Pane root; // Pane to hold the hexagons
  private static HexagonManager hexManager;
  private static GUI instance;

  // Getter methods for the GUI
  public static double getHexHeight() { // Used for drawing onto individual hexes
    return HEX_HEIGHT;
  }

  public static double getHexSize() { // Used for drawing onto individual hexes
    return HEX_SIZE;
  }

  // Method to add a circle to the root pane
  public static void addCircle(Circle circle) {
    if (circle == null) {
      throw new NullPointerException("Circle cannot be null");
    }
    root.getChildren().add(circle);
  }

  // Main method to launch the application
  public static void main(String[] args) {
    launch(args);
  }

  // Method to add a label to the root pane
  public static void addLabel(javafx.scene.control.Label label) {
    if (label == null) {
      throw new NullPointerException("Label cannot be null");
    }
    root.getChildren().add(label);
  }

  // Getter and setter methods for the HexagonManager
  public static HexagonManager getHexagonManager() {
    return hexManager;
  }

  public static void setHexagonManager(HexagonManager hexManager) {
    if (hexManager == null) {
      throw new NullPointerException("HexagonManager cannot be null");
    }
    GUI.hexManager = hexManager;
  }

  // Singleton pattern for the GUI instance
  public static GUI getInstance() {
    if (instance == null) {
      instance = new GUI();
    }
    return instance;
  }

  // Method to get a list of the circles in the root pane
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
    hexagon.setFill(Color.GRAY);
    hexagon.setStroke(Color.ORANGE);
    hexManager.addHexagon(x, y, z, hexagon);
    return hexagon;
  }

  // Method to generate the hexagonal grid
  private void generateGrid(Pane root) {
    double centerX = root.getWidth() / 2;
    double centerY = root.getHeight() / 2;

    for (int x = -HIGHEST_COORDINATE; x <= HIGHEST_COORDINATE; x++) {
      for (int y = Math.max(-HIGHEST_COORDINATE, -x - HIGHEST_COORDINATE);
          y <= Math.min(HIGHEST_COORDINATE, -x + HIGHEST_COORDINATE);
          y++) {
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
  // Position Calculator - could be reformatted later with better logic
  private double[] positionCalculator(int degree, int x, int y, int z) {
    double posX = GUI.getHexHeight() * (x + y / 2.0) + (GUI.GUI_SIZE / 2);
    double posY = 1.5 * GUI.getHexSize() * y + (GUI.GUI_SIZE / 2);
    double offset;

    switch(degree) {
      case 0:
        offset = GUI.getHexSize()+15;
        posY -= 15;
        posX -= 22;
        break;
      case 60:
        offset = GUI.getHexSize()+15;
        posY -=28;
        posX -=15;
        break;
      case 120:
        offset = GUI.getHexSize()+15;
        posY -=30;
        posX -= 5;
        break;
      case 180:
        offset = GUI.getHexSize()+15;
        posY -= 15;
        posX += 5;
        break;
      case 240:
        offset = GUI.getHexSize()-15;
        posY -= 30;
        posX -= 15;
        break;
      case 300:
        offset = GUI.getHexSize()-15;
        posY -= 30;
        break;
      default:
        throw new IllegalArgumentException("Invalid degree: " + degree);
    }
    return new double[]{offset, posX, posY};
  }

  // Calculates Button Positions using the calculated values from the positionCalculator
  private double[] calculateButtonPosition(int degree, int x, int y, int z) {
    double[] calculatedValues = positionCalculator(degree, x, y, z);
    double offset = calculatedValues[0];
    double posX = calculatedValues[1];
    double posY = calculatedValues[2];

    double radian = Math.toRadians(degree);
    posX += offset * Math.cos(radian);
    posY += offset * Math.sin(radian);

    return new double[]{posX, posY};
  }

  private void createAllEntryPointButtons(Game myGame) {
    // Create a button for each entry point
    for (Map.Entry<String, List<Integer>> entry : myGame.getEntryPointsMap().entrySet()) {
      String hex = entry.getKey();
      String[] coordinates = entry.getKey().split(",");
      int x = Integer.parseInt(coordinates[0]);
      int y = Integer.parseInt(coordinates[1]);
      int z = Integer.parseInt(coordinates[2]);

      for (Integer degree : entry.getValue()) {
        // Calculate the position of the button
        double[] position = calculateButtonPosition(degree, x, y, z);
        Button entryPointButton = createButtonWithAction(degree, position[0], position[1], hex);

        // Add the button to the root pane
        root.getChildren().add(entryPointButton);
      }
    }
  }



  private Button createButtonWithAction(Integer degree, double posX, double posY, String hex) {
    Button entryPointButton = new Button();
    entryPointButton.setLayoutX(posX); // Set the x position of the button
    entryPointButton.setLayoutY(posY); // Set the y position of the button
    entryPointButton.setMinWidth(1); // Set the width of the button
    entryPointButton.setMinHeight(30); // Set the height of the button
    entryPointButton.setRotate(degree); // Rotate the button

    entryPointButton.setUserData(new ButtonData(hex, degree));

    entryPointButton.setStyle("-fx-background-color: orange;");
    entryPointButton.setOnAction(e -> {
      ButtonData buttonData = (ButtonData) entryPointButton.getUserData();
      entryPointButton.setStyle("-fx-background-color: yellow;");
      myGame.handleButtonClick(buttonData);
      entryPointButton.setDisable(true);
    });

    return entryPointButton;
  }

  // Method to start the application
  @Override
  public void start(Stage primaryStage) {
    root = new Pane();
    Scene scene = new Scene(root, GUI_SIZE, GUI_SIZE);
    root.setStyle("-fx-background-color: grey;");
    Button startGameButton = new Button("Start Game");
    startGameButton.setLayoutX(10); // Set the x position of the button
    startGameButton.setLayoutY(10); // Set the y position of the button

    Button revealButton = new Button("Reveal Atoms");
    revealButton.setLayoutX(10); // Set the x position of the button
    revealButton.setLayoutY(40); // Set the y position of the button
    revealButton.setDisable(true); // Initially disable the reveal button

    generateGrid(root);

    startGameButton.setOnAction(
            e -> {
              // Enable the reveal button when the start game button is clicked
              myGame.atomSelection();
              myGame.atomsEffectiveRange();
              myGame.storeEntryPoints();
              revealButton.setDisable(false);
              startGameButton.setDisable(true); // Disable the button after it's clicked
              startGameButton.setText("Game Started"); // Change the text of the button
              startGameButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

              createAllEntryPointButtons(myGame);
            });

    revealButton.setOnAction(
            e -> {
              myGame.atomReveal();
              //displayEntryPoints();

              revealButton.setDisable(true); // Disable the button after it's clicked
              revealButton.setText("Revealed"); // Change the text of the button
              revealButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
            });

    root.getChildren().addAll(startGameButton, revealButton); // adds the buttons to the root pane

    primaryStage.setTitle("BlackBox Game");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Hexagon class that extends the Polygon class from JavaFX. This class represents a hexagon in
   * the grid.
   */
  public static class Hexagon extends Polygon {
    private int x, y, z;

    public Hexagon(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
  }
}

