package it.polimi.ingsw.model;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * This class represents the game's board.
 */
public class Board {

    /**
     * This variable represents the board.
     */
    Cell[][] grid = new Cell[9][9];

    private Set<Tile> initTileSet;
    private Queue<Tile> tiles;



    /**
     * This attribute represents the maximum number of tiles the board can have.
     */
    public static final int MAXTILES = 22;

    public Board (){
        initTileSet = new HashSet<>();
        for(Tile type : Tile.values())
            for(int i=0; i<MAXTILES; i++)
                initTileSet.add(type);
        tiles = new PriorityQueue<>(initTileSet);
    }

    /**
     * This method refills the board when needed (if all the tiles are isolated or if the board is empty).
     */
    void updateBoard(){

    }

    /**
     * This method is used to pick a tile in order to refill the game board.
     * Note that this method removes the Object from the queue.
     * @return An enumeration of type Tile
     * @author Nicolo' Gandini
     */
    public Tile pickTile() {
        return tiles.poll();
    }

    /**
     * This method checks if the board must be refilled (if the tiles in the board are isolated).
     * @return true if the board must be refilled.
     */
    boolean refillNeeded() {
        return false;
    }

    /**
     * This method checks is a tile can be picked or not (so if it has at least one free side and at least one
     * occupied side).
     * @param x x coordinate of the cell to check.
     * @param y y coordinate of the cell to check.
     * @return true if the tile can be picked.
     */
    boolean isPickable(int x, int y) {
        return false;
    }


    /**
     * This method removes the tile the player has chosen.
     * @param x x coordinate of the tile the player has chosen.
     * @param y y coordinate of the tile the player has chosen.
     * @return the tile the player has picked.
     */
    Tile removeTile(int x, int y) {
        return grid[x][y].getTile();
    }

}
