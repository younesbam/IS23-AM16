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

import static it.polimi.ingsw.Const.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * GameTest class tests class Game in model.
 * @see Game
 */
class GameTest {
    Game g;
    Bag bag = new Bag();
    Player p1, p2, p3;
    ArrayList<Player> ps = new ArrayList<>();
    static final int NUMPLAYERS = 3;
    static final int P1ID = 01;
    static final int P2ID = 03;
    static final int P3ID = 02;

    /**
     * This method instantiates the game to run tests.
     */
    @BeforeEach
    void init() {
        // Creating game's instance.
        g = new Game();

        // Creating the board.
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

        ps.add(p1);
        ps.add(p2);
        ps.add(p3);
    }

    /**
     * This method tests method removePlayer() in Game class.
     * @see Game#removePlayer(Player)
     */
    @Test
    void removePlayerTest(){
        g.removePlayer(p1);
        assertEquals(p2, g.getPlayers().get(0));
        assertEquals(p3, g.getPlayers().get(1));
        assertEquals(NUMPLAYERS-1, g.getPlayers().size());

        g.removePlayer(p2);
        assertEquals(p3, g.getPlayers().get(0));
        assertEquals(NUMPLAYERS-2, g.getPlayers().size());

        g.removePlayer(p3);
        assertEquals(0, g.getPlayers().size());

        // Placing back the players.
        g.createPlayer(p1);
        g.createPlayer(p2);
        g.createPlayer(p3);
    }

    /**
     * This method tests methods setNumOfPlayers() and getNumOfPlayers() in Game class.
     * @see Game#setNumOfPlayers(int)
     * @see Game#getNumOfPlayers()
     */
    @Test
    void numOfPlayersTest(){
        assertEquals(NUMPLAYERS, g.getNumOfPlayers());

        g.setNumOfPlayers(0);
        assertEquals(0, g.getNumOfPlayers());

        g.setNumOfPlayers(MINPLAYERS);
        assertEquals(MINPLAYERS, g.getNumOfPlayers());

        g.setNumOfPlayers(MAXPLAYERS);
        assertEquals(MAXPLAYERS, g.getNumOfPlayers());
    }

    /**
     * This method tests methods setFirstPlayer() and getFirstPlayer() in Game class.
     * @see Game#setFirstPlayer(Player)
     * @see Game#getFirstPlayer()
     */
    @Test
    void firstPlayerTest(){
        assertNotNull(g.getFirstPlayer());

        assertEquals(p2, g.getFirstPlayer());
        assertNotEquals(p1, g.getFirstPlayer());
        assertNotEquals(p3, g.getFirstPlayer());

        g.setFirstPlayer(p1);
        assertNotEquals(p2, g.getFirstPlayer());
        assertEquals(p1, g.getFirstPlayer());
        assertNotEquals(p3, g.getFirstPlayer());

        g.setFirstPlayer(p3);
        assertNotEquals(p1, g.getFirstPlayer());
        assertNotEquals(p2, g.getFirstPlayer());
        assertEquals(p3, g.getFirstPlayer());
    }

    /**
     * This method tests methods getCurrentPlayer() and setCurrentPlayer() in Game class.
     * @see Game#setCurrentPlayer(Player)
     * @see Game#getCurrentPlayer()
     */
    @Test
    void currentPlayerTest(){
        assertNotNull(g.getCurrentPlayer());

        assertEquals(p2, g.getCurrentPlayer());
        assertNotEquals(p1, g.getCurrentPlayer());
        assertNotEquals(p3, g.getCurrentPlayer());

        g.setCurrentPlayer(p1);
        assertEquals(p1, g.getCurrentPlayer());
        assertNotEquals(p2, g.getCurrentPlayer());
        assertNotEquals(p3, g.getCurrentPlayer());

        g.setCurrentPlayer(p3);
        assertEquals(p3, g.getCurrentPlayer());
        assertNotEquals(p1, g.getCurrentPlayer());
        assertNotEquals(p2, g.getCurrentPlayer());
    }

    /**
     * This method tests methods getBoard(), getCommonGoalCards() and getBag() in Game class.
     * @see Game#getBoard()
     * @see Game#getCommonGoalCards()
     * @see Game#getBag()
     */
    @Test
    void otherGettersTests(){
        assertNotNull(g.getCommonGoalCards());
        // Size is 0 because cards haven't been created yet.
        assertEquals(0, g.getCommonGoalCards().size());

        // The board is null because it hasn't been created yet.
        assertNull(g.getBoard());

        // The bag isn't null because it has been created in the instantiation.
        assertNotNull(g.getBag());
        assertEquals(bag, g.getBag());
    }

    /**
     * This method tests method getPlayerByID() in Game class.
     * @see Game#getPlayerByID(int)
     */
    @Test
    void getPlayerByIDTest(){
        assertNotNull(g.getPlayerByID(P1ID));
        assertNotNull(g.getPlayerByID(P2ID));
        assertNotNull(g.getPlayerByID(P3ID));

        assertEquals(p1, g.getPlayerByID(P1ID));
        assertEquals(p2, g.getPlayerByID(P2ID));
        assertEquals(p3, g.getPlayerByID(P3ID));

        assertNotEquals(p1, g.getPlayerByID(P3ID));
        assertNotEquals(p2, g.getPlayerByID(P1ID));
        assertNotEquals(p3, g.getPlayerByID(P2ID));
    }

    /**
     * This method tests method getPlayers() in Game class.
     * @see Game#getPlayers()
     */
    @Test
    void getPlayersTest(){
        assertNotNull(g.getPlayers());
        assertEquals(ps, g.getPlayers());
        assertEquals(NUMPLAYERS, g.getPlayers().size());
        assertEquals(ps.size(), g.getPlayers().size());
    }

    /**
     * This method tests method nextPlayer() in Game class.
     * @see Game#nextPlayer()
     */
    @Test
    void nextPlayerTest() throws NoNextPlayerException{
        // The game has just started, so every player has played 0 turns.
        assertEquals(0, g.getPlayerByID(P1ID).getNumOfTurns());
        assertEquals(0, g.getPlayerByID(P2ID).getNumOfTurns());
        assertEquals(0, g.getPlayerByID(P3ID).getNumOfTurns());

        // Calling next player, so the turn passes to player 3 and player 2 should have played 1 turn.
        g.nextPlayer();
        assertEquals(1, g.getPlayerByID(P2ID).getNumOfTurns());
        assertEquals(p3, g.getCurrentPlayer());
        assertEquals(0, g.getPlayerByID(P3ID).getNumOfTurns());
        assertEquals(0, g.getPlayerByID(P1ID).getNumOfTurns());

        // Calling next player, so the turn passes to player 1 and both player 2 and 3 should have played 1 turn.
        g.nextPlayer();
        assertEquals(1, g.getPlayerByID(P2ID).getNumOfTurns());
        assertEquals(1, g.getPlayerByID(P3ID).getNumOfTurns());
        assertEquals(0, g.getPlayerByID(P1ID).getNumOfTurns());
        assertEquals(p1, g.getCurrentPlayer());

        // Calling next player, so the turn passes back to player 2 and all players have played 1 turn.
        g.nextPlayer();
        assertEquals(1, g.getPlayerByID(P1ID).getNumOfTurns());
        assertEquals(1, g.getPlayerByID(P2ID).getNumOfTurns());
        assertEquals(1, g.getPlayerByID(P3ID).getNumOfTurns());
        assertEquals(p2, g.getCurrentPlayer());

        // Setting p2 as inactive to check that turn passes to p3 and p2 has still played 1 turn.
        g.setActivePlayer(P2ID, false);
        assertFalse(p2.isActive());
        g.nextPlayer();
        assertEquals(p3, g.getCurrentPlayer());
        assertEquals(1, g.getPlayerByID(P2ID).getNumOfTurns());

        // Calling again next player to check that player 3 has played 2 turns, player 2 still 1, and p1 is current player.
        g.nextPlayer();
        assertEquals(1, g.getPlayerByID(P2ID).getNumOfTurns());
        assertEquals(2, g.getPlayerByID(P3ID).getNumOfTurns());
        assertEquals(p1, g.getCurrentPlayer());

        // Setting also p3 as inactive to check that the method throws an exception.
        g.setActivePlayer(P3ID, false);
        assertFalse(p3.isActive());
        assertThrows(NoNextPlayerException.class, ()->g.nextPlayer());

        // Setting p2 as active again and check that p1 is still the current player.
        g.setActivePlayer(P2ID, true);
        assertTrue(p2.isActive());
        assertEquals(p1, g.getCurrentPlayer());
    }

    /**
     * Test on board's creation.
     * @see Game#createBoard()
     */
    @Test
    void createBoardTest(){
        g.createBoard();

        assertNotNull(g.getBoard());
    }

    /**
     * This method tests method setActivePlayer() in Game class.
     * @see Game#setActivePlayer(int, boolean)
     */
    @Test
    void setActivePlayerTest(){
        g.setActivePlayer(P1ID,true);
        assertTrue(p1.isActive());

        g.setActivePlayer(P2ID, true);
        assertTrue(p2.isActive());

        g.setActivePlayer(P3ID, true);
        assertTrue(p3.isActive());

        g.setActivePlayer(P1ID, false);
        assertFalse(p1.isActive());

        g.setActivePlayer(P2ID, false);
        assertFalse(p2.isActive());

        g.setActivePlayer(P3ID, false);
        assertFalse(p3.isActive());
    }

    /**
     * This method tests the creation factory method.
     */
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