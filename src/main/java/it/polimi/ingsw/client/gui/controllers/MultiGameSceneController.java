package it.polimi.ingsw.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class MultiGameSceneController implements GUIController{
    private GUIManager guiManager;
    @FXML
    TextField gameName;

    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void createGame(){
        if(!checkInput()){
            guiManager.firePC("action", null, "CREATE "+ gameName.getText());
            guiManager.loadGame();
        }

    }

    public void joinGame(){
        if(!checkInput()) {
            guiManager.firePC("action", null, "JOIN " + gameName.getText());
            guiManager.loadGame();
        }
    }

    private boolean checkInput(){
        Boolean error = true;
        StringBuilder message = new StringBuilder("");
        if (gameName.getText().contains(" "))
            message.append("Game name must not contain spaces!");
        else if (gameName.getText().equals(""))
            message.append("Game name is missing!");
        else error = false;
        if(error){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Input Error!");
            alert.setContentText(message.toString());
            alert.showAndWait();
        }
        return error;
    }
}
