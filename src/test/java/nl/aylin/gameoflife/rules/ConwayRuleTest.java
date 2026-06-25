package nl.aylin.gameoflife.rules;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConwayRuleTest {
    private final ConwayRule rule = new ConwayRule();

    @Test
    void conwayCellIsBornWithExactlyThreeConwayNeighbors() {
        // Test de geboorteregel uit de opdracht.
        assertTrue(rule.isBorn(3));
    }

    @Test
    void conwayCellSurvivesWithTwoOrThreeLivingNeighbors() {
        // Test de overlevingsregel uit de opdracht.
        assertTrue(rule.survives(2));
        assertTrue(rule.survives(3));
    }

    @Test
    void conwayCellDiesByUnderpopulation() {
        assertFalse(rule.survives(0));
        assertFalse(rule.survives(1));
    }

    @Test
    void conwayCellDiesByOverpopulation() {
        assertFalse(rule.survives(4));
        assertFalse(rule.survives(8));
    }
}
