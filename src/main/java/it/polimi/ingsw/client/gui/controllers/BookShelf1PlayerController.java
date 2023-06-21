package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.model.BookShelf;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class BookShelf1PlayerController extends BookShelfSceneController{
    @FXML
    Label username;
    @FXML
    GridPane bookShelfGrid;

    public void setView(){
        int opponentID = 1 - guiManager.getPlayerID();
        BookShelf bookShelf = guiManager.getModelView().getGame().getPlayerByID(opponentID).getBookShelf();
        printBookShelf(bookShelfGrid, bookShelf);
        this.username.setText(guiManager.getModelView().getGame().getPlayerByID(opponentID).getUsername()+"'s Bookshelf");
    }
}
