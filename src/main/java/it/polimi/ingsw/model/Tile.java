package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * This enumerator lists all possible values for tiles.
 */
public enum Tile implements Serializable {
    BLUE,
    PINK,
    WHITE,
    GREEN,
    YELLOW,
    LIGHTBLUE,
    /**
     * Blank identifies an empty cell that can be filled.
     */
    BLANK,
    /**
     * Unavailable identifies an empty cell that can't be filled.
     */
    UNAVAILABLE;

}
