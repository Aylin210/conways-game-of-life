package nl.aylin.gameoflife.model;

public class AlternativeCell extends Cell {
    public AlternativeCell(Position position) {
        super(position, CellType.ALTERNATIVE);
    }

    @Override
    public Cell withPosition(Position position) {
        return new AlternativeCell(position);
    }
}

