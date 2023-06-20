package it.polimi.ingsw.model;

import it.polimi.ingsw.common.exceptions.NoNextPlayerException;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Board3Players;
import it.polimi.ingsw.model.board.Board4Players;
import it.polimi.ingsw.model.board.CreationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game g = new Game();
    Player p1, p2, p3;
    ArrayList<Player> ps = new ArrayList<>();
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
        assertNotEquals(p2, g.getCurrentPlayer());
        assertNotEquals(p3, g.getCurrentPlayer());

        // Test for first player.
        assertEquals(p2, g.getFirstPlayer());
        assertNotEquals(p1, g.getFirstPlayer());
        assertNotEquals(p3, g.getFirstPlayer());

        // Test for ID.
        assertEquals(p1, g.getPlayerByID(P1ID));
        assertEquals(p2, g.getPlayerByID(P2ID));
        assertEquals(p3, g.getPlayerByID(P3ID));
        assertNotEquals(p3, g.getPlayerByID(P1ID));
        assertNotEquals(p2, g.getPlayerByID(P3ID));
        assertNotEquals(p1, g.getPlayerByID(P2ID));

        // Test for players' getter.
        ps.add(p1);
        ps.add(p2);
        ps.add(p3);
        assertEquals(ps, g.getPlayers());

        // Test for players' activity.
        g.setActivePlayer(P1ID, true);
        assertTrue(p1.isActive());
        g.setActivePlayer(P2ID, false);
        assertFalse(p2.isActive());
        g.setActivePlayer(P1ID, false);
        g.setActivePlayer(P3ID, false);
        assertFalse(p1.isActive());
        assertFalse(p3.isActive());

        assertThrows(NoNextPlayerException.class, ()->g.nextPlayer());

        // Test for players' removal.
        g.removePlayer(p1);
        g.removePlayer(p2);
        g.removePlayer(p3);
        assertEquals(null, g.getPlayerByID(P1ID));
        assertEquals(null, g.getPlayerByID(P2ID));
        assertEquals(null, g.getPlayerByID(P3ID));
        assertThrows(NoNextPlayerException.class, ()-> g.nextPlayer());
    }

    @Test
    void creationFactoryTest(){
        CreationFactory cf = new CreationFactory();
        assertThrows(IllegalArgumentException.class, ()->cf.createBoard(0));
        assertThrows(IllegalArgumentException.class, ()->cf.createBoard(1));
        assertThrows(IllegalArgumentException.class, ()->cf.createBoard(5));
        assertDoesNotThrow(()->cf.createBoard(2));
        assertDoesNotThrow(()->cf.createBoard(3));
        assertDoesNotThrow(()->cf.createBoard(4));
    }
}