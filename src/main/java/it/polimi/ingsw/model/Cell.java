package it.polimi.ingsw.model;

/**
 * This class represents the cell.
 */
public class Cell {
    /**
     * This attribute represents the object tile contained in the cell.
     */
    ObjectTile objectTile;
    /**
     * This attribute tells whether the cell is occupied or not.
     */
    Boolean occupied;
    /**
     * This attribute tells whether the cell is available or not.
     */
    Boolean available;

    /**
     * This variable specifies the x coordinate of the cell.
     */
    int x;

    /**
     * This variable specifies the x coordinate of the cell.
     */
    int y;

    /**
     *
     * @return The object tile contained in the cell.
     */
    ObjectTile getObjectTile(){
        return this.objectTile;
    }

    /**
     *
     * @return Whether the cell is occupied or not.
     */
    Boolean getOccupied() {
        boolean b = true;
        if (occupied) {
            return true;
        } else
            return false;
    }

    /**
     *
     * @return Whether the cell is available or not.
     */
    Boolean getAvailable() {
        boolean b = true;
        if (available) {
            return true;
        } else
            return false;
    }

    /**
     * This method is the constructor for the occupancy.
     */
    void setOccupied() {
        this.occupied = occupied;
    }

    /**
     * This method is the constructor for the availability.
     */
    void setAvailable() {
        this.available = available;
    }

}
