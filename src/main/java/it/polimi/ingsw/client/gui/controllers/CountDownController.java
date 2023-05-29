package it.polimi.ingsw.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CountDownController implements GUIController{
    private GUIManager guiManager;
    @FXML private Label time;
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void updateTime(String time){
        this.time.setText(time);
    }
}
