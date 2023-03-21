package it.polimi.ingsw.model;

import java.awt.*;

/**
 * This class represents the object tiles.
 */
public class ObjectTile {

    /**
     * This variable specifies the color of the object tile.
     */
    Color color;

    /**
     * This variable specifies whether the object tile is pickable or not.
     */
    Boolean pickable;

    /**
     * This method sets the color for the object tile.
     * It is the class' constructor.
     */
    void ObjectTile() {
        this.color = color;
    }

    /**
     * This method returns the object tile's color.
     * @return
     */
    Color getColor() {
        return this.color;
    }

}
