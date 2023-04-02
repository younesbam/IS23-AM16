package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board;

/**
 * This is the board for 2 players.
 * @author Francesca Rosa Diz.
 */
public class Board2Players extends Board implements IBoard{

    /**
     * Constructor for the board.
     */
    public Board2Players() {
        super();

        // This sets the cells for 3 and 4 players unavailable.
        grid[0][3].setTile(Tile.UNAVAILABLE);
        grid[0][4].setTile(Tile.UNAVAILABLE);
        grid[1][5].setTile(Tile.UNAVAILABLE);
        grid[2][2].setTile(Tile.UNAVAILABLE);
        grid[2][6].setTile(Tile.UNAVAILABLE);
        grid[3][1].setTile(Tile.UNAVAILABLE);
        grid[3][8].setTile(Tile.UNAVAILABLE);
        grid[4][0].setTile(Tile.UNAVAILABLE);
        grid[4][8].setTile(Tile.UNAVAILABLE);
        grid[5][0].setTile(Tile.UNAVAILABLE);
        grid[5][7].setTile(Tile.UNAVAILABLE);
        grid[6][2].setTile(Tile.UNAVAILABLE);
        grid[6][6].setTile(Tile.UNAVAILABLE);
        grid[7][3].setTile(Tile.UNAVAILABLE);
        grid[8][4].setTile(Tile.UNAVAILABLE);
        grid[8][5].setTile(Tile.UNAVAILABLE);
    }

}
