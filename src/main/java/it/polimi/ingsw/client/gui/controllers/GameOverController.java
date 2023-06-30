package it.polimi.ingsw.client.gui.controllers;

import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;

import java.util.List;
/**
 * The GameOverController class implements the GUIController interface
 * and controls the game over scene.
 *
 * @author Younes Bamhaoud
 */
public class GameOverController implements GUIController {

    private GUIManager guiManager;

    @FXML
    private Label points;

    @FXML
    private Label playerResult;

    @FXML
    private ImageView trophy;

    @FXML
    AnchorPane ranking;

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
     * Sets the points message to be displayed.
     *
     * @param message the message to set
     */
    public void setPointsMsg(String message) {
        points.setText(message);
        playerResult.setVisible(false);
        trophy.setVisible(false);
    }

    /**
     * Sets the ranking information.
     *
     * @param s the ranking information as a string
     */
    public void setRanking(String s) {
        List<String> lines = s.lines().toList();
        for (int i = 1; i < lines.size(); i++) {
            ((Label) ranking.getChildren().get(i - 1)).setText(lines.get(i));
            ranking.getChildren().get(i - 1).setVisible(true);
            ((Label) ranking.getChildren().get(i - 1)).setTextAlignment(TextAlignment.CENTER);
        }
    }

    /**
     * Sets the result of the player.
     *
     * @param s the player's result as a string
     */
    public void setPlayerResult(String s) {
        playerResult.setText(s);
        playerResult.setVisible(true);
        if (s.contains("winner"))
            trophy.setVisible(true);
    }
}
