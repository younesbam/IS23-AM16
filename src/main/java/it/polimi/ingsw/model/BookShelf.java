package it.polimi.ingsw.model;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;

import java.security.InvalidParameterException;
import java.util.List;

import static it.polimi.ingsw.Utils.*;


/**
 * This class represents the player's bookshelf.
 * @author Francesca Rosa Diz
 */
public class BookShelf {

    /**
     * This attribute specifies the grid's dimensions.
     */
    private Cell[][] grid;

    /**
     * Constructor for the BookShelf.
     * It sets initially BLANK all the cells contained in the BookShelf.
     */
    public BookShelf() {
        grid = Utils.createBlankGrid(MAXBOOKSHELFROW, MAXBOOKSHELFCOL);
    }

    /**
     * This method controls whether the player can insert the tiles he picked in the column he selects.
     * @param n number of the selected column
     * @param nTiles number of the tiles to insert
     * @return true if the tiles can be inserted.
     * @throws InvalidParameterException if the number of the column of the tiles are invalid.
     */
    public void checkColumn(int n, int nTiles) throws InvalidParameterException, NotEmptyColumnException {

        /*
         * This attribute counts the number of available cells in the column.
         */
        int available = 0;

        /*
         * Check of the validity of the column's number.
         */
        if (n>MAXBOOKSHELFCOL || n<0 || nTiles<0 || nTiles>MAXPICKEDTILES) throw new InvalidParameterException();

        for (int i=0; i<MAXBOOKSHELFCOL; i++) {
            if(grid[n][i].getTile() == Tile.BLANK)
                available++;
        }

        if (available < nTiles) {
            throw new NotEmptyColumnException();
        }
    }


    /**
     * This method adds the tile(s) the player selects in the desired column if the column has enough free
     * spaces (checked by checkColumn()).
     * @param y number of the desired column.
     * @param list list of tile(s) selected by the player.
     */
    public void placeTiles(int y, List<Tile> list){
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
        for (int i=0; i<MAXBOOKSHELFCOL; i++) {
            for (int j=0; j<MAXBOOKSHELFROW; j++) {
                if (grid[i][j].getTile() == Tile.BLANK) {
                    return false;
                }
            }
        }
        return true;
    }

}
