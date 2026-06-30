package nl.aylin.gameoflife.model;

import java.util.Objects;

public final class Position {
    // Rij en kolom samen vertellen waar iets op het bord staat.
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
        // Maak een nieuwe positie naast deze positie.
        // Bijvoorbeeld: offset (-1, 0) is 1 rij omhoog.
        return new Position(row + rowOffset, column + columnOffset);
    }

    @Override
    public boolean equals(Object other) {
        // Nodig zodat twee Position-objecten met dezelfde rij/kolom als gelijk worden gezien.
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
