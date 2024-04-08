package org.blackbox;

import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.control.Button;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GUITest {
    private GUI gui;

  @BeforeEach
  void setUp() {
      HexagonManager mockHexManager = Mockito.mock(HexagonManager.class);
    gui = new GUI();
    GUI.setHexagonManager(mockHexManager);
  }

  @Test
  void setHexagonManager_setsManagerCorrectly() {
    assertNotNull(GUI.getHexagonManager());
  }

  @Test
  void addCircle_addsCircleToRoot() {
    Circle circle = new Circle();
    GUI.addCircle(circle);
    assertTrue(GUI.getCircles().contains(circle));
  }

  @Test
  void addCircle_doesNotAddCircleWhenNull() {
    assertThrows(NullPointerException.class, () -> GUI.addCircle(null));
  }

  @Test
  void setHexagonManager_doesNotSetWhenNull() {
    assertThrows(NullPointerException.class, () -> GUI.setHexagonManager(null));
  }

  @Test
  void main_executesWithoutErrors() {
    assertDoesNotThrow(() -> GUI.main(new String[] {}));
  }

  @Test
  void createHexagon_createsHexagonCorrectly() {
    GUI.Hexagon hexagon = gui.createHexagon(0, 0, 0, 0, 0);
    assertNotNull(hexagon);
  }

  @Test
  void createHexButton_createsButtonCorrectly() {
    Button button = gui.createHexButton(0, 0, 0, 0, 0);
    assertNotNull(button);
  }

  @Test
  void createButtonWithAction_createsButtonWithActionCorrectly() {
    Button button = gui.createButtonWithAction(0, 0, 0, "0,0,0");
    assertNotNull(button);
  }
}
