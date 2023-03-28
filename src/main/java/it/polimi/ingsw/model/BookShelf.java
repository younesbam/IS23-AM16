package it.polimi.ingsw.model;

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
    Boolean checkColumn(int n, int nTiles) {

        /**
         * Check of the validity of the column's number.
         */
        if (n>MAXCOL || n<1)
            return false;



        return null;
    }

}
