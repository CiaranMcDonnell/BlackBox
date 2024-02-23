package org.blackbox;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.Test;

class HexagonManagerTest {

    @Test
    void addHexagon() {
        HexagonManager manager = new HexagonManager();
        GUI.Hexagon hexagon = org.mockito.Mockito.mock(GUI.Hexagon.class); // Using a mock instead of a real object
        manager.addHexagon(1, 2, 3, hexagon);
        assertNotNull(manager.getHexagon(1, 2, 3), "Hexagon should be added and retrievable");
    }

    @Test
    void getAllHexagonLocations() {
        HexagonManager manager = new HexagonManager();
        GUI.Hexagon hex1 = org.mockito.Mockito.mock(GUI.Hexagon.class); // Using a mock instead of a real object
        GUI.Hexagon hex2 = org.mockito.Mockito.mock(GUI.Hexagon.class); // Using a mock instead of a real object
        manager.addHexagon(1, 2, 3, hex1);
        manager.addHexagon(4, 5, 6, hex2);
        Set<String> locations = manager.getAllHexagonLocations();
        assertTrue(locations.contains("1,2,3") && locations.contains("4,5,6"), "All locations should be returned");
        assertEquals(2, locations.size(), "The size of locations should match the number of added hexagons");
    }
    @Test
    void getHexagon() {
        HexagonManager manager = new HexagonManager();
        GUI.Hexagon hexagon = org.mockito.Mockito.mock(GUI.Hexagon.class); // Using a mock instead of a real object
        manager.addHexagon(1, 2, 3, hexagon);
        assertNotNull(manager.getHexagon(1, 2, 3), "Should return the added hexagon");
        assertNull(manager.getHexagon(4, 5, 6), "Should return null for non-existent hexagon");
    }
    @Test
    void alterHexagon() {
        HexagonManager manager = new HexagonManager();
        GUI.Hexagon hexagon = org.mockito.Mockito.mock(GUI.Hexagon.class); // Mocking gui.Hexagon
        javafx.scene.paint.Color newColor = javafx.scene.paint.Color.BLUE; // Example color
        manager.addHexagon(1, 2, 3, hexagon);
        manager.alterHexagon(1, 2, 3, newColor);
        org.mockito.Mockito.verify(hexagon).setFill(newColor); // Verify setFill was called with newColor
    }
}