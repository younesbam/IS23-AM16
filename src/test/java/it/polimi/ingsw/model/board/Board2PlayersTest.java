package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class Board2PlayersTest {
    Board2Players b = new Board2Players();

    void boardTest(){
        assertTrue(b.isPickable(5,5));
//        assertFalse(b.refillNeeded());
        assertNotEquals(b.removeTile(4,5),Tile.UNAVAILABLE);
        assertNotEquals(b.removeTile(4,5), Tile.BLANK);
    }

}