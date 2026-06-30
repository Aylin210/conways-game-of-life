package nl.aylin.gameoflife.rules;

public class AlternativeRule implements CellRule {
    @Override
    public boolean survives(int livingNeighbors) {
        // Alternatieve cel blijft leven met 2, 3 of 4 buren.
        return livingNeighbors >= 2 && livingNeighbors <= 4;
    }

    @Override
    public boolean isBorn(int sameTypeNeighbors) {
        // Nieuwe alternatieve cel bij precies 4 alternatieve buren.
        return sameTypeNeighbors == 4;
    }
}
