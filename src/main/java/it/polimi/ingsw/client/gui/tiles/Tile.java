package it.polimi.ingsw.client.gui.tiles;

import javafx.scene.Cursor;
import javafx.scene.shape.Rectangle;

/**
 * Tile class is the gui representation of item tile in the GridPane.
 *
 * @author Younes Bamhaoud
 */
public class Tile extends Rectangle{

    private int abscissa;
    private int ordinate;
    private static final double WIDTH = 45.0;
    private static final double HEIGHT = 45.0;

    /**
     * Constructor Tile creates a new Tile instance.
     *
     * @param x of type int - the row of the cell.
     * @param y of type int - the column of the cell.
     */
    public Tile(int x, int y) {
        this.abscissa = x;
        this.ordinate = y;
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        makeSelectable();
    }
    /** Method makeSelectable makes the tile selectable. */
    public void makeSelectable() {
        setOnMouseEntered(mouseEvent -> setCursor(Cursor.HAND));
    }

    /**
     * Returns the x-coordinate (row) of the tile.
     *
     * @return The abscissa of the tile.
     */
    public int getAbscissa() {
        return abscissa;
    }

    /**
     * Returns the y-coordinate (column) of the tile.
     *
     * @return The ordinate of the tile.
     */
    public int getOrdinate() {
        return ordinate;
    }

}
