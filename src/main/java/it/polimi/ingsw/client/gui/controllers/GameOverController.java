package it.polimi.ingsw.client.gui.controllers;

import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class GameOverController implements GUIController{
    private GUIManager guiManager;
    @FXML
    private Label points;
    @FXML
    private Label playerResult;
    @FXML
    private ImageView trophy;
    @FXML
    AnchorPane ranking;
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void setPointsMsg(String message){
        points.setText(message);
        playerResult.setVisible(false);
        trophy.setVisible(false);
    }

    public void setRanking(String s){
        List<String> lines = s.lines().toList();
        for (int i = 1; i < lines.size() ; i++) {
            ((Label)ranking.getChildren().get(i-1)).setText(lines.get(i));
            ranking.getChildren().get(i-1).setVisible(true);
            ((Label) ranking.getChildren().get(i-1)).setTextAlignment(TextAlignment.CENTER);
        }
    }

    public void setPlayerResult(String s){
        playerResult.setText(s);
        playerResult.setVisible(true);
        if(s.contains("winner"))
            trophy.setVisible(true);
    }

}
