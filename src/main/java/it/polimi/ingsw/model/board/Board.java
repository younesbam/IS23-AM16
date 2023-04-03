package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Tile;

import java.util.*;

/**
 * This class represents the game's board.
 * @author Francesca Rosa Diz.
 */
public abstract class Board {

     /*This variable represents the board.
     The board is composed by cells.*/
    protected Cell[][] grid = new Cell[MAXBOARDDIM][MAXBOARDDIM];

    // Set of all the tiles.
    private Set<Tile> initTileSet;

    // Queue of the tiles.
    private Queue<Tile> tiles;

    // This constant represents the maximum number for any tile's type.
    public static final int MAXTILES = 22;

    // This constant represents the maximum number of columns and rows for the board.
    public static final int MAXBOARDDIM = 9;


    /**
     * Constructor of the board, used by the subclasses.
     */
    public Board(){
        initTileSet = new HashSet<>();
        for (Tile tile : Tile.values())
            for (int i=0; i<MAXTILES; i++)
                initTileSet.add(tile);
        tiles = new PriorityQueue<>(initTileSet);


        // This cycle sets blank in each cell in order to let the cell be filled.
        for (int i=0; i<MAXBOARDDIM; i++)
            for (int j = 0; j< MAXBOARDDIM; j++)
                grid[i][j].setTile(Tile.BLANK);


        // This cycle sets UNAVAILABLE the cells in the top left corner.
        for (int j=0; j<4; j++)
            for (int i=0; i<4-j; i++)
                grid[i][j].setTile(Tile.UNAVAILABLE);

        // This cycle sets UNAVAILABLE the cells in the top right corner.
        for (int j = 5; j<= MAXBOARDDIM; j++)
            for (int i=0; i<3; i++)
                grid[i][j].setTile(Tile.UNAVAILABLE);

        // This cycle sets UNAVAILABLE the cells in the bottom left corner.
        for (int i=6; i<=MAXBOARDDIM; i++)
            for (int j=0; j<3; j++){
                grid[i][j].setTile(Tile.UNAVAILABLE);
                grid[i][j+1].setTile(Tile.UNAVAILABLE);
            }

        //This cycle sets UNAVAILABLE the cells in the bottom right corner.
        for (int i=5; i<=MAXBOARDDIM; i++)
            for (int j = MAXBOARDDIM; j<5; j--)
                grid[i][j].setTile(Tile.UNAVAILABLE);
    }


    /**
     * This method refills the board when needed (if all the tiles are isolated or if the board is empty).
     */
    public void updateBoard(){

        // If there is no need to refill, the board isn't updated.

        if(!refillNeeded())
            return;

        for (int i=0; i<MAXBOARDDIM; i++)
            for (int j = 0; j< MAXBOARDDIM; j++)
                if(grid[i][j].getTile() == Tile.BLANK)
                    grid[i][j].setTile(insertTile());
    }

    /**
     * This method checks if the board must be refilled (if the tiles in the board are isolated).
     * @return true if the board must be refilled.
     */
    public boolean refillNeeded() {

        // If there is at least one cell pickable there is no need to refill.
        for (int i=0; i<MAXBOARDDIM; i++)
            for (int j = 0; j< MAXBOARDDIM; j++)
                if (isPickable(i, j))
                    return false;

        return true;
    }

    /**
     * This method checks is a tile can be picked or not (so if it has at least one free side and at least one
     * occupied side).
     * @param x x coordinate of the cell to check.
     * @param y y coordinate of the cell to check.
     * @return true if the tile can be picked.
     */
    public boolean isPickable(int x, int y) {

        // These variables represent the adjacent cells.
        int north = y-1;
        int east = x+1;
        int south = y+1;
        int west = x-1;

        // Number of free and occupied cells.
        int free = 0;
        int occupied = 0;

        if(north>=0){
            if (grid[x][north].getTile() == Tile.BLANK || grid[x][north].getTile() == Tile.UNAVAILABLE)
                free++;
            else
                occupied++;
        }
        if(south<MAXBOARDDIM){
            if (grid[x][south].getTile() == Tile.BLANK || grid[x][south].getTile() == Tile.UNAVAILABLE)
                free++;
            else
                occupied++;
        }
        if(east<MAXBOARDDIM) {
            if (grid[east][y].getTile() == Tile.BLANK || grid[east][y].getTile() == Tile.UNAVAILABLE)
                free++;
            else
                occupied++;
        }
        if(west>=0) {
            if (grid[west][y].getTile() == Tile.BLANK || grid[west][y].getTile() == Tile.UNAVAILABLE)
                free++;
            else
                occupied++;
        }

        return occupied > 0 && free > 0;

    }


    /**
     * This method removes the tile the player has chosen.
     * @param x x coordinate of the tile the player has chosen.
     * @param y y coordinate of the tile the player has chosen.
     * @return the tile the player has picked.
     */
    public Tile removeTile(int x, int y) {
        Tile tile = grid[x][y].getTile();

        grid[x][y].setTile(Tile.BLANK);

        return tile;
    }

    /**
     * This method removes tiles from the queue to refill the Board when needed.
     * @return An enumeration of type Tile.
     */
    private Tile insertTile() {
        return tiles.poll();
    }
}
