package org.blackbox;

public class BlackBox {
  public static void main(String[] args) {

    HexagonManager hexManager = new HexagonManager();
    GUI.setHexagonManager(hexManager);
    javafx.application.Application.launch(GUI.class, args);
  }
}
