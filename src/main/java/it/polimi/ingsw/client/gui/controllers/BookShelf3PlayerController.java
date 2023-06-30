package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.model.BookShelf;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
/**
 * The BookShelf3PlayerController class extends the BookShelfSceneController class
 * and controls the bookshelf scene for three opponent players.
 *
 * @author Younes Bamhaoud
 */
public class BookShelf3PlayerController extends BookShelfSceneController {

    private final int NUM_OF_PLAYERS = 3;

    @FXML
    Label username1;

    @FXML
    GridPane bookShelfGrid1;

    @FXML
    Label username2;

    @FXML
    GridPane bookShelfGrid2;

    @FXML
    Label username3;

    @FXML
    GridPane bookShelfGrid3;

    /**
     * Sets the view of the bookshelf scene for three players.
     * Retrieves the opponents' bookshelves from the game model and prints them on the respective grids.
     * Sets the username labels to display the opponents' usernames.
     */
    public void setView() {
        int opponent1ID = guiManager.getPlayerID() + 1;
        if (opponent1ID > NUM_OF_PLAYERS)
            opponent1ID = 0;

        int opponent2ID = opponent1ID + 1;
        if (opponent2ID > NUM_OF_PLAYERS)
            opponent2ID = 0;

        int opponent3ID = opponent2ID + 1;
        if (opponent3ID > NUM_OF_PLAYERS)
            opponent3ID = 0;

        BookShelf bookShelf1 = guiManager.getModelView().getGame().getPlayerByID(opponent1ID).getBookShelf();
        BookShelf bookShelf2 = guiManager.getModelView().getGame().getPlayerByID(opponent2ID).getBookShelf();
        BookShelf bookShelf3 = guiManager.getModelView().getGame().getPlayerByID(opponent3ID).getBookShelf();

        printBookShelf(bookShelfGrid1, bookShelf1);
        this.username1.setText(guiManager.getModelView().getGame().getPlayerByID(opponent1ID).getUsername() + "'s Bookshelf");

        printBookShelf(bookShelfGrid2, bookShelf2);
        this.username2.setText(guiManager.getModelView().getGame().getPlayerByID(opponent2ID).getUsername() + "'s Bookshelf");

        printBookShelf(bookShelfGrid3, bookShelf3);
        this.username3.setText(guiManager.getModelView().getGame().getPlayerByID(opponent3ID).getUsername() + "'s Bookshelf");
    }
}
