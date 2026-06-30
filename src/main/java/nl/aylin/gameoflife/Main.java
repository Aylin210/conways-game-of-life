package nl.aylin.gameoflife;

import javax.swing.SwingUtilities;
import nl.aylin.gameoflife.view.GameOfLifeFrame;

public class Main {
    public static void main(String[] args) {
        // Start de Swing-app op de juiste thread.
        SwingUtilities.invokeLater(() -> new GameOfLifeFrame().setVisible(true));
    }
}
