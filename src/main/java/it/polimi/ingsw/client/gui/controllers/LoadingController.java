package it.polimi.ingsw.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
/**
 * The LoadingController class implements the GUIController interface
 * and controls the loading scene of the game.
 *
 * @author Younes Bamhaoud
 */
public class LoadingController implements GUIController {

    @FXML
    private Label status;

    @FXML
    private AnchorPane playersNumberPane;

    @FXML
    private RadioButton players_2;

    @FXML
    private RadioButton players_3;

    @FXML
    private RadioButton players_4;

    @FXML
    private Label waitingLabel;

    @FXML
    private ProgressBar progressBar;

    private GUIManager guiManager;

    /**
     * Sets the GUI manager for the controller.
     *
     * @param guiManager The GUI manager to be set.
     */
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    /**
     * Updates the status label with the specified text.
     *
     * @param s The text to be set as the status.
     */
    public void updateStatus(String s) {
        status.setText(s);
    }

    /**
     * Switches to the waiting mode, hiding the playersNumberPane and showing the waitingLabel and progressBar.
     */
    public void waitingMode() {
        playersNumberPane.setVisible(false);
        waitingLabel.setVisible(true);
        progressBar.setVisible(true);
    }

    /**
     * Resets the loading scene, showing the playersNumberPane and hiding the waitingLabel and progressBar.
     */
    public void resetLoading() {
        playersNumberPane.setVisible(true);
        waitingLabel.setVisible(false);
        progressBar.setVisible(false);
    }

    /**
     * Handles the event when the player number is chosen.
     * Determines the selected player number and notifies the GUI manager.
     */
    public void PlayerNumberChosen() {
        String playerNumber = "1";
        if (players_2.isSelected())
            playerNumber = "2";
        else if (players_3.isSelected())
            playerNumber = "3";
        else if (players_4.isSelected())
            playerNumber = "4";
        guiManager.firePC("action", null, "PLAYERS " + playerNumber);
    }

}
