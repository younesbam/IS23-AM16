package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GoalCardSceneController implements GUIController{
    private GUIManager guiManager;
    @FXML private ImageView personalGoalCard;
    @FXML private ImageView commonGoalCard1;
    @FXML private ImageView commonGoalCard2;
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void setPersonalGoalCard(String path){
        personalGoalCard.setImage(new Image(GUI.class.getResourceAsStream(path)));
    }

    public void setCommonGoalCard1(String path){
        commonGoalCard1.setImage(new Image(GUI.class.getResourceAsStream(path)));
    }

    public void setCommonGoalCard2(String path){
        commonGoalCard2.setImage(new Image(GUI.class.getResourceAsStream(path)));
    }

}