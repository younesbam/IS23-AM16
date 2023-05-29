package it.polimi.ingsw.client.gui.tiles;

import it.polimi.ingsw.client.gui.controllers.MainSceneController;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * Tile class is the gui representation of item tile in the GridPane.
 *
 * @author Younes Bamhaoud
 */
public class Tile extends Rectangle {

    private int x;
    private int y;
    private final MainSceneController mainSceneController;
    private static final double WIDTH = 40.0;
    private static final double HEIGHT = 40.0;

    /**
     * Constructor Tile creates a new Tile instance.
     *
     * @param x of type int - the row of the cell.
     * @param y of type int - the column of the cell.
     * @param mainSceneController of type MainSceneController - the MainSceneController reference.
     */
    public Tile(int x, int y, MainSceneController mainSceneController) {
        this.x = x;
        this.y = y;
        this.mainSceneController = mainSceneController;
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
    }
    /** Method makeSelectable makes the tile selectable. */
    public void makeSelectable() {
        setOnMouseEntered(mouseEvent -> setCursor(Cursor.HAND));

        setOnMouseClicked(
                mouseEvent -> {
                    mainSceneController
                            .getGuiManager();
                            //.firePropertyChange();
                });
    }

    /** Method deselects the tile. */
    public void deselect() {
        setOnMouseEntered(null);
        setOnMouseClicked(null);
    }

    /** Method move handles move action. */
    public void move() {

    }

    /**
     * Method setPosition sets tile's cell.
     *
     * @param x of type int - the row of the cell.
     * @param y of type int - the column of the cell.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }


}
