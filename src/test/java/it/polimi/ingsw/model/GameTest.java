package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.CreationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game g = new Game();
    Player p1;
    Player p2;
    Player p3;
    ArrayList<Player> ps = new ArrayList<>();
    ArrayList<Player> activePs = new ArrayList<>();
    Bag bag = new Bag();
    static final int NUMPLAYERS = 3;
    static final int P1ID = 01;
    static final int P2ID = 03;
    static final int P3ID = 02;

    @BeforeEach
    void init() {
        // Creation of the players.
        p1 = new Player("Pippo", P1ID);
        p2 = new Player("Paperino", P2ID);
        p3 = new Player("Pluto", P3ID);

        g.createPlayer(p1);
        g.createPlayer(p2);
        g.createPlayer(p3);

        g.setNumOfPlayers(NUMPLAYERS);

        g.setFirstPlayer(p2);
    }

    @Test
    void gameTest(){
        // Test for number of players.
        assertEquals(NUMPLAYERS, g.getNumOfPlayers());

        // Test for the first player.
        assertEquals(p2, g.getFirstPlayer());
        assertNotEquals(p1, g.getFirstPlayer());
        assertNotEquals(p3, g.getFirstPlayer());

        // Test for current player.
        g.setCurrentPlayer(p1);
        assertEquals(p1, g.getCurrentPlayer());

        // Test for ID.
        assertEquals(p1, g.getPlayerByID(P1ID));
        assertEquals(p2, g.getPlayerByID(P2ID));
        assertEquals(p3, g.getPlayerByID(P3ID));
        assertNotEquals(p3, g.getPlayerByID(P1ID));
        assertNotEquals(p2, g.getPlayerByID(P3ID));
        assertNotEquals(p1, g.getPlayerByID(P2ID));

    }

    @Test
    void creationFactoryTest(){
        int numofplayers = 0;
        CreationFactory cf = new CreationFactory();
        g.createBoard();
        assertThrows(IllegalArgumentException.class, ()->cf.createBoard(numofplayers));
    }
}