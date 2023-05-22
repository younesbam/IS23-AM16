package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Tile;

import java.io.Serializable;
import java.util.*;

import static it.polimi.ingsw.Const.*;
import static it.polimi.ingsw.Const.RESET;
import static java.util.Collections.shuffle;

/**
 * This class represents the game's board.
 * @author Francesca Rosa Diz.
 */
public abstract class Board implements Serializable {

    /*This variable represents the board.
    The board is composed by cells.*/
    protected Cell[][] grid = new Cell[MAXBOARDDIM][MAXBOARDDIM];

    // Set of all the tiles.
    private LinkedList<Tile> initTileList;

    // Queue of the tiles.
    private Queue<Tile> tiles;

    /**
     * Constructor of the board, used by the subclasses.
     */
    public Board() {
        /*
        Creation of the tile queue
         */
        initTileList = new LinkedList<>();
        for (Tile tile : Tile.values())
            if (!tile.name().equals("BLANK") && !tile.name().equals("UNAVAILABLE")) {
                for (int i = 0; i < MAXTILES; i++)
                    initTileList.add(tile);
            }

        shuffle(initTileList);
    }


    /**
     * This method refills the board when needed (if all the tiles are isolated or if the board is empty).
     */
    public void updateBoard() {

        // If there is no need to refill, the board isn't updated.

        if (!refillNeeded())
            return;

        for (int i = 0; i < MAXBOARDDIM; i++)
            for (int j = 0; j < MAXBOARDDIM; j++)
                if (grid[i][j].getTile() == Tile.BLANK)
                    grid[i][j].setTile(insertTile());
    }

    /**
     * This method checks if the board must be refilled (if the tiles in the board are isolated).
     *
     * @return true if the board must be refilled.
     */
    public boolean refillNeeded() {

        // If there is at least one cell pickable there is no need to refill.
        for (int i = 0; i < MAXBOARDDIM; i++)
            for (int j = 0; j < MAXBOARDDIM; j++)
                if (isPickable(i, j))
                    return false;

        return true;
    }

    /**
     * This method checks is a tile can be picked or not (so if it has at least one free side and at least one
     * occupied side).
     *
     * @param x x coordinate of the cell to check.
     * @param y y coordinate of the cell to check.
     * @return true if the tile can be picked.
     */
    public boolean isPickable(int x, int y) {

        if(!grid[x][y].getTile().name().equals("BLANK") && !grid[x][y].getTile().name().equals("UNAVAILABLE")) {

            // These variables represent the adjacent cells.
            int west = y - 1;
            int south = x + 1;
            int east = y + 1;
            int north = x - 1;


            // Number of free and occupied cells.
            int free = 0;
            int occupied = 0;

            if (west >= 0) {
                if (grid[x][west].getTile() == Tile.BLANK || grid[x][west].getTile() == Tile.UNAVAILABLE)
                    free++;
                else
                    occupied++;
            }
            if (east < MAXBOARDDIM) {
                if (grid[x][east].getTile() == Tile.BLANK || grid[x][east].getTile() == Tile.UNAVAILABLE)
                    free++;
                else
                    occupied++;
            }
            if (south < MAXBOARDDIM) {
                if (grid[south][y].getTile() == Tile.BLANK || grid[south][y].getTile() == Tile.UNAVAILABLE)
                    free++;
                else
                    occupied++;
            }
            if (north >= 0) {
                if (grid[north][y].getTile() == Tile.BLANK || grid[north][y].getTile() == Tile.UNAVAILABLE)
                    free++;
                else
                    occupied++;
            }

            return occupied > 0 && free > 0;
        }
        else
            return false;
    }


    /**
     * This method removes the tile the player has chosen.
     *
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
     *
     * @return An enumeration of type Tile.
     */
    private Tile insertTile() {
        return initTileList.poll();
    }


    /**
     * This method returns the tile at the requested coordinates.
     *
     * @param x
     * @param y
     * @return
     */
    public Tile getTile(int x, int y) {
        return grid[x][y].getTile();
    }


    /**
     * Method used to print the board.
     */
    public void printBoard() {
        for (int i = 0; i < MAXBOARDDIM; i++) {
            System.out.print("[  ");
            for (int j = 0; j < MAXBOARDDIM; j++) {
                switch (grid[i][j].getTile().name()) {
                    case "BLANK" -> System.out.print(BLACKCOLOR + " " + RESET + "  ");
                    case "WHITE" -> System.out.print(WHITECOLOR + " " + RESET + "  ");
                    case "LIGHTBLUE" -> System.out.print(CYANCOLOR + " " + RESET + "  ");
                    case "BLUE" -> System.out.print(BLUECOLOR + " " + RESET + "  ");
                    case "YELLOW" -> System.out.print(YELLOWCOLOR + " " + RESET + "  ");
                    case "GREEN" -> System.out.print(GREENCOLOR + " " + RESET + "  ");
                    case "PINK" -> System.out.print(PURPLECOLOR + " " + RESET + "  ");
                    case "RED" -> System.out.print(REDCOLOR + " " + RESET + "  ");
                    case "UNAVAILABLE" -> System.out.print("x  ");
                }
            }
            System.out.print("]");
            System.out.println();
        }
    }
}

