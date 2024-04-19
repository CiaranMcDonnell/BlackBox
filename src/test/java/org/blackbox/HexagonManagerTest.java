package org.blackbox;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class HexagonManagerTest {

  @Test
  void addHexagon() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hexagon =
        org.mockito.Mockito.mock(GUI.Hexagon.class); // Using a mock instead of a real object
    manager.addHexagon(1, 2, 3, hexagon);
    assertNotNull(manager.getHexagon(1, 2, 3), "Hexagon should be added and retrievable");
  }

  @Test
  void getAllHexagonLocations() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hex1 =
        org.mockito.Mockito.mock(GUI.Hexagon.class); // Using a mock instead of a real object
    GUI.Hexagon hex2 =
        org.mockito.Mockito.mock(GUI.Hexagon.class); // Using a mock instead of a real object
    manager.addHexagon(1, 2, 3, hex1);
    manager.addHexagon(4, 5, 6, hex2);
    Set<String> locations = manager.getAllHexagonLocations();
    assertTrue(
        locations.contains("1,2,3") && locations.contains("4,5,6"),
        "All locations should be returned");
    assertEquals(
        2, locations.size(), "The size of locations should match the number of added hexagons");
  }

  @Test
  void getHexagon() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hexagon =
        org.mockito.Mockito.mock(GUI.Hexagon.class); // Using a mock instead of a real object
    manager.addHexagon(1, 2, 3, hexagon);
    assertNotNull(manager.getHexagon(1, 2, 3), "Should return the added hexagon");
    assertNull(manager.getHexagon(4, 5, 6), "Should return null for non-existent hexagon");
  }

  @Test
  void alterHexagon() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hexagon = org.mockito.Mockito.mock(GUI.Hexagon.class); // Mocking gui.Hexagon
    javafx.scene.paint.Color newColor = javafx.scene.paint.Color.BLUE; // Example color
    javafx.scene.paint.Color expectedColor = javafx.scene.paint.Color.color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), 0.7); // Expected transparent color
    manager.addHexagon(1, 2, 3, hexagon);
    manager.alterHexagon(1, 2, 3, newColor);
    org.mockito.Mockito.verify(hexagon).setFill(expectedColor); // Verify setFill was called with expectedColor
  }
  @Test
  void addHexagonShouldAddHexagonToMap() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hexagon = Mockito.mock(GUI.Hexagon.class);
    manager.addHexagon(1, 2, 3, hexagon);
    assertNotNull(manager.getHexagon(1, 2, 3));
  }

  @Test
  void addHexagonShouldOverwriteExistingHexagon() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hexagon1 = Mockito.mock(GUI.Hexagon.class);
    GUI.Hexagon hexagon2 = Mockito.mock(GUI.Hexagon.class);
    manager.addHexagon(1, 2, 3, hexagon1);
    manager.addHexagon(1, 2, 3, hexagon2);
    assertSame(hexagon2, manager.getHexagon(1, 2, 3));
  }

  @Test
  void getHexagonShouldReturnNullIfHexagonDoesNotExist() {
    HexagonManager manager = new HexagonManager();
    assertNull(manager.getHexagon(1, 2, 3));
  }

  @Test
  void alterHexagonShouldChangeColorOfExistingHexagon() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hexagon = Mockito.mock(GUI.Hexagon.class);
    Color newColor = Color.BLUE;
    manager.addHexagon(1, 2, 3, hexagon);
    manager.alterHexagon(1, 2, 3, newColor);
    Mockito.verify(hexagon).setFill(Color.color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), 0.7));
  }

  @Test
  void alterHexagonShouldNotChangeColorIfHexagonDoesNotExist() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hexagon = Mockito.mock(GUI.Hexagon.class);
    Color newColor = Color.BLUE;
    manager.alterHexagon(1, 2, 3, newColor);
    Mockito.verifyNoInteractions(hexagon);
  }

  @Test
  void getNeighborLocationsShouldReturnEmptyListIfNoNeighbors() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hexagon = Mockito.mock(GUI.Hexagon.class);
    manager.addHexagon(1, 2, 3, hexagon);
    assertTrue(manager.getNeighborLocations(1, 2, 3).isEmpty());
  }

  @Test
  void getNeighborLocationsShouldReturnNeighbors() {
    HexagonManager manager = new HexagonManager();
    GUI.Hexagon hexagon1 = Mockito.mock(GUI.Hexagon.class);
    GUI.Hexagon hexagon2 = Mockito.mock(GUI.Hexagon.class);
    manager.addHexagon(-1, 3, -2, hexagon1);
    manager.addHexagon(-2, 3, -1, hexagon2);
    List<String> neighbors = manager.getNeighborLocations(-1, 3, -2);
    assertEquals(1, neighbors.size());
    assertTrue(neighbors.contains("-2,3,-1"));
  }
}
