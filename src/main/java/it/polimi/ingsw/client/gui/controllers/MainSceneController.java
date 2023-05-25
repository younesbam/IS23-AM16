package it.polimi.ingsw.client.gui.controllers;

public class MainSceneController implements GUIController{
    private GUIManager guiManager;

    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }
}
