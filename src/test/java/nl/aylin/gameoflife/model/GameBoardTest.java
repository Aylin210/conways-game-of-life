package nl.aylin.gameoflife.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameBoardTest {
    @Test
    void cellCanBeAdded() {
        GameBoard board = new GameBoard(10, 10);
        Position position = new Position(5, 5);

        board.addCell(CellType.CONWAY, position);

        assertTrue(board.getCell(position).isPresent());
        assertEquals(CellType.CONWAY, board.getCell(position).get().getType());
    }

    @Test
    void cellCanBeRemoved() {
        GameBoard board = new GameBoard(10, 10);
        Position position = new Position(5, 5);
        board.addCell(CellType.CONWAY, position);

        board.removeCell(position);

        assertFalse(board.getCell(position).isPresent());
    }

    @Test
    void livingNeighborsAreCountedInAllEightDirections() {
        // Buren tellen moet horizontaal, verticaal en diagonaal werken.
        GameBoard board = new GameBoard(10, 10);
        Position center = new Position(5, 5);
        board.addCell(CellType.CONWAY, new Position(4, 4));
        board.addCell(CellType.CONWAY, new Position(4, 5));
        board.addCell(CellType.ALTERNATIVE, new Position(6, 6));

        assertEquals(3, board.countLivingNeighbors(center));
        assertEquals(2, board.countNeighborsOfType(center, CellType.CONWAY));
        assertEquals(1, board.countNeighborsOfType(center, CellType.ALTERNATIVE));
    }

    @Test
    void conwayBirthCreatesNewCell() {
        // Een lege plek met exact 3 Conway-buren krijgt een nieuwe Conway-cel.
        GameBoard board = new GameBoard(10, 10);
        Position birthPosition = new Position(5, 5);
        board.addCell(CellType.CONWAY, new Position(4, 5));
        board.addCell(CellType.CONWAY, new Position(5, 4));
        board.addCell(CellType.CONWAY, new Position(6, 5));

        board.nextGeneration();

        assertEquals(CellType.CONWAY, board.getCell(birthPosition).get().getType());
    }

    @Test
    void conwayCellSurvivesWithTwoLivingNeighbors() {
        GameBoard board = new GameBoard(10, 10);
        Position cellPosition = new Position(5, 5);
        board.addCell(CellType.CONWAY, cellPosition);
        board.addCell(CellType.CONWAY, new Position(5, 4));
        board.addCell(CellType.ALTERNATIVE, new Position(5, 6));

        board.nextGeneration();

        assertTrue(board.getCell(cellPosition).isPresent());
    }

    @Test
    void conwayCellDiesByUnderpopulation() {
        GameBoard board = new GameBoard(10, 10);
        Position cellPosition = new Position(5, 5);
        board.addCell(CellType.CONWAY, cellPosition);

        board.nextGeneration();

        assertFalse(board.getCell(cellPosition).isPresent());
    }

    @Test
    void conwayCellDiesByOverpopulation() {
        GameBoard board = new GameBoard(10, 10);
        Position cellPosition = new Position(5, 5);
        board.addCell(CellType.CONWAY, cellPosition);
        board.addCell(CellType.CONWAY, new Position(4, 4));
        board.addCell(CellType.CONWAY, new Position(4, 5));
        board.addCell(CellType.CONWAY, new Position(4, 6));
        board.addCell(CellType.CONWAY, new Position(5, 4));

        board.nextGeneration();

        assertFalse(board.getCell(cellPosition).isPresent());
    }

    @Test
    void alternativeBirthCreatesNewCell() {
        // Een lege plek met exact 4 alternatieve buren krijgt een nieuwe alternatieve cel.
        GameBoard board = new GameBoard(10, 10);
        Position birthPosition = new Position(5, 5);
        board.addCell(CellType.ALTERNATIVE, new Position(4, 5));
        board.addCell(CellType.ALTERNATIVE, new Position(5, 4));
        board.addCell(CellType.ALTERNATIVE, new Position(5, 6));
        board.addCell(CellType.ALTERNATIVE, new Position(6, 5));

        board.nextGeneration();

        assertEquals(CellType.ALTERNATIVE, board.getCell(birthPosition).get().getType());
    }

    @Test
    void nextGenerationUpdatesWholeBoard() {
        // Klassieke blinker-test: verticaal patroon wordt horizontaal.
        GameBoard board = new GameBoard(10, 10);
        board.addCell(CellType.CONWAY, new Position(4, 5));
        board.addCell(CellType.CONWAY, new Position(5, 5));
        board.addCell(CellType.CONWAY, new Position(6, 5));

        board.nextGeneration();

        assertTrue(board.getCell(new Position(5, 4)).isPresent());
        assertTrue(board.getCell(new Position(5, 5)).isPresent());
        assertTrue(board.getCell(new Position(5, 6)).isPresent());
        assertEquals(3, board.getLivingCellCount());
    }
}
