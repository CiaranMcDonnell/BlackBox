package org.blackbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * GUI class that extends the Application class from JavaFX. This class is responsible for creating
 * and managing the graphical user interface of the application.
 */
public class GUI extends Application {
  // Constants for the GUI
  public static final int HIGHEST_COORDINATE =
          4; // Highest coordinate value which also sets the over-all size of the grid
  public static final float GUI_SIZE = 1000;
  private Button tutorialButton;
  public boolean cheatMode = false;
  private static final double HEX_SIZE = 55; // Size of the individual hexagon
  private int guessedAtoms = 0;
  private static final double HEX_HEIGHT =
          Math.sqrt(3) * HEX_SIZE; // Height of the individual hexagon
  private static int counter = 0;
  // Instance variables for the GUI
  private static Pane root; // Pane to hold the hexagons
  private static HexagonManager hexManager;
  private static GUI instance;
  // Method to generate the hexagonal grid
  public Map<String, Integer> gridLocationMap = new HashMap<>();
  Game myGame = new Game(hexManager, this);
  Map<String, Integer> orangeHexButtons = new HashMap<String, Integer>();

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
    hexagon.setFill(Color.BLACK);
    hexagon.setStroke(Color.ORANGE);
    hexManager.addHexagon(x, y, z, hexagon);
    return hexagon;
  }

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

        Button hexButton = createHexButton(posX, posY, x, y, z);
        hexButton.setVisible(false); // Hide the button initially
        root.getChildren().add(hexButton);

        // Used to print out the grid locations on the hexagons
        // Create a new Text object for the grid location
        Text gridLocation = new Text(posX, posY, String.valueOf(counter++));
        gridLocation.setFont(Font.font("Verdana", FontWeight.BOLD, 20)); // Set the font name, style, and size
        gridLocation.setStyle("-fx-fill: grey;");
        gridLocation.setX(posX - HEX_SIZE / 2); // Adjust the x position
        gridLocation.setY(posY); // Adjust the y position

        root.getChildren().add(gridLocation);
        String coordinateKey = x + "," + y + "," + z;
        gridLocationMap.put(coordinateKey, counter);
      }
    }
  }

  private Button createHexButton(double posX, double posY, int x, int y, int z) {
    Polygon hexagon = new Polygon();
    for(int i = 0; i < 6; i++) {
      double angle = 2.0 * Math.PI / 6 * i;
      double hexX = HEX_SIZE * Math.cos(angle);
      double hexY = HEX_SIZE * Math.sin(angle);
      hexagon.getPoints().addAll(hexX, hexY);
    }
    String hex = x + "," + y + "," + z;
    Button hexButton = new Button();
    hexButton.setUserData(new HexagonButtonData(hex, counter));
    hexButton.setLayoutX(posX); // Set the x position of the button
    hexButton.setLayoutY(posY); // Set the y position of the button
    hexButton.setShape(hexagon); // Set the shape of the button to a hexagon
    hexButton.setOpacity(0.9); // Make the button transparent
    hexButton.setOnAction(e -> {
      if (hexButton.getStyle().equals("-fx-background-color: orange;")) {
        guessedAtoms--;
        hexButton.setStyle(""); // Reset the style to default when the button is unclicked
      } else if(guessedAtoms < 6){
        guessedAtoms++;
        hexButton.setStyle("-fx-background-color: orange;"); // Set the style to red when the button is clicked
      }
      updateGuessedAtoms(guessedAtoms);
    });
    return hexButton;
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
    System.out.println("HEX = " + hex);
    entryPointButton.setUserData(new ButtonData(hex, degree));

    entryPointButton.setStyle("-fx-background-color: orange;");
    entryPointButton.setOnAction(e -> {
      ButtonData buttonData = (ButtonData) entryPointButton.getUserData();
      entryPointButton.setStyle("-fx-background-color: yellow;");
      myGame.handleButtonClick(buttonData);
      myGame.raysShot++;
      System.out.println("Rays shot: " + myGame.raysShot);
      entryPointButton.setDisable(true);
    });

    return entryPointButton;
  }

  public void disableButtonAt(String hex, int degree) {
    for (Node node : root.getChildren()) {
      if (node instanceof Button button) {
        Object userData = button.getUserData();
        if (userData instanceof ButtonData buttonData) {
          if (buttonData.hex().equals(hex) && buttonData.degree() == degree) {
            button.setDisable(true);
            button.setStyle("-fx-background-color: purple;");
          }
        } else if (userData instanceof HexagonButtonData hexButtonData) {
          if (hexButtonData.hex().equals(hex)) {
            button.setDisable(true);
          }
        }
      }
    }
  }

  // Method to start the application
  @Override
  public void start(Stage primaryStage) {
    root = new Pane();
    Scene scene = new Scene(root, GUI_SIZE, GUI_SIZE);
    String hoverStyle = "-fx-background-color: linear-gradient(to bottom, #999999, #777777); -fx-text-fill: white; -fx-font-weight: bold;";
    String startStyle = "-fx-background-color: linear-gradient(to bottom, #4CAF50, #45a049); -fx-text-fill: white; -fx-font-weight: bold";
    String endStyle = "-fx-background-color: linear-gradient(to bottom, #f44336, #d32f2f); -fx-text-fill: white; -fx-font-weight: bold";
    String cheatStyle = "-fx-background-color: linear-gradient(to bottom, #2196F3, #1976D2); -fx-text-fill: white; -fx-font-weight: bold";
    String fullDetailsStyle = "-fx-background-color: linear-gradient(to bottom, #FFC107, #FFA000); -fx-text-fill: white; -fx-font-weight: bold";
    String revealStyle = "-fx-background-color: linear-gradient(to bottom, #9C27B0, #7B1FA2); -fx-text-fill: white; -fx-font-weight: bold";
    root.setStyle("-fx-background-color: black; " +
            "-fx-border-color: orange; " +
            "-fx-border-width: 2px;");
    Button startGameButton = new Button("Start Game");
    startGameButton.setStyle(startStyle);
    startGameButton.setOnMouseEntered(e -> {
      startGameButton.setStyle(hoverStyle);
      startGameButton.setCursor(Cursor.HAND);
    });
    startGameButton.setOnMouseExited(e -> {
      startGameButton.setStyle(startStyle);
      startGameButton.setCursor(Cursor.DEFAULT);
    });
    startGameButton.setLayoutX(10); // Set the x position of the button
    startGameButton.setLayoutY(10); // Set the y position of the button
    Button endGameButton = new Button("End Game");
    endGameButton.setStyle(endStyle);
    endGameButton.setOnMouseEntered(e -> {
      endGameButton.setStyle(hoverStyle);
      endGameButton.setCursor(Cursor.HAND);
    });
    endGameButton.setOnMouseExited(e -> {
      endGameButton.setStyle(endStyle);
      endGameButton.setCursor(Cursor.DEFAULT);
    });
    endGameButton.setLayoutX(10); // Set the x position of the button
    endGameButton.setLayoutY(70); // Set the y position of the button
    endGameButton.setDisable(true); // Initially disable the end game button

    Button cheatModeButton = new Button("Cheat Mode");
    cheatModeButton.setStyle(cheatStyle);
    cheatModeButton.setOnMouseEntered(e -> {
      cheatModeButton.setStyle(hoverStyle);
      cheatModeButton.setCursor(Cursor.HAND);
    });
    cheatModeButton.setOnMouseExited(e -> {
      cheatModeButton.setStyle(cheatStyle);
      cheatModeButton.setCursor(Cursor.DEFAULT);
    });
    cheatModeButton.setLayoutX(10); // Set the x position of the button
    cheatModeButton.setLayoutY(100); // Set the y position of the button
    cheatModeButton.setDisable(true);

    Button fullDetailsButton = new Button("Full Details");
    fullDetailsButton.setStyle(fullDetailsStyle);
    fullDetailsButton.setOnMouseEntered(e -> {
      fullDetailsButton.setStyle(hoverStyle);
      fullDetailsButton.setCursor(Cursor.HAND);
    });
    fullDetailsButton.setOnMouseExited(e ->{
      fullDetailsButton.setStyle(fullDetailsStyle);
      fullDetailsButton.setCursor(Cursor.DEFAULT);
    });
    fullDetailsButton.setLayoutX(10); // Set the x position of the button
    fullDetailsButton.setLayoutY(130); // Set the y position of the button
    fullDetailsButton.setDisable(true);

    Button revealButton = new Button("Reveal Atoms");
    revealButton.setStyle("-fx-background-color: linear-gradient(to bottom, #9C27B0, #7B1FA2); -fx-text-fill: white; -fx-font-weight: bold;");
    revealButton.setOnMouseEntered(e -> {
      revealButton.setStyle(hoverStyle);
      revealButton.setCursor(Cursor.HAND);
    });
    revealButton.setOnMouseExited(e -> {
      revealButton.setStyle(hoverStyle);
      revealButton.setCursor(Cursor.DEFAULT);
    });
    revealButton.setLayoutX(10); // Set the x position of the button
    revealButton.setLayoutY(40); // Set the y position of the button
    revealButton.setDisable(true); // Initially disable the reveal button

    tutorialButton = new Button("Tutorial");
    tutorialButton.setStyle(revealStyle);
    tutorialButton.setOnMouseEntered(e -> {
      tutorialButton.setStyle(hoverStyle);
      tutorialButton.setCursor(Cursor.HAND);
    });
    tutorialButton.setOnMouseExited(e -> {
      tutorialButton.setStyle(revealStyle);
      tutorialButton.setCursor(Cursor.DEFAULT);
    });
    tutorialButton.setLayoutX(10); // Set the x position of the button
    tutorialButton.setLayoutY(160); // Set the y position of the button
    tutorialButton.setOnAction(
            e -> {
              showTutorial();
              tutorialButton.setDisable(true);
            }
    );

    root.getChildren().add(scoreLabel); // Add the Label to the root Pane

    generateGrid(root);
    fullDetailsButton.setOnAction(
            e -> {
              if(!cheatMode){
                root.getChildren().add(polylinePane);
                cheatMode = true;
              }
              atomRevealer();
              showScore();
              fullDetailsButton.setDisable(true);
              fullDetailsButton.setText("Full Details Enabled");
              fullDetailsButton.setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
            }
    );
    cheatModeButton.setOnAction(
            e -> {
              if(!cheatMode){
                root.getChildren().add(polylinePane);
                cheatMode = true;
              }
              atomRevealer();
              cheatModeButton.setDisable(true);
              cheatModeButton.setText("Cheat Mode Enabled");
              cheatModeButton.setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
            });

    startGameButton.setOnAction(
            e -> {
              // Enable the reveal button when the start game button is clicked
              myGame.atomSelection();
              myGame.atomsEffectiveRange();
              myGame.storeEntryPoints();
              startGameButton.setDisable(true); // Disable the button after it's clicked
              startGameButton.setText("Game Started"); // Change the text of the button
              startGameButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

              createAllEntryPointButtons(myGame);

              // make hex buttons visible
              for (Node node : root.getChildren()) {
                if (node instanceof Button button && button.getShape() instanceof Polygon) {
                  button.setVisible(true);
                }
              }
            });
    endGameButton.setOnAction(
            e -> {
              endGameButton.setDisable(true); // Disable the button after it's clicked
              endGameButton.setText("Game Ended"); // Change the text of the button
              endGameButton.setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
              if(!atomsRevealed){
                revealButton.setDisable(false);
              }
              cheatModeButton.setDisable(true);
              fullDetailsButton.setDisable(false);
              myGame.scoreTracker();
              for (Node node : root.getChildren()) {
                if (node instanceof Button button) {
                  if (button.getShape() instanceof Polygon
                          || button.getUserData() instanceof ButtonData) {
                    button.setDisable(true);
                  }
                }
              }
              showScore();
            });
    revealButton.setOnAction(
            e -> {
              myGame.atomReveal();

              revealButton.setDisable(true); // Disable the button after it's clicked
              revealButton.setText("Revealed"); // Change the text of the button
              revealButton.setStyle("-fx-text-fill: grey; -fx-font-weight: bold;");
            });

    root.getChildren().addAll(startGameButton, revealButton, endGameButton); // adds the buttons to the root pane

    primaryStage.setTitle("BlackBox Game");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showScore() {
    Stage scoreStage = new Stage();

    Label scoreLabel = new Label("Score: " + myGame.getScore() + "\nRays Shot: " + myGame.raysShot + "\nAtoms Hit: " + myGame.atomsHit + "\nAtoms Missed: " + myGame.atomsMissed
            + "\nScoring Formula = (" + myGame.raysShot + " * 1) + (" + myGame.atomsHit + " * -5) + (" + myGame.atomsMissed + " * 5) = " + myGame.getScore());
    scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
    scoreLabel.setTextFill(Color.BLACK);

    Scene scoreScene = new Scene(new Pane(scoreLabel), 550, 150);
    scoreStage.setScene(scoreScene);

    scoreStage.show();
  }

  public void showTutorial(){
    Stage tutorialStage = new Stage();

    Label tutorialLabel = new Label("""
    **Welcome to BlackBox!**

    The objective of the game is simple:
    - Your mission is to locate the atoms hidden inside the black box.
    - You can shoot rays into the box from the entry point buttons located on the edge of the grid.
    - The rays will interact with the atoms in two possible ways: they will either be deflected or absorbed.
    - Deflected rays will exit the box from the edge points on the grid. The exit points are shown by the de-activated purple buttons.
    - Absorbed rays will not exit the box.
    - You can use the behavior of the rays to determine the location of the atoms.
    - If you're feeling stuck, you can reveal the atoms at any time using the cheat mode button.
    - The game ends when you have successfully guessed all the atom locations.
    - The lower your score at the end the better, formula shown in score window at end of game.

    Good luck and have fun!

    **LEGEND:**
    - **BLUE** = Ray deflected once at 60 degrees. Exit location is marked.
    - **GREEN** = Ray absorbed by an atom. No exit location.
    - **YELLOW** = Ray deflected by two atoms, sent 180 degrees back to source. No exit location marked.
    - **PURPLE** = Ray made no contact with any atom and had a clear path. Exit location marked.
    - **PINK** = Ray deflected by an atom at 60 degrees more than once. Exit location marked.
    - **Entry Point Button** = ORANGE when not used, YELLOW when fired from, PURPLE when ray exited at that angle.
    - **RED** = Atom. A dotted circle around it indicates the area of effect.
    - **BROWN** = Ray deflected twice by two atoms affecting one hex, causing a 120-degree change and making the ray
    leave the box before hitting another atom. Exit location marked.
    """);

    tutorialLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));
    tutorialLabel.setTextFill(Color.ORANGE);
    Pane tutorialPane = new Pane(tutorialLabel);
    tutorialPane.setStyle("-fx-background-color: black;"); //Sets background color
    Scene tutorialScene = new Scene(tutorialPane, 1200, 750);
    tutorialStage.setScene(tutorialScene);
    tutorialStage.show();

    tutorialStage.setOnCloseRequest(e -> tutorialButton.setDisable(false));
  }


  void hexButtonAccuracy(){
    // Clear the existing entries in the map
    orangeHexButtons.clear();

    // Iterate over all nodes in the root pane
    for (Node node : root.getChildren()) {
      if (node instanceof Button button) {
        // Check if the button has a Polygon shape and is styled with an orange background
        if (button.getShape() instanceof Polygon && button.getStyle().contains("background-color: orange;")) {
          // Cast the user data of the button to HexagonButtonData and get the data
          HexagonButtonData hexButtonData = (HexagonButtonData) button.getUserData();
          String hex = hexButtonData.hex();
          int number = hexButtonData.number();

          // Add the button to the map with its data as the key
          orangeHexButtons.put(hex, number);
        }
      }
    }
  }

  public Map<String, Integer> getOrangeHexButtons() {
    hexButtonAccuracy();
    return orangeHexButtons;
  }

  public void updateScore(int score) {
    scoreLabel.setText("Score: " + score + "\nRays Shot: " + myGame.raysShot + "\nAtoms Hit: " + myGame.atomsHit + "\nAtoms Missed: " + myGame.atomsMissed
      + "\nScoring Formula = (" + myGame.raysShot + " * 1) + (" + myGame.atomsHit + " * -5) + (" + myGame.atomsMissed + " * 5) = " + score);
  }

  public void updateGuessedAtoms(int newGuessedAtoms) {
    // Update the guessedAtoms value
    this.guessedAtoms = newGuessedAtoms;
    setEndGameButtonDisabled(this.guessedAtoms < 6);
  }
  public Button getEndGameButton() {
    // Iterate over all nodes in the root pane
    for (Node node : root.getChildren()) {
      if (node instanceof Button button) {
        // Check if the button's text is "End Game"
        if ("End Game".equals(button.getText())) {
          return button; // Return the "End Game" button
        }
      }
    }
    return null; // Return null if the "End Game" button is not found
  }
  public void setEndGameButtonDisabled(boolean disabled) {
    // Get the end game button
    Button endGameButton = getEndGameButton();

    // Enable or disable the button
    endGameButton.setDisable(disabled);
  }

  public static void updateButtons(){
    List<Button> buttonsToModify = new ArrayList<>();

    for (Node node : root.getChildren()) {
      if (node instanceof Button button) {
        buttonsToModify.add(button);
      }
    }

    for (Button button : buttonsToModify) {
      button.toFront();
      if(button.getShape() instanceof Polygon){
        button.setDisable(false);
      }
    }
  }


  /**
   * Hexagon class that extends the Polygon class from JavaFX. This class represents a hexagon in
   * the grid.
   */
  public static class Hexagon extends Polygon {
    private final int x;
    private final int y;
    private final int z;

    public Hexagon(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
  }
}

