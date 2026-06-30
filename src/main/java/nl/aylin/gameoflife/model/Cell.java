package nl.aylin.gameoflife.model;

public class Cell {
    // Een cel weet alleen waar hij staat en welk type hij is.
    private final Position position;
    private final CellType type;

    public Cell(Position position, CellType type) {
        this.position = position;
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public CellType getType() {
        return type;
    }
}
