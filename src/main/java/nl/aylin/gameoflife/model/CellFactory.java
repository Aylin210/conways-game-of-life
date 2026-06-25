package nl.aylin.gameoflife.model;

public class CellFactory {
    public Cell create(CellType type, Position position) {
        // Factory pattern: op basis van het type wordt het juiste Cell-object gemaakt.
        if (type == CellType.CONWAY) {
            return new ConwayCell(position);
        }
        if (type == CellType.ALTERNATIVE) {
            return new AlternativeCell(position);
        }
        throw new IllegalArgumentException("Onbekend celtype: " + type);
    }
}
