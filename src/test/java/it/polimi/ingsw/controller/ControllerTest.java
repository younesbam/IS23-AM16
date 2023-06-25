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
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Board3Players;
import it.polimi.ingsw.model.board.CreationFactory;
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
import java.security.InvalidParameterException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.Const.*;
import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    Controller controller;
    GameHint game;
    Player p1, p2, p3;
    ArrayList<Player> players = new ArrayList<>();
    List<CommonGoalCard> commCards = new ArrayList<>();
    LinkedList<PersonalGoalCard> persCards = new LinkedList<>();
    int numOfPlayers = 3;
    PropertyChangeSupport pcsController = new PropertyChangeSupport(this);
    Phase phase = null;
    List<Coordinate> coordinates = new ArrayList<>();
    List<Tile> tiles3 = new ArrayList<>();
    List<Tile> tiles2 = new ArrayList<>();
    List<Tile> tiles1 = new ArrayList<>();
    int col = 0;
    ArrayList<Tile> pickedTiles = new ArrayList<>();


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
        private CreationFactory creationFactory;
        private Board board;

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

        @Override
        public void createBoard(){
            this.creationFactory = new CreationFactory();
            this.board = creationFactory.createBoard(3);
        }

        @Override
        public Board getBoard(){return this.board;}
    }

    @BeforeEach
    void testSetup(){
        game = new GameHint(); // Game's creation.
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

        // Creation of a list of 3 tiles.
        tiles3.add(Tile.PINK);
        tiles3.add(Tile.PINK);
        tiles3.add(Tile.PINK);

        // Creation of a list of 2 tiles.
        tiles2.add(Tile.GREEN);
        tiles2.add(Tile.GREEN);

        // Creation of a list of 1 tile.
        tiles1.add(Tile.YELLOW);

        game.getBoard().updateBoard();

        // Assigning all players a personal goal card.
        jsonParser = new JSONParser("initSetup.json");
        persCards = jsonParser.getPersonalGoalCards();
        for (Player player : players)
            player.setPersonalGoalCard(persCards.get(0));
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
        assertEquals(p1, game.getCurrentPlayer());
        assertNotEquals(p2, game.getCurrentPlayer());
        assertNotEquals(p3, game.getCurrentPlayer());

        controller.setCurrentPlayer(p2);
        assertEquals(p2, game.getCurrentPlayer());

        controller.setCurrentPlayer(p3);
        assertEquals(p3, game.getCurrentPlayer());
    }

    @Test
    public void pointsTest(){
        controller.updateTotalPoints();
        assertEquals(0, game.getCurrentPlayer().getTotalPoints());
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

    // Test with the wrong phase.
    @Test
    void pickTilesActionWrongPhaseTest(){
        controller.setPhase(Phase.LOBBY);
        coordinates.add(new Coordinate(1,3));
        coordinates.add(new Coordinate(0,3));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    // Test in which cells are aligned in column.
    @Test
    void pickTilesActionTestInCol(){
        controller.setPhase(Phase.TILESPICKING);
        coordinates.add(new Coordinate(1,3));
        coordinates.add(new Coordinate (0,3));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    // Test in which cells are aligned in row.
    @Test
    void pickTilesActionTestInRow(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(2,5));
        coordinates.add(new Coordinate(2,6));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    // Test with an invalid parameter for row.
    @Test
    void pickTilesActionTestInvalidRow(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(-1, 0));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    //Test with an invalid parameter for column.
    @Test
    void pickTilesActionInvalidCol(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(0, -1));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    // Test with coordinates that can't be picked.
    @Test
    void pickTilesActionTestUnpickableCells(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(4,4));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    // Test with coordinates of not adjacent tiles.
    @Test
    void pickTilesActionTestNotAdjacentCells(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(5,0));
        coordinates.add(new Coordinate(8,5));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    // Test with a not empty bookshelf.
    @Test
    void pickTilesActionTestNotEmptyBookshelf(){
        controller.setPhase(Phase.TILESPICKING);

        // Filling the player's bookshelf.
        for(int i=0; i<MAXBOOKSHELFCOL; i++){
            game.getCurrentPlayer().getBookShelf().placeTiles(i, tiles2);
            game.getCurrentPlayer().getBookShelf().placeTiles(i, tiles3);
        }

        coordinates.add(new Coordinate(7,5));
        coordinates.add(new Coordinate(8,5));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    // Test in which all should be done correctly.
    @Test
    void pickAndPlaceTilesActionTest(){
        controller.setPhase(Phase.TILESPICKING);
        coordinates.add(new Coordinate(3,7));
        coordinates.add(new Coordinate(3,8));
        pickedTiles.add(game.getBoard().getTile(3,7));
        pickedTiles.add(game.getBoard().getTile(3,8));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));


        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, col));
    }

    // Test in which the phase is wrong.
    @Test
    void placeTilesActionTestWrongPhase(){
        controller.setPhase(Phase.TILESPICKING);

        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles2, col));
    }

    // Test with an invalid number for the column.
    @Test
    void placeTilesActionTestWrongCol(){
        controller.setPhase(Phase.TILESPICKING);
        pickedTiles.add(game.getBoard().getTile(0,5));
        coordinates.add(new Coordinate(5,0));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, -1));
    }

    // Test passing the method different tiles from the picked ones.
    @Test
    void placeTilesActionDifferentTiles(){
        controller.setPhase(Phase.TILESPLACING);

        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles1, col));
    }

    // Test trying to fill a not empty column.
    @Test
    void placeTilesActionTestNotEmptyCol(){
        game.getCurrentPlayer().getBookShelf().placeTiles(col, tiles3);
        game.getCurrentPlayer().getBookShelf().placeTiles(col, tiles3);

        controller.setPhase(Phase.TILESPICKING);
        coordinates.add(new Coordinate(4,7));
        pickedTiles.add(game.getBoard().getTile(4,7));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, col));
    }

    // Test with an action that fills the board in order to end the game.
    @Test
    void placeTilesActionTestEndGame(){
        // Filling all bookshelf's columns in order to leave 1 space free for the last tile.
        for(int i=1; i<MAXBOOKSHELFCOL; i++){
            controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(i, tiles3);
            controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(i, tiles3);
        }
        controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(col, tiles3);
        controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(col, tiles2);

        controller.setPhase(Phase.TILESPICKING);
        coordinates.add(new Coordinate(7,4));
        pickedTiles.add(game.getBoard().getTile(7,4));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, col));
    }

    // Test of the printing action for tilesplacing.
    @Test
    void printCardsActionTestTilesPlacing(){
        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PrintCardsAction", null, new PrintCardsAction());
    }

    // Test of the printing action for tilespicking.
    @Test
    void printCardsActionTestTilesPicking(){
        controller.setPhase(Phase.TILESPICKING);
        pcsController.firePropertyChange("PrintCardsAction", null, new PrintCardsAction());
    }

    // Test of the printing action with a not allowed phase.
    @Test
    void printCardsActionTestNotAllowed(){
        controller.setPhase(Phase.LOBBY);
        pcsController.firePropertyChange("PrintCardsAction", null, new PrintCardsAction());
    }
}