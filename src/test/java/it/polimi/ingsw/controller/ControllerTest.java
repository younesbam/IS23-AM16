package it.polimi.ingsw.controller;

import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PrintCardsAction;
import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorClassification;
import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import it.polimi.ingsw.model.cards.EqualCorners;
import it.polimi.ingsw.model.cards.EqualCross;
import it.polimi.ingsw.model.cards.PersonalGoalCard;
import it.polimi.ingsw.server.GameHandler;
import it.polimi.ingsw.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.Const.*;
import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    Controller controller;
    Game game;
    Player p1, p2, p3;
    ArrayList<Player> players = new ArrayList<>();
    List<CommonGoalCard> commCards = new ArrayList<>();
    LinkedList<PersonalGoalCard> persCards = new LinkedList<>();
    int numOfPlayers = 3;
    PropertyChangeSupport pcsController = new PropertyChangeSupport(this);
    Phase phase;

    /**
     * This class represents a hint of the GameHandler.
     * It's used to run tests without using communication tools.
     */
    private static class GameHandlerHint extends GameHandler{
        /**
         * GameHandler constructor.
         *
         * @param server
         */
        public GameHandlerHint(Server server) {
            super(server);
        }

        @Override
        public void sendToEveryone(Answer answer){
            // Nothing.
        }

        @Override
        public void sendToEveryoneExcept(Answer answer, int notToHim){
            // Nothing.
        }

        @Override
        public void sendToPlayer(Answer answer, int playerID){
            // Nothing.
        }
    }

    /**
     * This class is used to create a game in which personal and common goal cards are known in order to
     * run the tests.
     */
    private static class GameHint extends Game {
        /**
         * GameHint constructor.
         */
        public GameHint(){super();};

        /**
         * In this case we choose common goal cards 1 and 8 to be the goals to achieve.
         * @return Cards 1 and 8 in a list.
         */
        @Override
        public List<CommonGoalCard> getCommonGoalCards(){
            List<CommonGoalCard> list = new ArrayList<>();
            list.add(new EqualCorners(8));
            list.add(new EqualCross(1));

            return list;
        }
    }

    @BeforeEach
    void testSetup(){
        game = new Game(); // Game's creation.
        controller = new Controller(new GameHandlerHint(null), game); // Controller's creation.

        // Player 1 creation.
        p1 = new Player("Pippo", 01);
        game.createPlayer(p1);
        p1.setChair(true);

        // Player 2 creation.
        p2 = new Player("Pluto", 02);
        game.createPlayer(p2);
        p2.setChair(false);

        // Player 3 creation.
        p3 = new Player("Paperino", 03);
        game.createPlayer(p3);
        p3.setChair(false);

        // Creation of players' list.
        players = game.getPlayers();
        game.setNumOfPlayers(players.size());

        // First player and current player are player 1, who has the chair.
        game.setFirstPlayer(p1);
        controller.setCurrentPlayer(p1);

        // Setting all players as active.
        for(int i=0; i< players.size(); i++)
            game.getPlayers().get(i).setActive(true);

        // Common goal cards' creation.
        commCards = game.getCommonGoalCards();

        /*
        Personal goal card's creation.
        Player 1 has card 1, player 2 has card 2 and player 3 has card 3.
         */
        JSONParser jsonParser = new JSONParser("initSetup.json");
        persCards = jsonParser.getPersonalGoalCards();
        for(int i=0; i<players.size(); i++)
            game.getPlayers().get(i).setPersonalGoalCard(persCards.get(i));

        // Board's creation.
        game.createBoard();

        // Property change listener declaration.
        pcsController.addPropertyChangeListener(controller);
    }

    @Test
    public void getGameTest(){
        assertNotNull(controller.getGame());
        assertEquals(game, controller.getGame());
    }

    @Test
    void phaseTest(){
        Phase phase = Phase.LOBBY;
        controller.setPhase(phase);
        assertNotNull(controller.getPhase());
        assertEquals(phase, controller.getPhase());

        phase = Phase.SETUP;
        controller.setPhase(phase);
        assertNotNull(controller.getPhase());
        assertEquals(phase, controller.getPhase());
    }

    @Test
    void currentPlayerTest(){
        // Check that player 1 is current player.
        //assertEquals(game.getCurrentPlayer(), controller.getCurrentPlayer());
        //assertNotEquals(p2, controller.getCurrentPlayer());
        //assertNotEquals(p3, controller.getCurrentPlayer());

        controller.setCurrentPlayer(p2);
        //assertEquals(game.getCurrentPlayer(), controller.getCurrentPlayer());

        controller.setCurrentPlayer(p3);
        //assertEquals(game.getCurrentPlayer(), controller.getCurrentPlayer());
    }

    @Test
    public void pointsTest(){
        controller.updateTotalPoints();
        //assertEquals(0, controller.getCurrentPlayer().getTotalPoints());
    }

    @Test
    void suspensionTest() {
        controller.suspendClient(01);
        assertFalse(p1.isActive());
        assertTrue(p2.isActive());
        assertTrue(p3.isActive());

        controller.suspendClient(02);
        assertFalse(p2.isActive());
        assertTrue(p3.isActive());

        controller.suspendClient(03);
        assertFalse(p3.isActive());

        controller.restoreClient(01);
        assertTrue(p1.isActive());
        assertFalse(p2.isActive());
        assertFalse(p3.isActive());

        controller.restoreClient(02);
        assertTrue(p1.isActive());
        assertTrue(p2.isActive());
        assertFalse(p3.isActive());

        controller.restoreClient(03);
        assertTrue(p1.isActive());
        assertTrue(p2.isActive());
        assertTrue(p3.isActive());
    }

    @Test
    void pickTilesActionTest(){
        phase = Phase.TILESPICKING;

        // Coordinates of tiles that can be picked.
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(0, 3));
        coordinates.add(new Coordinate(1,3));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        // Invalid row and col.
        coordinates.clear();
        coordinates.add(new Coordinate(-1, -1));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        // Coordinates of a tile that can't be picked.
        coordinates.clear();
        coordinates.add(new Coordinate(4,4));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        // Coordinates of not adjacent tiles.
        coordinates.clear();
        coordinates.add(new Coordinate(1,3));
        coordinates.add(new Coordinate(8,5));

        // Invalid phase.
        phase = phase.LOBBY;
        coordinates.clear();
        coordinates.add(new Coordinate(1,3));

        // Aggiungere il caso in cui la bookshelf del giocatore non è piena.

        // Dividere tutto in sottometodi.
    }

    @Test
    void placeTilesAction(){
        phase = phase.TILESPLACING;
        List<Tile> tiles = new ArrayList<>();
        int col = 0;
        final int invalidCol = 10;

        // Valid number of column and list of tiles.
        tiles.add(Tile.BLUE);
        tiles.add(Tile.PINK);
        tiles.add(Tile.PINK);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles, col));

        // Invalid number of column.
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles, invalidCol));
        assertDoesNotThrow(()->pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles, 0)));

        // Filling the column to have no space for other tiles.
        game.getCurrentPlayer().getBookShelf().placeTiles(col, tiles);
        game.getCurrentPlayer().getBookShelf().placeTiles(col, tiles);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles, col));
        assertDoesNotThrow(()->pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles, 0)));

        // Incorrect phase.
        phase = phase.TILESPLACING;
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles, col));
    }

    @Test
    void printCardsTest(){
        phase = phase.TILESPLACING;
        pcsController.firePropertyChange("PrintCardsAction", null, new PrintCardsAction());

        phase = phase.TILESPICKING;
        pcsController.firePropertyChange("PrintCardsAction", null, new PrintCardsAction());

        phase = phase.LOBBY;
        pcsController.firePropertyChange("PrintCardsAction", null, new PrintCardsAction());
    }
}