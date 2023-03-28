package it.polimi.ingsw.model;

/**
 * This class represents the cell.
 */
public class Cell {
    /**
     * This attribute represents the object tile contained in the cell.
     */
    Tile tile;

    /**
     * This variable specifies the x coordinate of the cell.
     */
    int x;

    /**
     * This variable specifies the y coordinate of the cell.
     */
    int y;


    /**
     * This method returns the tile contained in the cell.
     * @return
     */
    public Tile getTile(){
        return tile;
    }

    /**
     * This method sets the tile in the cell.
     * @param tile
     */
    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
