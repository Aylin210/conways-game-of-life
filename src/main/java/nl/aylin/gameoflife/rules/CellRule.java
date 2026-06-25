package nl.aylin.gameoflife.rules;

import nl.aylin.gameoflife.model.CellType;

public interface CellRule {
    CellType getCellType();

    // Strategy pattern: elk celtype bepaalt zelf wanneer het blijft leven.
    boolean survives(int livingNeighbors);

    // Strategy pattern: elk celtype bepaalt zelf wanneer een nieuwe cel ontstaat.
    boolean isBorn(int sameTypeNeighbors);
}
