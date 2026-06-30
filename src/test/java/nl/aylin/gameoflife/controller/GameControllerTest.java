package nl.aylin.gameoflife.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.aylin.gameoflife.clock.GameClock;
import nl.aylin.gameoflife.model.CellType;
import nl.aylin.gameoflife.model.GameBoard;
import nl.aylin.gameoflife.model.Position;
import org.junit.jupiter.api.Test;

class GameControllerTest {
    @Test
    void controllerPlacesCellInModel() {
        // Arrange
        GameBoard board = new GameBoard(10, 10);
        GameController controller = new GameController(board, new GameClock());
        Position position = new Position(4, 4);

        // Act
        controller.placeCell(CellType.CONWAY, position);

        // Assert
        assertTrue(board.getCell(position).isPresent());
        assertEquals(CellType.CONWAY, board.getCell(position).get().getType());
    }

    @Test
    void controllerTickAdvancesModel() {
        // Arrange: klassiek blinker-patroon.
        GameBoard board = new GameBoard(10, 10);
        GameController controller = new GameController(board, new GameClock());
        board.addCell(CellType.CONWAY, new Position(4, 5));
        board.addCell(CellType.CONWAY, new Position(5, 5));
        board.addCell(CellType.CONWAY, new Position(6, 5));

        // Act
        controller.onTick(1);

        // Assert
        assertTrue(board.getCell(new Position(5, 4)).isPresent());
        assertTrue(board.getCell(new Position(5, 5)).isPresent());
        assertTrue(board.getCell(new Position(5, 6)).isPresent());
    }

    @Test
    void controllerResetClearsModel() {
        // Arrange
        GameBoard board = new GameBoard(10, 10);
        GameController controller = new GameController(board, new GameClock());
        controller.placeCell(CellType.ALTERNATIVE, new Position(2, 2));

        // Act
        controller.reset();

        // Assert
        assertEquals(0, board.getLivingCellCount());
        assertEquals(0, controller.getTickNumber());
    }
}
