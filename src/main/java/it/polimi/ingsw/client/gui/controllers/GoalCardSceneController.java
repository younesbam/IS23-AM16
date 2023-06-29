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
    @FXML private ImageView goalCardToken1;
    @FXML private ImageView goalCardToken2;
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

    public void setTokens(int tokenID, String path){
        if(tokenID == 0){
            goalCardToken1.setImage(new Image(GUI.class.getResourceAsStream(path)));
            goalCardToken1.setVisible(true);
        }
        else{
            goalCardToken2.setImage(new Image(GUI.class.getResourceAsStream(path)));
            goalCardToken2.setVisible(true);
        }
    }
}
