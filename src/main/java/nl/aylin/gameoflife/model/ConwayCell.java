package nl.aylin.gameoflife.model;

public class ConwayCell extends Cell {
    public ConwayCell(Position position) {
        super(position, CellType.CONWAY);
    }

    @Override
    public Cell withPosition(Position position) {
        return new ConwayCell(position);
    }
}

