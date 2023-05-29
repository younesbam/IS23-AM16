package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw.Const.*;
import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    private Controller controller;

    @BeforeEach
    public void setUp() {
        controller = new Controller(null, null);
    }
    @Test
    public void testSetup(){
        Game game = controller.getGame();
        int numOfPlayers = game.getNumOfPlayers();
        assertTrue(numOfPlayers > 0);

        List<Player> players = game.getPlayers();
        assertNotNull(players);
        assertEquals(numOfPlayers, players.size());

        int numChairs = 0;
        for (Player player : players) {
            if (player.hasChair()) {
                numChairs++;
            }
        }
        assertEquals(1, numChairs);

        List<CommonGoalCard> commonGoalCards = game.getCommonGoalCards();
        assertNotNull(commonGoalCards);

        for (Player player : players) {
            assertNotNull(player.getPersonalGoalCard());
            assertNotNull(player.getBookShelf());
        }

        assertNotNull(game.getBoard());
    }
    @Test
    public void testGetGame() {
        assertNotNull(controller.getGame());
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
//        controller.placeTiles(0, tiles.size(), tiles);

        // Check if the tiles have been added to the correct column of the bookshelf
        for(int i = 0; i<tiles.size(); i++)
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
        controller.setup();
        // Fill the current player's bookshelf to end the game
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < MAXBOOKSHELFROW; i++) {
            tiles.add(Tile.GREEN);
        }
        for (int i = 0; i < MAXBOOKSHELFCOL; i++) {
            controller.getGame().getCurrentPlayer().getBookShelf().placeTiles(i,tiles);
        }

        // Check if the game has ended
        assertTrue(controller.checkEndGame());
    }

    @Test
    void testUpdatePoints() {
        // store previous points
        int prevPoints = controller.getGame().getCurrentPlayer().getTotalPoints();
        // Add random points to the current player's score
        Random random = new Random();
        int pointsAdded = random.nextInt();
        controller.updateTotalPoints();//controller.updateTotalPoints(pointsAdded);

        // Check if the current player's score has been updated correctly
        assertEquals(pointsAdded, controller.getGame().getCurrentPlayer().getTotalPoints()- prevPoints);
    }

    @Test
    void testSetCurrentPlayer() {
        Player firstPlayer = controller.getGame().getPlayers().get(0);
        controller.setCurrentPlayer(firstPlayer);
        assertEquals(firstPlayer, controller.getGame().getCurrentPlayer());
    }

    @Test
    void testNextPlayer() {
        List<Player> players = controller.getGame().getPlayers();
        int numOfPlayers = players.size();
        // Last player is the current player
        controller.getGame().setCurrentPlayer(players.get(numOfPlayers-1));
        // Now NextPlayer() should follow the order of the list of players -> the first next is the first player and so on
        for(int i = 0 ; i < numOfPlayers; i++){
            //controller.nextPlayer();
            assertEquals(players.get(i), controller.getGame().getCurrentPlayer());
        }
    }
}