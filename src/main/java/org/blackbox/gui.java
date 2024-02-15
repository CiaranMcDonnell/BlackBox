package org.blackbox;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class gui extends Application {

    private static final double HEX_SIZE = 30; // Size of the hexagon
    private static final double HEX_HEIGHT = Math.sqrt(3) * HEX_SIZE;

    // Hexagon class
    public static class Hexagon extends Polygon {
        private int x, y, z;

        public Hexagon(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }


    // Method to create a hexagon at a given position
    private Hexagon createHexagon(int x, int y, int z, double posX, double posY) {
        Hexagon hexagon = new Hexagon(x, y, z);
        for (int i = 0; i < 6; i++) {
            hexagon.getPoints().addAll(
                    posX + HEX_SIZE * Math.cos((i * Math.PI / 3) + Math.PI / 6),
                    posY + HEX_SIZE * Math.sin((i * Math.PI / 3) + Math.PI / 6)
            );
        }
        hexagon.setFill(Color.LIGHTGRAY);
        hexagon.setStroke(Color.BLACK);
        return hexagon;
    }

    // Method to generate the hexagonal grid
    private void generateGrid(Pane root) {
        double centerX = root.getWidth() / 2;
        double centerY = root.getHeight() / 2;

        for (int x = -4; x <= 4; x++) {
            for (int y = Math.max(-4, -x-4); y <= Math.min(4, -x+4); y++) {
                int z = -x-y;
                double posX = HEX_HEIGHT * (x + y / 2.0) + centerX;
                double posY = 1.5 * HEX_SIZE * y + centerY;
                root.getChildren().add(createHexagon(x, y, z, posX, posY));
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        generateGrid(root);

        primaryStage.setTitle("My Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}