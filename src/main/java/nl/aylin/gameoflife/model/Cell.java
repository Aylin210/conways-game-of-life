package nl.aylin.gameoflife.model;

public abstract class Cell {
    private final Position position;
    private final CellType type;

    protected Cell(Position position, CellType type) {
        this.position = position;
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public CellType getType() {
        return type;
    }

    public abstract Cell withPosition(Position position);
}

