package it.polimi.ingsw.model;

import java.util.List;

/**
 * This class represents the player's bookshelf.
 */
public class BookShelf {

    /**
     * This attribute specifies the grid's dimensions.
     */
    Cell[][] grid = new Cell[6][5];

    /**
     * This attribute represents the maximum number a column can assume.
     */
    public static final int MAXCOL = 5;


    /**
     * This attribute represents the maximum number a row can assume.
     */
    public static final int MAXROW = 6;


    /**
     * This method controls whether the player can insert the tiles he picked in the column he selects.
     * @param n number of the selected column
     * @param nTiles number of the tiles to insert
     * @return true if the tiles can be inserted.
     */
    Boolean checkColumn(int n, int nTiles){

        /**
         * This attribute counts the number of available cells in the column.
         */
        int available = 0;

        /**
         * Check of the validity of the column's number.
         */
        if (n>MAXCOL || n<1)
            return false;



        for (int i=0; i<MAXROW; i++) {
            if(grid[n][i].getTile() == Tile.BLANK)
                available++;
        }
        if (available >= nTiles)
            return true;
        else
            System.out.println("Invalid move, try again");

        return false;
    }

    /**
     * This method inserts 1 tile if there is availability (check by the method checkColumn).
     * @param y number of the selected column.
     * @param tile1 tile to insert.
     */
    void placeTiles(int y, Tile tile1) {
        if (checkColumn(y, 1) == false)
            return;


        int i = 0;
        while (grid[y][i].getTile() != Tile.BLANK)
            i++;

        grid[y][i].setTile(tile1);


    }

    /**
     * This method inserts 2 tiles if there is availability (checked by the method checkColumn).
     * @param y number of the selected column.
     * @param tile1 first tile to insert.
     * @param tile2 second tile to insert.
     */
    void placeTiles(int y, Tile tile1, Tile tile2) {
        if (checkColumn(y, 2) == false)
            return;

        int i = 0;
        while (grid[y][i].getTile() != Tile.BLANK)
            i++;

        grid[y][i].setTile(tile1);
        grid[y][i+1].setTile(tile2);
    }


    void placeTiles(int y, Tile tile1, Tile tile2, Tile tile3) {
        if (checkColumn(y, 3) == false)
            return;

        int i = 0;
        while (grid[y][i].getTile() != Tile.BLANK)
            i++;

        grid[y][i].setTile(tile1);
        grid[y][i+1].setTile(tile2);
        grid[y][i+2].setTile(tile3);
    }

    /**
     * This method returns the bookshelf.
     * @return the current status of the bookshelf.
     */
    Cell[][] getGrid() {
        return grid;
    }

    /**
     * This method checks if the player has completed his bookshelf (so if the bookshelf is full).
     * If so, the game ends.
     * @return true if the bookshelf is full.
     */
    boolean checkEndGame() {
        for (int i=0; i<MAXCOL; i++) {
            for (int j=0; j<MAXROW; j++) {
                if (grid[i][j].getTile() == Tile.BLANK) {
                    return false;
                }
            }
        }

        return true;
    }

}
