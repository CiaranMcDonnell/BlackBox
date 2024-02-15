package org.blackbox;

// ansi color codes

public class main {
    public static void main(String[] args) {
        game myGame = new game();
        myGame.atomSelection();
        javafx.application.Application.launch(gui.class, args);
    }
}