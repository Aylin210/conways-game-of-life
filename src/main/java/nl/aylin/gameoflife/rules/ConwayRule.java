package nl.aylin.gameoflife.rules;

import nl.aylin.gameoflife.model.CellType;

public class ConwayRule implements CellRule {
    @Override
    public CellType getCellType() {
        return CellType.CONWAY;
    }

    @Override
    public boolean survives(int livingNeighbors) {
        // Klassieke Conway-regel: overleven met 2 of 3 levende buren.
        return livingNeighbors == 2 || livingNeighbors == 3;
    }

    @Override
    public boolean isBorn(int sameTypeNeighbors) {
        // Klassieke Conway-regel: geboorte bij exact 3 Conway-buren.
        return sameTypeNeighbors == 3;
    }
}
