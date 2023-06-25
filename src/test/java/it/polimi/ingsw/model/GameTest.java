package it.polimi.ingsw.model;

import it.polimi.ingsw.common.exceptions.NoNextPlayerException;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Board3Players;
import it.polimi.ingsw.model.board.Board4Players;
import it.polimi.ingsw.model.board.CreationFactory;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game g;
    Player p1, p2, p3;
    ArrayList<Player> ps = new ArrayList<>();
    static final int NUMPLAYERS = 3;
    static final int P1ID = 01;
    static final int P2ID = 03;
    static final int P3ID = 02;

    @BeforeEach
    void init() {
        // Creating game's instance.
        g = new Game();

        // Creating the board.
        Bag bag = new Bag();
        bag = g.getBag();

        // Creating the common goal cards.
        List<CommonGoalCard> cards = new ArrayList<>();
        cards = g.getCommonGoalCards();

        // Creation of the players.
        p1 = new Player("Pippo", P1ID);
        p2 = new Player("Paperino", P2ID);
        p3 = new Player("Pluto", P3ID);

        g.createPlayer(p1);
        g.createPlayer(p2);
        g.createPlayer(p3);

        // Setting all players as active.
        g.setActivePlayer(P1ID, true);
        g.setActivePlayer(P2ID, true);
        g.setActivePlayer(P3ID, true);

        g.setNumOfPlayers(NUMPLAYERS);

        g.setFirstPlayer(p2);

        g.setCurrentPlayer(p2);
    }

    @Test
    void testsOnPlayers() throws NoNextPlayerException {
        // Tests on players' list.
        ps = g.getPlayers();
        assertNotNull(ps);
        assertEquals(NUMPLAYERS, ps.size());
        assertEquals(g.getNumOfPlayers(), ps.size());

        // Tests on first player.
        assertEquals(p2, g.getFirstPlayer());
        assertNotEquals(p1, g.getFirstPlayer());
        assertNotEquals(p3, g.getFirstPlayer());

        // Tests on current player.
        assertEquals(p2, g.getCurrentPlayer());
        assertNotEquals(p1, g.getCurrentPlayer());
        assertNotEquals(p3, g.getCurrentPlayer());

        // Tests on playerID.
        assertEquals(p2, g.getPlayerByID(P2ID));
        assertEquals(p3, g.getPlayerByID(P3ID));
        assertEquals(p1, g.getPlayerByID(P1ID));

        // Check that all players have played 0 turns.
        assertEquals(0, g.getPlayerByID(P2ID).getNumOfTurns());
        assertEquals(0, g.getPlayerByID(P1ID).getNumOfTurns());
        assertEquals(0, g.getPlayerByID(P3ID).getNumOfTurns());

        // Check that after nextPlayer() P2 has played 1 turn and that P3 is next player.
        g.nextPlayer();
        assertEquals(1, g.getPlayerByID(P2ID).getNumOfTurns());
        assertEquals(p3, g.getCurrentPlayer());

        // Check that now both P2 and P3 have played 1 turn.
        g.nextPlayer();
        assertEquals(1, g.getPlayerByID(P3ID).getNumOfTurns());
        assertEquals(p1, g.getCurrentPlayer());

        // Check that now all players have played 1 turn and current player is P2 again.
        g.nextPlayer();
        assertEquals(1, g.getPlayerByID(P1ID).getNumOfTurns());
        assertEquals(p2, g.getCurrentPlayer());

        // Setting p2 as inactive to check that now current player is P3.
        g.setActivePlayer(P2ID, false);
        g.nextPlayer();
        assertEquals(1, g.getPlayerByID(P2ID).getNumOfTurns());
        assertEquals(p3, g.getCurrentPlayer());

        // P3 now has played 2 turns, P2 has still played only 1 turn and P1 is current player.
        g.nextPlayer();
        assertEquals(1, g.getPlayerByID(P2ID).getNumOfTurns());
        assertEquals(2, g.getPlayerByID(P3ID).getNumOfTurns());
        assertEquals(p1, g.getCurrentPlayer());

        // Setting also p1 as inactive in order to check that the method throws an exception.
        g.setActivePlayer(P1ID, false);
        assertThrows(NoNextPlayerException.class, ()->g.nextPlayer());

        // Tests on player's removal.
        g.removePlayer(p1);
        g.removePlayer(p2);
        g.removePlayer(p3);
        ps = g.getPlayers();
        assertEquals(0, ps.size());
        assertNull(g.getPlayerByID(P1ID));
        assertNull(g.getPlayerByID(P2ID));
        assertNull(g.getPlayerByID(P3ID));
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