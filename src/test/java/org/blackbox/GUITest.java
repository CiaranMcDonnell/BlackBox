package org.blackbox;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import javafx.scene.shape.Circle;
import org.junit.jupiter.api.Test;

class GUITest {
  private HexagonManager mockHexManager;

  @Test
  void setHexagonManager_setsManagerCorrectly() {
    mockHexManager = mock(HexagonManager.class);
    GUI.setHexagonManager(mockHexManager);
    assertNotNull(GUI.getHexagonManager());
  }

  @Test
  void getHexHeight_returnsCorrectHeight() {
    double expectedHeight = Math.sqrt(3) * 30;
    assertEquals(expectedHeight, GUI.getHexHeight());
  }

  @Test
  void getHexSize_returnsCorrectSize() {
    double expectedSize = 30;
    assertEquals(expectedSize, GUI.getHexSize());
  }

  @Test
  void addCircle_addsCircleToRoot() {
    Circle circle = new Circle();
    GUI.addCircle(circle);
    assertTrue(GUI.getCircles().contains(circle));
  }

  @Test
  void main_executesWithoutErrors() {
    mockHexManager = mock(HexagonManager.class);
    GUI.setHexagonManager(mockHexManager);
    assertDoesNotThrow(() -> GUI.main(new String[] {}));
  }

  @Test
  void addCircle_doesNotAddCircleWhenNull() {
    assertThrows(NullPointerException.class, () -> GUI.addCircle(null));
  }

  @Test
  void setHexagonManager_doesNotSetWhenNull() {
    assertThrows(NullPointerException.class, () -> GUI.setHexagonManager(null));
  }
}
