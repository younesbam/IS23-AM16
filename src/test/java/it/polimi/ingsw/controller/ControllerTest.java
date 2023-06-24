package it.polimi.ingsw.controller;

import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import it.polimi.ingsw.server.GameHandler;
import it.polimi.ingsw.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.Const.*;
import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    ControllerHint controller;
    Game game;
    Player p1, p2, p3;
    ArrayList<Player> players = new ArrayList<>();

    private static class ServerHint extends Server{
        public ServerHint(){
            System.out.println("Patate");
        }
    }

    private static class ControllerHint extends Controller{
        public ControllerHint(GameHandler gameHandler, Game model) {super(gameHandler, model);}
    }

    private static class GameHandlerHint extends GameHandler{
        /**
         * GameHandler constructor.
         *
         * @param server
         */
        public GameHandlerHint(ServerHint server) {
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

    @BeforeEach
    void testSetup(){
        game = new Game(); // Game's creation.
        controller = new ControllerHint(new GameHandlerHint(null), game); // Controller's creation.

        // Players' creation.
        p1 = new Player("Pippo", 01);
        p2 = new Player("Pluto", 02);
        p3 = new Player("Paperino", 03);
        game.createPlayer(p1);
        game.createPlayer(p2);
        game.createPlayer(p3);
        players = game.getPlayers();
        game.setNumOfPlayers(players.size());
        p1.setChair(true);
        p2.setChair(false);
        p3.setChair(false);
        game.setFirstPlayer(p1);
        controller.setCurrentPlayer(p1);

        // Every player is active.
        for(int i=0; i< players.size(); i++)
            game.getPlayers().get(i).setActive(true);

        // Common goal cards' creation.
        game.getCommonGoalCards().add(game.getBag().pickCommonGoalCard(players.size()));
        game.getCommonGoalCards().add(game.getBag().pickCommonGoalCard(players.size()));

        // Personal goal cards' creation.
        for(int i=0; i<players.size(); i++)
            game.getPlayers().get(i).setPersonalGoalCard(game.getBag().pickPersonalGoalCard());

        // Board's creation.
        game.createBoard();
    }

    @Test
    public void setTest(){
        // getGame() check.
        assertNotNull(controller.getGame());
        assertEquals(game, controller.getGame());

        // Tests on players.
        assertNotNull(players);
        int numOfPlayers = game.getNumOfPlayers();
        assertTrue(numOfPlayers >= MINPLAYERS);
        assertTrue(numOfPlayers <= MAXPLAYERS);
        for (Player player : players) {
            assertNotNull(player.getPersonalGoalCard());
            assertNotNull(player.getBookShelf());
        }
        assertEquals(p1, controller.getCurrentPlayer());
        assertNotEquals(p2, controller.getCurrentPlayer());
        assertNotEquals(p3, controller.getCurrentPlayer());

        // Tests on chair.
        int numChairs = 0;
        for (Player player : players) {
            if (player.hasChair()) {
                numChairs++;
            }
        }
        assertEquals(1, numChairs);
        assertNotEquals(0, numChairs);

        // Tests on common goal cards.
        List<CommonGoalCard> commonGoalCards = game.getCommonGoalCards();
        assertNotNull(commonGoalCards);
        assertEquals(2, commonGoalCards.size());

        // getBoard() check.
        assertNotNull(game.getBoard());

        // Tests on points.
        controller.updateTotalPoints();
        assertEquals(0, controller.getCurrentPlayer().getTotalPoints());

        // Tests on current player.
        assertEquals(game.getCurrentPlayer(), controller.getCurrentPlayer());
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
    public void testPickTiles() {
        controller.getGame().getBoard().updateBoard();
        // Select a random tile
        Random random = new Random();
        int x = random.nextInt(MAXBOARDDIM);
        int y = random.nextInt(MAXBOARDDIM);
        while(controller.getGame().getBoard().isPickable(x,y)){
            x = random.nextInt(MAXBOARDDIM);
            y = random.nextInt(MAXBOARDDIM);
        }
        int[][] coordinates = {{x, y}};
//        controller.pickTilesAction(coordinates);

        // Check if the tile has been removed from the board
        assertFalse(controller.getGame().getBoard().isPickable(x,y));
    }

    @Test
    void testPlaceTiles() {
        // Add 4 tiles to the current player's bookshelf
        List<Tile> tiles = new ArrayList<>();
        tiles.add(Tile.BLUE);
        tiles.add(Tile.PINK);
        tiles.add(Tile.WHITE);
        tiles.add(Tile.GREEN);

        // Check if the tiles have been added to the correct column of the bookshelf
        for(int i = MAXBOOKSHELFROW; i<MAXBOOKSHELFROW-tiles.size(); i--)
            assertEquals(tiles.get(i), controller.getGame().getCurrentPlayer().getBookShelf().getGrid()[i][0]);
    }

    @Test
    void testCheckCommonGoal() {
        Player player = controller.getGame().getCurrentPlayer();
        int prevPoints = player.getTotalPoints();
        /* TO DO complete a common goal and check if points update */
    }

    @Test
    void testCheckPersonalGoal() {
        Player player = controller.getGame().getCurrentPlayer();
        int prevPoints = player.getTotalPoints();
        /* TO DO complete personal goal */
        // assertEquals(player.getPersonalGoalCard().checkScheme(player), player.getPoints()- prevPoints);
    }

    @Test
    void testCheckEndGame() {
        // Fill the current player's bookshelf to end the game
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < MAXBOOKSHELFROW; i++) {
            tiles.add(Tile.GREEN);
        }
        for (int i = 0; i < MAXBOOKSHELFCOL; i++) {
            controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(i,tiles);
        }

        // Check if the game has ended
    }

    @Test
    void testUpdatePoints() {
//        // store previous points
//        int prevPoints = controller.getGame().getCurrentPlayer().getTotalPoints();
//        // Add random points to the current player's score
//        Random random = new Random();
//        int pointsAdded = random.nextInt(0);
//        controller.updateTotalPoints();//controller.updateTotalPoints(pointsAdded);
//
//        // Check if the current player's score has been updated correctly
//        assertEquals(pointsAdded, controller.getGame().getCurrentPlayer().getTotalPoints()- prevPoints);
    }

    @Test
    void testSetCurrentPlayer() {
        Player firstPlayer = controller.getGame().getPlayers().get(0);
        controller.setCurrentPlayer(firstPlayer);
        assertEquals(firstPlayer, controller.getGame().getCurrentPlayer());
    }

    @Test
    void
    testNextPlayer() {
        List<Player> players = controller.getGame().getPlayers();
        int numOfPlayers = players.size();
        // Last player is the current player
        controller.getGame().setCurrentPlayer(players.get(numOfPlayers-1));
        // Now NextPlayer() should follow the order of the list of players -> the first next is the first player and so on
        for(int i = 0 ; i < numOfPlayers-1; i++){
            players.get(i).setActive(false);
            assertEquals(players.get(i+1), controller.getGame().getCurrentPlayer());
        }
    }
}