package nl.aylin.gameoflife.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import nl.aylin.gameoflife.model.Cell;
import nl.aylin.gameoflife.model.CellType;
import nl.aylin.gameoflife.model.GameBoard;
import nl.aylin.gameoflife.model.Position;

public class GamePanel extends JPanel {
    private static final int CELL_SIZE = 16;
    private static final Color GRID_COLOR = new Color(224, 228, 234);
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private final GameBoard board;
    private ToolMode toolMode = ToolMode.PLACE_CONWAY;
    private Runnable changeListener = () -> { };

    public GamePanel(GameBoard board) {
        this.board = board;
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(board.getColumns() * CELL_SIZE, board.getRows() * CELL_SIZE));
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

    public void setChangeListener(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        // Swing roept paintComponent aan wanneer het speelveld opnieuw getekend moet worden.
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawGrid(g2);
        drawCells(g2);
        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        Rectangle clip = g2.getClipBounds();
        // Alleen het zichtbare deel van het 1000x1000 bord wordt getekend.
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
        for (Cell cell : board.getCellsSnapshot()) {
            int x = cell.getPosition().getColumn() * CELL_SIZE;
            int y = cell.getPosition().getRow() * CELL_SIZE;
            if (!clip.intersects(x, y, CELL_SIZE, CELL_SIZE)) {
                continue;
            }

            g2.setColor(cell.getType().getColor());
            if (cell.getType() == CellType.CONWAY) {
                // Conway-cellen zijn vierkantjes.
                g2.fillRect(x + 3, y + 3, CELL_SIZE - 5, CELL_SIZE - 5);
            } else {
                // Alternatieve cellen zijn rondjes.
                g2.fillOval(x + 3, y + 3, CELL_SIZE - 5, CELL_SIZE - 5);
            }
        }
    }

    private void handleMouse(MouseEvent event) {
        // Muisklik omrekenen van pixels naar rij/kolom op het bord.
        int row = event.getY() / CELL_SIZE;
        int column = event.getX() / CELL_SIZE;
        Position position = new Position(row, column);

        if (event.getButton() == MouseEvent.BUTTON3 || toolMode == ToolMode.REMOVE) {
            // Rechtsklik verwijdert altijd een cel.
            board.removeCell(position);
        } else if (toolMode == ToolMode.PLACE_CONWAY) {
            board.addCell(CellType.CONWAY, position);
        } else if (toolMode == ToolMode.PLACE_ALTERNATIVE) {
            board.addCell(CellType.ALTERNATIVE, position);
        }

        changeListener.run();
        repaint();
    }
}
