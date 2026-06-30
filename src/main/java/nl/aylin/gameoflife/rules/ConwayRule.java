package nl.aylin.gameoflife.rules;

public class ConwayRule implements CellRule {
    @Override
    public boolean survives(int livingNeighbors) {
        // Klassieke Conway-regel: leven met 2 of 3 buren.
        return livingNeighbors == 2 || livingNeighbors == 3;
    }

    @Override
    public boolean isBorn(int sameTypeNeighbors) {
        // Nieuwe Conway-cel bij precies 3 Conway-buren.
        return sameTypeNeighbors == 3;
    }
}
