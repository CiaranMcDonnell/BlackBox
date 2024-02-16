package org.blackbox;

public class blackBox {
  public static void main(String[] args) {
    hexagonManager hexManager = new hexagonManager();
    gui.setHexagonManager(hexManager);
    javafx.application.Application.launch(gui.class, args);
  }
}
