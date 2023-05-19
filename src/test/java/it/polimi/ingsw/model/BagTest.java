package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BagTest {

    Bag b = new Bag();

    /*
    Testing that methods don't return a null pointer.
     */
    @Test
    void bagTest() {
        assertNotEquals(b.pickCommonGoalCard(3), null);
        assertNotEquals(b.pickPersonalGoalCard(), null);
    }
}