package it.polimi.ingsw.model;

import java.security.InvalidParameterException;
import java.util.List;


/**
 * This class represents the player's bookshelf.
 * @author Francesca Rosa Diz
 */
public class BookShelf {

    // This attribute specifies the grid's dimensions.
    private Cell[][] grid;

    // Maximum numbers of rows
    public static final int MAXBOOKSHELFROW = 6;
    // Maximum number of columns.
    public static final int MAXBOOKSHELFCOL = 5;
    // Maximum number of picked tiles.
    public static final int MAXPICKEDTILES = 3;


    /**
     * Constructor for the BookShelf.
     * The cycle sets initially BLANK all the cells contained in the BookShelf.
     */
    public BookShelf() {
        grid = new Cell[MAXBOOKSHELFROW][MAXBOOKSHELFCOL];
        for(int i=0; i<MAXBOOKSHELFROW; i++){
            grid[i] = new Cell[]{new Cell()};
        }

        for(int i=0; i<MAXBOOKSHELFROW; i++)
            for (int j=0; j<MAXBOOKSHELFCOL; j++)
                grid[i][j].setTile(Tile.BLANK);
    }

    /**
     * This method controls whether the player can insert the tiles he picked in the column he selects.
     * @param n number of the selected column
     * @param nTiles number of the tiles to insert
     * @return true if the tiles can be inserted.
     * @throws InvalidParameterException if the number of the column of the tiles are invalid.
     */
    public Boolean checkColumn(int n, int nTiles) throws InvalidParameterException {

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

        if (available >= nTiles) return true;

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
    public void placeTiles(int y, List<Tile> list) throws InvalidParameterException{

        /*
        If the method checkColumn throws an exception because the parameters are invalid,
        this method will return an exception.
         */
        try{
            if (!checkColumn(y, list.size()))
                return;
        } catch (Exception e) {
            throw new InvalidParameterException("Invalid column or number of picked tiles.");
        }


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
