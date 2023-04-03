package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Tile;

/**
 * This is the board for 3 players.
 * @author Francesca Rosa Diz
 */
public class Board3Players extends Board implements IBoard {

    public Board3Players() {
        super();

        /**
         * This sets the cells for 4 players unavailable.
         */
        grid[0][4].setTile(Tile.UNAVAILABLE);
        grid[1][5].setTile(Tile.UNAVAILABLE);
        grid[3][1].setTile(Tile.UNAVAILABLE);
        grid[4][0].setTile(Tile.UNAVAILABLE);
        grid[4][8].setTile(Tile.UNAVAILABLE);
        grid[5][7].setTile(Tile.UNAVAILABLE);
        grid[7][3].setTile(Tile.UNAVAILABLE);
        grid[8][4].setTile(Tile.UNAVAILABLE);
    }

}
