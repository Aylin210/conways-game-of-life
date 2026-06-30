package nl.aylin.gameoflife.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import nl.aylin.gameoflife.controller.GameController;
import nl.aylin.gameoflife.controller.ToolMode;
import nl.aylin.gameoflife.model.Cell;
import nl.aylin.gameoflife.model.CellType;
import nl.aylin.gameoflife.model.GameBoard;
import nl.aylin.gameoflife.model.Position;

public class GamePanel extends JPanel {
    // Elke cel wordt als een vakje van 16 bij 16 pixels getekend.
    private static final int CELL_SIZE = 16;
    private static final Color GRID_COLOR = new Color(224, 228, 234);
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private final GameController controller;
    private final GameBoard board;
    private ToolMode toolMode = ToolMode.PLACE_CONWAY;

    public GamePanel(GameController controller) {
        this.controller = controller;
        this.board = controller.getBoard();
        setBackground(BACKGROUND_COLOR);

        // Het bord is groot, daarom zit dit panel in een JScrollPane.
        setPreferredSize(new Dimension(board.getColumns() * CELL_SIZE, board.getRows() * CELL_SIZE));

        // Als iemand klikt, voeren we de gekozen actie uit.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                handleMouse(event);
            }
        });
    }

    public void setToolMode(ToolMode toolMode) {
        this.toolMode = toolMode;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // Graphics2D geeft net iets mooiere tekenopties dan gewone Graphics.
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawGrid(g2);
        drawCells(g2);
        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        Rectangle clip = g2.getClipBounds();

        // Clip is het deel dat nu zichtbaar is.
        // We tekenen alleen dat stuk, anders zou een 1000x1000 bord traag worden.
        int firstColumn = Math.max(0, clip.x / CELL_SIZE);
        int lastColumn = Math.min(board.getColumns(), (clip.x + clip.width) / CELL_SIZE + 2);
        int firstRow = Math.max(0, clip.y / CELL_SIZE);
        int lastRow = Math.min(board.getRows(), (clip.y + clip.height) / CELL_SIZE + 2);

        g2.setColor(GRID_COLOR);
        for (int column = firstColumn; column <= lastColumn; column++) {
            int x = column * CELL_SIZE;
            g2.drawLine(x, firstRow * CELL_SIZE, x, lastRow * CELL_SIZE);
        }
        for (int row = firstRow; row <= lastRow; row++) {
            int y = row * CELL_SIZE;
            g2.drawLine(firstColumn * CELL_SIZE, y, lastColumn * CELL_SIZE, y);
        }
    }

    private void drawCells(Graphics2D g2) {
        Rectangle clip = g2.getClipBounds();

        // Loop door alle levende cellen en teken ze op hun plek.
        for (Cell cell : board.getCellsSnapshot()) {
            int x = cell.getPosition().getColumn() * CELL_SIZE;
            int y = cell.getPosition().getRow() * CELL_SIZE;

            // Cellen buiten het zichtbare scherm hoeven we niet te tekenen.
            if (!clip.intersects(x, y, CELL_SIZE, CELL_SIZE)) {
                continue;
            }

            int padding = 3;
            int size = CELL_SIZE - 5;
            g2.setColor(cell.getType().getColor());

            // Conway is een vierkant, alternatief is een rondje.
            if (cell.getType() == CellType.CONWAY) {
                g2.fillRect(x + padding, y + padding, size, size);
            } else {
                g2.fillOval(x + padding, y + padding, size, size);
            }
        }
    }

    private void handleMouse(MouseEvent event) {
        // Zet muis-pixels om naar een rij en kolom op het bord.
        int row = event.getY() / CELL_SIZE;
        int column = event.getX() / CELL_SIZE;
        Position position = new Position(row, column);

        // Rechtsklik verwijdert altijd.
        // Anders gebruiken we de gekozen actie uit de dropdown.
        if (event.getButton() == MouseEvent.BUTTON3 || toolMode == ToolMode.REMOVE) {
            controller.removeCell(position);
        } else if (toolMode == ToolMode.PLACE_CONWAY) {
            controller.placeCell(CellType.CONWAY, position);
        } else if (toolMode == ToolMode.PLACE_ALTERNATIVE) {
            controller.placeCell(CellType.ALTERNATIVE, position);
        }
    }
}
