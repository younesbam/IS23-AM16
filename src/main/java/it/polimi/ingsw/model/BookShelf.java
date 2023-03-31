package it.polimi.ingsw.model;

import java.util.List;

/**
 * This class represents the player's bookshelf.
 */
public class BookShelf {

    /**
     * This attribute specifies the grid's dimensions.
     */
    private Cell[][] grid = new Cell[6][5];

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
    public Boolean checkColumn(int n, int nTiles){

        /**
         * This attribute counts the number of available cells in the column.
         */
        int available = 0;

        /**
         * Check of the validity of the column's number.
         */
        if (n>MAXCOL || n<0)
            return false;



        for (int i=0; i<MAXROW; i++) {
            if(grid[n][i].getTile() == Tile.BLANK)
                available++;
        }
        if (available >= nTiles)
            return true;
        else
            System.out.println("Invalid column, try again");

        return false;
    }


    /**
     * This method adds the tile(s) the player selects in the desired column if the column has enough free
     * spaces (checked by checkColumn()).
     * @param y number of the desired column.
     * @param list list of tile(s) selected by the player.
     */
    public void placeTiles(int y, List<Tile> list) {

        /**
         * This statement controls whether the number of the picked tiles is valid.
         * (Not sure if this control has to be done here, maybe in another place).
         */
        if (list.size() > 3 || list.size() == 0) {
            System.out.println("Error: invalid number of tiles.");
            return;
        }

        if (checkColumn(y, list.size()) == false)
            return;


        int i=0;
        while (grid[y][i].getTile() != Tile.BLANK)
            i++;

        for (Tile tile : list) {
            grid[y][i].setTile(tile);
            i++;
        }

    }



    /**
     * This method returns the bookshelf.
     * @return the current status of the bookshelf.
     */
    public Cell[][] getGrid() {
        return grid;
    }

    /**
     * This method checks if the player has completed his bookshelf (so if the bookshelf is full).
     * If so, the game ends.
     * @return true if the bookshelf is full.
     */
    public boolean checkEndGame() {
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
