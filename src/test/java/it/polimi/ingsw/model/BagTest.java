package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BagTest class tests Bag class in model.
 * @see Bag
 */
class BagTest {

    Bag b;

    /**
     * This method instantiates the bag to run tests.
     */
    @BeforeEach
    void init(){
        b = new Bag();
    }

    /**
     * This method tests that methods pickCommonGoalCard() and pickPersonalGoalCard() in Bag class
     * don't return null.
     * @see Bag#pickCommonGoalCard(int)
     * @see Bag#pickPersonalGoalCard()
     */
    @Test
    void pickCardsTests() {
        assertNotEquals(b.pickCommonGoalCard(3), null);
        assertNotEquals(b.pickPersonalGoalCard(), null);
    }
}