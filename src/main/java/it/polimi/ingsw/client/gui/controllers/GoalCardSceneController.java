package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
/**
 * The GoalCardSceneController class implements the GUIController interface
 * and controls the goal card scene.
 *
 * @author Younes Bamhaoud
 */
public class GoalCardSceneController implements GUIController {

    private GUIManager guiManager;

    @FXML
    private ImageView personalGoalCard;

    @FXML
    private ImageView commonGoalCard1;

    @FXML
    private ImageView commonGoalCard2;

    @FXML
    private ImageView goalCardToken1;

    @FXML
    private ImageView goalCardToken2;

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
     * Sets the image of the personal goal card.
     *
     * @param path the path to the image file
     */
    public void setPersonalGoalCard(String path) {
        personalGoalCard.setImage(new Image(GUI.class.getResourceAsStream(path)));
    }

    /**
     * Sets the image of the first common goal card.
     *
     * @param path the path to the image file
     */
    public void setCommonGoalCard1(String path) {
        commonGoalCard1.setImage(new Image(GUI.class.getResourceAsStream(path)));
    }

    /**
     * Sets the image of the second common goal card.
     *
     * @param path the path to the image file
     */
    public void setCommonGoalCard2(String path) {
        commonGoalCard2.setImage(new Image(GUI.class.getResourceAsStream(path)));
    }

    /**
     * Sets the token image based on the token ID.
     *
     * @param tokenID the ID of the token (0 or 1)
     * @param path    the path to the image file
     */
    public void setTokens(int tokenID, String path) {
        if (tokenID == 0) {
            goalCardToken1.setImage(new Image(GUI.class.getResourceAsStream(path)));
            goalCardToken1.setVisible(true);
        } else {
            goalCardToken2.setImage(new Image(GUI.class.getResourceAsStream(path)));
            goalCardToken2.setVisible(true);
        }
    }
}
