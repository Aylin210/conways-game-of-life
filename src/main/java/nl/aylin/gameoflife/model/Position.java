package nl.aylin.gameoflife.model;

import java.util.Objects;

public final class Position {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Position neighborAt(int rowOffset, int columnOffset) {
        // Maakt een buurpositie, bijvoorbeeld linksboven is (-1, -1).
        return new Position(row + rowOffset, column + columnOffset);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Position)) {
            return false;
        }
        Position position = (Position) other;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        // Nodig omdat Position als sleutel in de HashMap van GameBoard wordt gebruikt.
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
