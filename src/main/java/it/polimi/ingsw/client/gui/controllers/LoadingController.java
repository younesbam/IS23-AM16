package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.HowManyPlayersRequest;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.Optional;

public class LoadingController implements GUIController{
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

    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }
    public void updateStatus(String s){status.setText(s);}
    public String getStatus(){return status.getText();}
    public void setWaitingLabel(String s){waitingLabel.setText(s);}
    public void waitingMode(){
        playersNumberPane.setVisible(false);
        waitingLabel.setVisible(true);
        progressBar.setVisible(true);
    }

    public void resetLoading(){
        playersNumberPane.setVisible(true);
        waitingLabel.setVisible(false);
        progressBar.setVisible(false);
    }
    public void PlayerNumberChosen(){
        String playerNumber="1";
        if(players_2.isSelected())
            playerNumber="2";
        else if(players_3.isSelected())
            playerNumber="3";
        else if(players_4.isSelected())
            playerNumber="4";
        guiManager.firePC("action", null, "PLAYERS "+playerNumber);
    }
    /*private void howManyPlayerRequest(String s){
        //message.setText(s);


        //setSetupMode(true);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Lobby capacity");
        alert.setHeaderText("Choose the number of players.");
        alert.setContentText(((HowManyPlayersRequest) guiManager.getModelView().getAnswerFromServer().getAnswer()).toString());

        ButtonType two = new ButtonType("2");
        ButtonType three = new ButtonType("3");
        ButtonType four = new ButtonType("4");
        alert.getButtonTypes().setAll(two, three);
        Optional<ButtonType> result = alert.showAndWait();
        int players = 0;
        if (result.isPresent() && result.get() == two) {
            players = 2;
        } else if (result.isPresent() && result.get() == three) {
            players = 3;
        } else if (result.isPresent() && result.get() == four) {
            players = 4;
        }
        //pcsDispatcher.firePropertyChange("playerResponse", null, players);

        //updateTurn(false);
    }
     */

}
