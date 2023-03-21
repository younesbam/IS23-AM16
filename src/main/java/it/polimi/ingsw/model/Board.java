package it.polimi.ingsw.model;

/**
 * This class represents the game's board.
 */
public class Board {

    /**
     * This variable represents the board.
     */
    Cell[][] grid = new Cell[9][9];

    /**
     * This method determines whether a cell is pickable or not.
     * A cell is pickable if it has at least one side free and at least one side occupied.
     * @param cell This is the cell we want to check.
     * @return returns true if the cell is pickable.
     */
    Boolean isPickable(Cell cell) {

        /**
         * This variable represents the cell's x coordinate.
         */
        int x = cell.x;

        /**
         * This variable represents the cell's y coordinate.
         */
        int y = cell.y;


        /**
         * If the cell has all of its side free, the method will return false because the cell isn't pickable.
         */
        if ( grid[x+1][y].occupied == false
                && grid[x-1][y].occupied == false
                && grid[x][y+1].occupied == false
                && grid[x][y-1].occupied == false ){
            return false;
        }


        /**
         * If the cell has all of its side occupied, the method will return false because the cell isn't pickable.
         */
        if ( grid[x+1][y].occupied == true &&
                grid[x-1][y].occupied == true &&
                grid[x][y+1].occupied == true &&
                grid[x][y-1].occupied == true) {
            return false;
        }


        /**
         * If none of the conditions above is verified, the method will return true because the cell is pickable.
         */
        return true;
    }


}
