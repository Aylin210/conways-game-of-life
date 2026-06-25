package nl.aylin.gameoflife;

import javax.swing.SwingUtilities;
import nl.aylin.gameoflife.ui.GameOfLifeFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameOfLifeFrame().setVisible(true));
    }
}

