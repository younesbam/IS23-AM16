package it.polimi.ingsw.model.board;

import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.common.exceptions.CellNotEmptyException;
import it.polimi.ingsw.common.exceptions.WrongCoordinateException;
import it.polimi.ingsw.common.exceptions.WrongTilesException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Tile;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.*;

import static it.polimi.ingsw.Const.*;
import static it.polimi.ingsw.Const.RESET_COLOR;
import static java.util.Collections.shuffle;

/**
 * This class represents the game's board.
 */
public abstract class Board implements Serializable {

    /**
     * This variable represents the board.
     * The board is composed by cells.
     */
    protected Cell[][] grid = new Cell[MAXBOARDDIM][MAXBOARDDIM];

    /**
     * List of all tiles.
     */
    private final LinkedList<Tile> initTileList;

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
                for (int i = 0; i < MAXTILES; i++){
                    initTileList.add(tile);
                }
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
     * @return true if the board must be refilled.
     */
    private boolean refillNeeded() {
        // If there is at least one cell pickable there is no need to refill.
        for (int i = 0; i < MAXBOARDDIM; i++)
            for (int j = 0; j < MAXBOARDDIM; j++)
                if (isPickable(i, j))
                    return false;

        return true;
    }

    /**
     * Restore tiles on the board following a picking action and disconnection of the player.
     * @param map containing tiles picked and coordinate of where tiles were picked.
     * @throws WrongTilesException wrong tiles passed.
     * @throws WrongCoordinateException wrong coordinates passed.
     * @throws CellNotEmptyException wrong cell selected. The cell is not empty.
     */
    public void restoreTiles(Map<Tile, Coordinate> map) throws WrongTilesException, WrongCoordinateException, CellNotEmptyException {
        Tile tile;
        int row, col;
        // Iterate all over the map for preliminary checks
        for (Map.Entry<Tile, Coordinate> entry : map.entrySet()) {
            row = entry.getValue().getRow();
            col = entry.getValue().getCol();
            tile = entry.getKey();

            if(tile == Tile.BLANK || tile == Tile.UNAVAILABLE) throw new WrongTilesException();
            if(row >= MAXBOARDDIM || row < 0 || col >= MAXBOARDDIM || col < 0) throw new WrongCoordinateException();
            if(grid[row][col].getTile() != Tile.BLANK) throw new CellNotEmptyException();
        }
        // Iterate all over the map to restore tiles
        for (Map.Entry<Tile, Coordinate> entry : map.entrySet()) {
            row = entry.getValue().getRow();
            col = entry.getValue().getCol();
            tile = entry.getKey();

            grid[row][col].setTile(tile);
        }
    }

    /**
     * This method checks is a tile can be picked or not (so if it has at least one free side and at least one
     * occupied side).
     * Check also the validity of the parameters
     * @param row row coordinate of the cell to check.
     * @param col col coordinate of the cell to check.
     * @return true if the tile can be picked.
     */
    public boolean isPickable(int row, int col) throws InvalidParameterException {
        // Exception if coordinates are wrong
        if(row > MAXBOARDDIM-1 || col > MAXBOARDDIM-1 || row < 0 || col < 0) throw new InvalidParameterException();

        if(!grid[row][col].getTile().name().equals("BLANK") && !grid[row][col].getTile().name().equals("UNAVAILABLE")) {

            // These variables represent the adjacent cells.
            int west = col - 1;
            int south = row + 1;
            int east = col + 1;
            int north = row - 1;


            // Number of free and occupied cells.
            int free = 0;
            int occupied = 0;

            if (west >= 0) {
                if (grid[row][west].getTile() == Tile.BLANK || grid[row][west].getTile() == Tile.UNAVAILABLE)
                    free++;
                else
                    occupied++;
            }
            if (east < MAXBOARDDIM) {
                if (grid[row][east].getTile() == Tile.BLANK || grid[row][east].getTile() == Tile.UNAVAILABLE)
                    free++;
                else
                    occupied++;
            }
            if (south < MAXBOARDDIM) {
                if (grid[south][col].getTile() == Tile.BLANK || grid[south][col].getTile() == Tile.UNAVAILABLE)
                    free++;
                else
                    occupied++;
            }
            if (north >= 0) {
                if (grid[north][col].getTile() == Tile.BLANK || grid[north][col].getTile() == Tile.UNAVAILABLE)
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
     * @param row row of the tile the player has chosen.
     * @param col column of the tile the player has chosen.
     * @return the tile the player has picked.
     */
    public Tile removeTile(int row, int col) {
        Tile tile = grid[row][col].getTile();

        grid[row][col].setTile(Tile.BLANK);

        return tile;
    }

    /**
     * This method removes tiles from the queue to refill the Board when needed.
     * @return An enumeration of type Tile.
     */
    private Tile insertTile() {
        return initTileList.poll();
    }


    /**
     * This method returns the tile at the requested coordinates.
     * @param row row
     * @param col column
     * @return
     */
    public Tile getTile(int row, int col) {return grid[row][col].getTile();}


    /**
     * Method used to print the board.
     */
    public void printBoard() {
        for (int i = 0; i < MAXBOARDDIM; i++) {
            System.out.print(CYAN_BOLD_COLOR + i + RESET_COLOR);
            System.out.print(" [  ");
            for (int j = 0; j < MAXBOARDDIM; j++) {
                switch (grid[i][j].getTile().name()) {
                    case "BLANK" -> System.out.print(BLACK_COLOR + " " + RESET_COLOR + "  ");
                    case "WHITE" -> System.out.print(WHITE_COLOR + " " + RESET_COLOR + "  ");
                    case "LIGHTBLUE" -> System.out.print(CYAN_COLOR + " " + RESET_COLOR + "  ");
                    case "BLUE" -> System.out.print(BLUE_COLOR + " " + RESET_COLOR + "  ");
                    case "YELLOW" -> System.out.print(YELLOW_COLOR + " " + RESET_COLOR + "  ");
                    case "GREEN" -> System.out.print(GREEN_COLOR + " " + RESET_COLOR + "  ");
                    case "PINK" -> System.out.print(PURPLE_COLOR + " " + RESET_COLOR + "  ");
                    case "RED" -> System.out.print(RED_COLOR + " " + RESET_COLOR + "  ");
                    case "UNAVAILABLE" -> System.out.print("x  ");
                }
            }
            System.out.print("]");
            System.out.println();
        }

        // Print column numbers
        System.out.print("     ");
        for (int k = 0; k < MAXBOARDDIM; k++) {
            System.out.print(CYAN_BOLD_COLOR + k + RESET_COLOR);
            System.out.print("  ");
        }
        System.out.println();
    }

    /**
     * Method used to get the board for GUI.
     * @return array of strings.
     */
    public String[][] getBoardforGUI() {
        String[][] board = new String[MAXBOARDDIM][MAXBOARDDIM];
        for (int i = 0; i < MAXBOARDDIM; i++) {
            for (int j = 0; j < MAXBOARDDIM; j++) {
                board[i][j] = grid[i][j].getTile().name();
            }
        }
        return board;
    }
}

