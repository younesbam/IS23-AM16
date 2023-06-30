package it.polimi.ingsw.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


/**
 * The MultiGameSceneController class implements the GUIController interface
 * and controls the multiplayer game scene.
 */
public class MultiGameSceneController implements GUIController {
    private GUIManager guiManager;
    @FXML
    TextField gameName;

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
     * Creates a new game if the input is valid and loads the game scene.
     */
    public void createGame() {
        if (!checkInput()) {
            guiManager.firePC("action", null, "CREATE " + gameName.getText());
            guiManager.loadGame();
        }
    }

    /**
     * Joins an existing game if the input is valid and loads the game scene.
     */
    public void joinGame() {
        if (!checkInput()) {
            guiManager.firePC("action", null, "JOIN " + gameName.getText());
            guiManager.loadGame();
        }
    }

    /**
     * Checks the input for any errors.
     *
     * @return true if there are errors in the input, false otherwise
     */
    private boolean checkInput() {
        Boolean error = true;
        StringBuilder message = new StringBuilder("");

        if (gameName.getText().contains(" "))
            message.append("Game name must not contain spaces!");
        else if (gameName.getText().equals(""))
            message.append("Game name is missing!");
        else
            error = false;

        if (error) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Input Error!");
            alert.setContentText(message.toString());
            alert.showAndWait();
        }

        return error;
    }
}
