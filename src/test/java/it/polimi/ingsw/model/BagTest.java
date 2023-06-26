package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BagTest class tests Bag class in model.
 * @see Bag
 */
class BagTest {

    Bag b = new Bag();

    /**
     * This method tests that the Board's methods don't return null.
     */
    @Test
    void bagTest() {
        assertNotEquals(b.pickCommonGoalCard(3), null);
        assertNotEquals(b.pickPersonalGoalCard(), null);
    }
}