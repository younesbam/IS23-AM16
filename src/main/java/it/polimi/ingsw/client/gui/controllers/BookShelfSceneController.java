package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.tiles.Tile;
import it.polimi.ingsw.model.BookShelf;
import it.polimi.ingsw.model.Cell;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;
/**
 * The BookShelfSceneController class is an abstract class that implements the GUIController interface
 * and provides common functionality for controlling the bookshelf scene.
 *
 * @author Younes Bamhaoud
 */
public abstract class BookShelfSceneController implements GUIController {

    protected GUIManager guiManager;
    protected int imageChoise;
    private static final String IMAGEPATH = "/graphics/item_tiles/";

    /**
     * Sets the GUIManager instance for this controller.
     *
     * @param guiManager the GUIManager instance to set
     */
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    /**
     * Prints the bookshelf grid on the specified GridPane based on the given BookShelf object.
     *
     * @param bookShelfGrid the GridPane on which to print the bookshelf
     * @param bookShelf     the BookShelf object representing the grid
     */
    protected void printBookShelf(GridPane bookShelfGrid, BookShelf bookShelf) {
        imageChoise = guiManager.getModelView().getGame().getNumOfPlayers() - 1;
        bookShelfGrid.getChildren().clear();
        Cell[][] grid = bookShelf.getGrid();
        for (int i = 0; i < MAXBOOKSHELFROW; i++) {
            for (int j = 0; j < MAXBOOKSHELFCOL; j++) {
                if (!(grid[i][j].getTile().name().equals("BLANK") || grid[i][j].getTile().name().equals("UNAVAILABLE"))) {
                    Tile tile = new Tile(i, j);
                    //imageChoise = (i*j)%3+1;
                    tile.setFill(
                            new ImagePattern(
                                    new Image(GUI.class.getResourceAsStream(IMAGEPATH
                                            + grid[i][j].getTile().name()
                                            + imageChoise
                                            + ".png"))));
                    bookShelfGrid.add(tile, j, i);
                }
            }
        }
    }
}
