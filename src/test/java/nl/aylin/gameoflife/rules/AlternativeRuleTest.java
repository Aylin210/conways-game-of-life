package nl.aylin.gameoflife.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AlternativeRuleTest {
    private final AlternativeRule rule = new AlternativeRule();

    @Test
    void alternativeCellIsBornWithExactlyFourAlternativeNeighbors() {
        // Test de alternatieve geboorteregel uit de opdracht.
        assertTrue(rule.isBorn(4));
    }

    @Test
    void alternativeCellSurvivesWithTwoThreeOrFourLivingNeighbors() {
        // Test de alternatieve overlevingsregel uit de opdracht.
        assertTrue(rule.survives(2));
        assertTrue(rule.survives(3));
        assertTrue(rule.survives(4));
    }

    @Test
    void alternativeCellDiesWithFewerThanTwoLivingNeighbors() {
        assertFalse(rule.survives(0));
        assertFalse(rule.survives(1));
    }

    @Test
    void alternativeCellDiesWithMoreThanFourLivingNeighbors() {
        assertFalse(rule.survives(5));
        assertFalse(rule.survives(8));
    }
}
