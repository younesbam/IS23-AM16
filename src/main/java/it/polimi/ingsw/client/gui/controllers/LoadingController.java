package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.HowManyPlayersRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.Optional;

public class LoadingController extends UI implements GUIController{
    private GUI gui;
    @FXML
    private Label message;
    @Override
    public void setGui(GUI gui) {
        this.gui = gui;
        this.pcsDispatcher = new PropertyChangeSupport(gui);
        this.modelView = new ModelView(gui);
        this.actionHandler = new ActionHandler(gui, gui.getModelView());
        setActiveGame(true);
    }

    private void howManyPlayerRequest(String s){
        message.setText(s);


        //setSetupMode(true);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Lobby capacity");
        alert.setHeaderText("Choose the number of players.");
        alert.setContentText(((HowManyPlayersRequest) gui.getModelView().getAnswerFromServer().getAnswer()).toString());

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
        pcsDispatcher.firePropertyChange("playerResponse", null, players);

        //updateTurn(false);
    }
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        switch (event.getPropertyName()){
            case "HowManyPlayersRequest" -> howManyPlayerRequest((String) event.getNewValue());
            //case "SetupCompleted" -> setupCompleted((String) event.getNewValue());
            //case "UpdateTurn" -> updateTurn((Boolean) event.getNewValue());


        }
    }
}
