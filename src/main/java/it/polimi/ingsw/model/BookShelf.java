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
     * This method checks whether the column chosen by the player is correct or not.
     * The column is correct if it has a number of free cells at least equals to the number of tiles
     * the player wants to insert.
     * @param col Number of the column chosen by the player.
     * @param tiles Number of tiles the player wants to insert.
     * @return True if the play is valid, false otherwise.
     */
    Boolean checkColumn(int col, int tiles) {
        int n = col;

        return false;
    }

}
