package nl.aylin.gameoflife.rules;

public interface CellRule {
    // True betekent: een bestaande cel blijft leven.
    boolean survives(int livingNeighbors);

    // True betekent: op een lege plek wordt een nieuwe cel geboren.
    boolean isBorn(int sameTypeNeighbors);
}
