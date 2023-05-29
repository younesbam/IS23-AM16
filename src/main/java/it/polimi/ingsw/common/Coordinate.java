package it.polimi.ingsw.common;

import java.io.Serializable;

/**
 * Generic coordinate which represent a generic tile's position on the board / bookshelf.
 */
public class Coordinate implements Serializable {
    /**
     * Row
     */
    int row;

    /**
     * Column
     */
    int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
