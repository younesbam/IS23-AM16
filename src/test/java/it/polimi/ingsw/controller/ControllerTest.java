package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static it.polimi.ingsw.Utils.MAXBOARDDIM;

class ControllerTest {

    private Controller controller;

    @BeforeEach
    public void setUp() {
        controller = new Controller();
    }

    @Test
    public void testGetGame() {
        assertNotNull(controller.getGame());
    }

    @Test
    public void testPickTiles() {
        // Select a random tile
        Random random = new Random();
        int x = random.nextInt(MAXBOARDDIM);
        int y = random.nextInt(MAXBOARDDIM);
        while(controller.getGame().getBoard().isPickable(x,y)){
            x = random.nextInt(MAXBOARDDIM);
            y = random.nextInt(MAXBOARDDIM);
        }
        int[][] coordinates = {{0, 0}};
        controller.pickTiles(1, coordinates);

        // Check if the tile has been removed from the board
        assertFalse(controller.getGame().getBoard().isPickable(x,y));
    }

    @Test
    void placeTiles() {
        // Add 4 tiles to the current player's bookshelf
        List<Tile> tiles = new ArrayList<>();
        tiles.add(Tile.BLUE);
        tiles.add(Tile.PINK);
        tiles.add(Tile.WHITE);
        tiles.add(Tile.GREEN);
        controller.placeTiles(0, tiles.size(), tiles);

        // Check if the tiles have been added to the correct column of the bookshelf
        for(int i = 0; i<tiles.size(); i++)
            assertEquals(tiles.get(i), controller.getGame().getCurrentPlayer().getBookShelf().getGrid()[i][0]);
    }

    @Test
    void checkCommonGoal() {
    }

    @Test
    void checkPersonalGoal() {
    }

    @Test
    void checkEndGame() {
    }

    @Test
    void updatePoints() {
    }

    @Test
    void setCurrentPlayer() {
    }

    @Test
    void nextPlayer() {
    }
}