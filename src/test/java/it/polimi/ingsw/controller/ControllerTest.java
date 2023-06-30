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

/**
 * ControllerTest class tests Controller class.
 *
 * @see Controller
 */
class ControllerTest {

    Controller controller;
    GameHint game;
    Player p1, p2, p3;
    ArrayList<Player> players = new ArrayList<>();
    List<CommonGoalCard> commCards = new ArrayList<>();
    PropertyChangeSupport pcsController = new PropertyChangeSupport(this);
    List<Coordinate> coordinates = new ArrayList<>();
    List<Tile> tiles3pink = new ArrayList<>();
    List<Tile> tiles3white = new ArrayList<>();
    List<Tile> tiles2 = new ArrayList<>();
    List<Tile> tiles1 = new ArrayList<>();
    int col = 0;
    ArrayList<Tile> pickedTiles = new ArrayList<>();


    /**
     * This class represents a hint of the GameHandler.
     * It's used to run tests without using communication tools.
     * @see GameHandler
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

        /**
         * @see GameHandler#sendToEveryone(Answer)
         */
        @Override
        public void sendToEveryone(Answer answer){
            // Nothing.
        }

        /**
         * @see GameHandler#sendToEveryoneExcept(Answer, int)
         */
        @Override
        public void sendToEveryoneExcept(Answer answer, int notToHim){
            // Nothing.
        }

        /**
         * @see GameHandler#sendToPlayer(Answer, int)
         */
        @Override
        public void sendToPlayer(Answer answer, int playerID){
            // Nothing.
        }
    }

    /**
     * This class represents a hint of the Game.
     * It's used to instantiate a board for 3 players to run the tests.
     * @see Game
     */
    private static class GameHint extends Game {
        private CreationFactory creationFactory;
        private Board board;

        /**
         * GameHint constructor.
         */
        public GameHint(){super();};

        /**
         * @see Game#createBoard()
         */
        @Override
        public void createBoard(){
            this.creationFactory = new CreationFactory();
            this.board = creationFactory.createBoard(3);
        }

        /**
         * @see Game#getBoard()
         * @return
         */
        @Override
        public Board getBoard(){return this.board;}
    }

    /**
     * This method instantiates the controller to run tests.
     */
    @BeforeEach
    void init(){
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

        /*
        The method setup() in Controller class creates common goal cards, selects personal goal cards for players,
        creates the board and sets the phase to TilesPicking.
         */
        controller.setup();

        // Common goal cards' creation.
        commCards = game.getCommonGoalCards();

        // Property change listener declaration.
        pcsController.addPropertyChangeListener(controller);

        // Creation of a list of 3 pink tiles.
        tiles3pink.add(Tile.PINK);
        tiles3pink.add(Tile.PINK);
        tiles3pink.add(Tile.PINK);

        // Creation of a list of 3 white tiles.
        tiles3white.add(Tile.WHITE);
        tiles3white.add(Tile.WHITE);
        tiles3white.add(Tile.WHITE);

        // Creation of a list of 2 tiles.
        tiles2.add(Tile.GREEN);
        tiles2.add(Tile.GREEN);

        // Creation of a list of 1 tile.
        tiles1.add(Tile.YELLOW);
    }

    /**
     * This method tests the method getGame() in Controller class.
     * @see Controller#getGame()
     */
    @Test
    public void getGameTest(){
        assertNotNull(controller.getGame());
        assertEquals(game, controller.getGame());
    }

    /**
     * This method tests the methods getPhase() and setPhase() in Controller class.
     * @see Controller#getPhase()
     * @see Controller#setPhase(Phase)
     */
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

    /**
     * This method tests method setCurrentPlayer() in Controller class.
     * @see Controller#setCurrentPlayer(Player)
     */
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

    /**
     * This method tests that at the beginning of the game every player has 0 points.
     * @see Controller#updateTotalPoints()
     */
    @Test
    public void pointsTest(){
        controller.updateTotalPoints();
        assertEquals(0, game.getCurrentPlayer().getTotalPoints());

        controller.setCurrentPlayer(p2);
        controller.updateTotalPoints();
        assertEquals(0, game.getCurrentPlayer().getTotalPoints());

        controller.setCurrentPlayer(p3);
        controller.updateTotalPoints();
        assertEquals(0, game.getCurrentPlayer().getTotalPoints());
    }

    /**
     * This method tests methods suspendClient() and restoreClient() in Controller class
     * when the phase is different from Standby.
     * @see Controller#restoreClient(int)
     * @see Controller#suspendClient(int)
     */
    @Test
    void suspensionTest() {
        controller.setPhase(Phase.SETUP);

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

        // Test suspending a different player from current player.
        controller.setCurrentPlayer(p2);
        controller.suspendClient(01);
        assertTrue(p2.isActive());
        assertFalse(p1.isActive());

        // Suspending a client who is already inactive.
        game.getPlayerByID(03).setActive(false);
        controller.suspendClient(03);
        assertFalse(p3.isActive());
        assertTrue(p2.isActive());
    }

    /**
     * This method tests methods suspendClient() restoreClient() in Controller class
     * when the phase is Standby.
     * @see Controller#suspendClient(int)
     * @see Controller#restoreClient(int)
     */
    @Test
    void suspensionTestInStandby(){
        controller.suspendClient(01);
        assertFalse(p1.isActive());
        assertTrue(p2.isActive());
        assertTrue(p3.isActive());

        controller.setPhase(Phase.STANDBY);
        controller.restoreClient(01);
    }

    /**
     * This method tests method pickTilesAction() in Controller class when the phase is different from TilesPicking.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionWrongPhaseTest(){
        controller.setPhase(Phase.LOBBY);
        coordinates.add(new Coordinate(1,3));
        coordinates.add(new Coordinate(0,3));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests method pickTilesAction() in Controller class when the player selects tiles aligned in column.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionTestInCol(){
        controller.setPhase(Phase.TILESPICKING);
        coordinates.add(new Coordinate(1,3));
        coordinates.add(new Coordinate (0,3));
        assertDoesNotThrow(()->pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates)));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests method pickTilesAction() in Controller class when the player selects tiles aligned in row.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionTestInRow(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(2,5));
        coordinates.add(new Coordinate(2,6));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests method PickTilesAction() in Controller class when the selected tiles are on the same row but
     * not close together.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionTestRowNotClose(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(2,6));
        coordinates.add(new Coordinate(2,2));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests method PickTilesAction() in Controller class when the selected tiles are on the same column
     * but not close together.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionTestColNotClose(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(2,6));
        coordinates.add(new Coordinate(6,6));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests method PickTilesAction() in Controller class when the row is an invalid parameter.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionTestInvalidRow(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(-1, 0));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests method PickTilesAction() in Controller class when the column is an invalid parameter.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionInvalidCol(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(0, -1));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests method PickTilesAction() in Controller class when the player selects tiles that can't be picked.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionTestUnpickableCells(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(4,4));
        coordinates.add(new Coordinate(4,5));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests method PickTilesAction() in Controller class when the player selects not adjacent cells.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionTestNotAdjacentCells(){
        controller.setPhase(Phase.TILESPICKING);

        coordinates.add(new Coordinate(5,0));
        coordinates.add(new Coordinate(8,5));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests method PickTilesAction() in Controller class when the player's bookshelf doesn't have enough free space
     * in any column for the selected tiles.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void pickTilesActionTestNotEmptyBookshelf(){
        controller.setPhase(Phase.TILESPICKING);

        // Filling the player's bookshelf.
        for(int i=0; i<MAXBOOKSHELFCOL; i++){
            game.getCurrentPlayer().getBookShelf().placeTiles(i, tiles2);
            game.getCurrentPlayer().getBookShelf().placeTiles(i, tiles3white);
        }

        coordinates.add(new Coordinate(7,5));
        coordinates.add(new Coordinate(8,5));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));
    }

    /**
     * This method tests methods PickTilesAction() and PlaceTilesAction() in Controller class when coordinates
     * are right and then the player places the picked tiles in a valid column.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
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

    /**
     * This method tests method PlaceTilesAction() in Controller class when the phase is different from TilesPlacing.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void placeTilesActionTestWrongPhase(){
        controller.setPhase(Phase.TILESPICKING);

        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles2, col));
    }

    /**
     * This method tests method PlaceTilesAction() in Controller class when the player inserts an invalid parameter for
     * bookshelf's column.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void placeTilesActionTestWrongCol(){
        controller.setPhase(Phase.TILESPICKING);
        pickedTiles.add(game.getBoard().getTile(0,5));
        coordinates.add(new Coordinate(5,0));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, -1));
    }

    /**
     * This method tests method PlaceTilesAction() in Controller class when the tiles passed as parameter to the method
     * are different from the ones picked.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void placeTilesActionDifferentTiles(){
        controller.setPhase(Phase.TILESPLACING);

        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(tiles1, col));
    }

    /**
     * This method tests method PlaceTilesAction() in Controller class when the player chooses a column that doesn't have
     * enough free space for the tiles picked.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void placeTilesActionTestNotEmptyCol(){
        game.getCurrentPlayer().getBookShelf().placeTiles(col, tiles3pink);
        game.getCurrentPlayer().getBookShelf().placeTiles(col, tiles3white);

        controller.setPhase(Phase.TILESPICKING);
        coordinates.add(new Coordinate(4,7));
        pickedTiles.add(game.getBoard().getTile(4,7));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, col));
    }

    /**
     * This method tests method PlaceTilesAction() in Controller class when the player fills the last free space
     * in his bookshelf and then the game ends.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void placeTilesActionTestEndGame(){
        // Filling all bookshelf's columns in order to leave 1 space free for the last tile.
        for(int i=1; i<MAXBOOKSHELFCOL; i++){
            controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(i, tiles3white);
            controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(i, tiles3pink);
        }
        controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(col, tiles3white);
        controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(col, tiles2);

        controller.setPhase(Phase.TILESPICKING);
        coordinates.add(new Coordinate(7,4));
        pickedTiles.add(game.getBoard().getTile(7,4));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, col));
        controller.updateTotalPoints();
    }

    /**
     * This method tests method PlaceTilesAction() in Controller class when the player disconnects during his turn
     * so the tiles he picked must be placed back in the board.
     * @see Controller#propertyChange(PropertyChangeEvent)
     * @see Controller#suspendClient(int)
     */
    @Test
    void suspensionTestDuringTurn(){
        controller.setPhase(Phase.TILESPICKING);
        coordinates.add(new Coordinate(7,5));
        coordinates.add(new Coordinate(8,5));
        pickedTiles.add(controller.getGame().getBoard().getTile(7,5));
        pickedTiles.add(controller.getGame().getBoard().getTile(8,5));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        controller.setPhase(Phase.TILESPLACING);
        controller.suspendClient(controller.getGame().getCurrentPlayer().getID());
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, col));
    }

    /**
     * This method tests methods PickTilesAction() and PlaceTilesAction() when the current player is the only active
     * playe and he fills all his bookshelf.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void placeTilesActionTestOnePlayerActiveAndEndGame(){
        controller.setCurrentPlayer(p2);
        controller.setPhase(Phase.TILESPICKING);
        coordinates.add(new Coordinate(2,2));

        for(int i=1; i<MAXBOOKSHELFCOL; i++){
            p2.getBookShelf().placeTiles(i, tiles3white);
            p2.getBookShelf().placeTiles(i, tiles3pink);
        }
        p2.getBookShelf().placeTiles(col, tiles3pink);
        p2.getBookShelf().placeTiles(col, tiles2);

        pickedTiles.add(game.getBoard().getTile(2,2));

        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        controller.setPhase(Phase.TILESPLACING);
        controller.suspendClient(01);
        controller.suspendClient(03);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, col));
    }

    /**
     * This method tests methods PickTilesAction() and PlaceTilesAction() in Controller class when the current player
     * is the only active player but he doesn't fill his bookshelf.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void placeTilesActionTestOnePlayerActive(){
        controller.setCurrentPlayer(p1);
        controller.setPhase(Phase.TILESPLACING);
        controller.suspendClient(02);
        controller.suspendClient(03);

        coordinates.add(new Coordinate(8,5));
        pickedTiles.add(game.getBoard().getTile(8,5));
        pcsController.firePropertyChange("PickTilesAction", null, new PickTilesAction(coordinates));

        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PlaceTilesAction", null, new PlaceTilesAction(pickedTiles, col));
    }

    /**
     * This method tests method PrintCardsAction() in Controller class when the phase is TilesPlacing.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void printCardsActionTestTilesPlacing(){
        controller.setPhase(Phase.TILESPLACING);
        pcsController.firePropertyChange("PrintCardsAction", null, new PrintCardsAction());
    }

    /**
     * This method tests method PrintCardsAction() in Controller class when the phase is TilesPicking.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void printCardsActionTestTilesPicking(){
        controller.setPhase(Phase.TILESPICKING);
        pcsController.firePropertyChange("PrintCardsAction", null, new PrintCardsAction());
    }

    /**
     * This method tests method PrintCardsAction() in Controller class when the phase is different from
     * TilesPlacing and TilesPicking.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    @Test
    void printCardsActionTestNotAllowed(){
        controller.setPhase(Phase.LOBBY);
        pcsController.firePropertyChange("PrintCardsAction", null, new PrintCardsAction());
    }
}