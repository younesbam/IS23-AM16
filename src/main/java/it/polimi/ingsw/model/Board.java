package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * This class represents the game's board.
 */
public abstract class Board {

    /**
     * This variable represents the board.
     * The board is composed by cells.
     */
    private Cell[][] grid = new Cell[8][8];

    /**
     * ????
     */
    private ArrayList<Tile> objectTilesBag;

    /**
     * This constant represents the maximum number for any tile's type.
     */
    public static final int MAXTILES = 22;

    /**
     * This constant represents the maximum number of columns.
     */
    public static final int MAXCOL = 8;

    /**
     * This constant represents the maximum number of rows.
     */
    public static final int MAXROW = 8;


    /**
     * This method refills the board when needed (if all the tiles are isolated or if the board is empty).
     */
    public void updateBoard(){

    }

    /**
     * This method checks if the board must be refilled (if the tiles in the board are isolated).
     * @return true if the board must be refilled.
     */
    public boolean refillNeeded() {
        return false;
    }

    /**
     * This method checks is a tile can be picked or not (so if it has at least one free side and at least one
     * occupied side).
     * @param x x coordinate of the cell to check.
     * @param y y coordinate of the cell to check.
     * @return true if the tile can be picked.
     */
    public boolean isPickable(int x, int y) {
        return false;
    }


    /**
     * This method removes the tile the player has chosen.
     * @param x x coordinate of the tile the player has chosen.
     * @param y y coordinate of the tile the player has chosen.
     * @return the tile the player has picked.
     */
    public Tile removeTile(int x, int y) {
        Tile tile = grid[x][y].getTile();

        grid[x][y].setTile(tile.BLANK);

        return tile;
    }
}
