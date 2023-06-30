package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.model.BookShelf;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
/**
 * The BookShelf1PlayerController class extends the BookShelfSceneController class
 * and controls the bookshelf scene for a single opponent player.
 *
 * @author Younes Bamhaoud
 */
public class BookShelf1PlayerController extends BookShelfSceneController {

    @FXML
    Label username;

    @FXML
    GridPane bookShelfGrid;

    /**
     * Sets the view of the bookshelf scene for a single player.
     * Retrieves the opponent's bookshelf from the game model and prints it on the grid.
     * Sets the username label to display the opponent's username.
     */
    public void setView() {
        int opponentID = 1 - guiManager.getPlayerID();
        BookShelf bookShelf = guiManager.getModelView().getGame().getPlayerByID(opponentID).getBookShelf();
        printBookShelf(bookShelfGrid, bookShelf);
        this.username.setText(guiManager.getModelView().getGame().getPlayerByID(opponentID).getUsername() + "'s Bookshelf");
    }
}
