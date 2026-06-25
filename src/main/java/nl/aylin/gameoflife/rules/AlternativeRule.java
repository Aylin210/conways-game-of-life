package nl.aylin.gameoflife.rules;

import nl.aylin.gameoflife.model.CellType;

public class AlternativeRule implements CellRule {
    @Override
    public CellType getCellType() {
        return CellType.ALTERNATIVE;
    }

    @Override
    public boolean survives(int livingNeighbors) {
        // Alternatieve regel uit de opdracht: overleven met 2, 3 of 4 buren.
        return livingNeighbors >= 2 && livingNeighbors <= 4;
    }

    @Override
    public boolean isBorn(int sameTypeNeighbors) {
        // Alternatieve regel uit de opdracht: geboorte bij exact 4 alternatieve buren.
        return sameTypeNeighbors == 4;
    }
}
