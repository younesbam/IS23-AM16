package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;

public class ChatController implements GUIController{
    private GUIManager guiManager;

    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }
    
}
