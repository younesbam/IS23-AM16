package it.polimi.ingsw.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * The CountDownController class implements the GUIController interface
 * and controls the countdown timer scene.
 *
 * @author Younes Bamhaoud
 */
public class CountDownController implements GUIController {

    private GUIManager guiManager;

    @FXML
    private Label time;

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
     * Updates the displayed time on the countdown timer.
     *
     * @param time the time to update and display
     */
    public void updateTime(String time) {
        this.time.setText(time);
    }
}

