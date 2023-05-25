package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.common.ConnectionType;
import it.polimi.ingsw.server.connection.CSConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;

import static java.lang.Integer.parseInt;

public class SetupController implements GUIController{
    private GUIManager guiManager;

    @FXML
    private TextField username;
    @FXML private TextField ipaddress;
    @FXML private TextField serverport;
    @FXML private Label message;
    @FXML private RadioButton rmiRadioBtn;
    @FXML private RadioButton socketRadioBtn;
    public void join() {
        ConnectionType connectionType;
        if (username.getText().equals("")
                || ipaddress.getText().equals("")
                || serverport.getText().equals("")) {
            message.setText("Error: missing parameters!");
        } else if (username.getText().length() > 15) {
            message.setText("Error: the maximum length of nickname is 15 characters!");
        } else if (ipaddress.getText().contains(" ")) {
            message.setText("Error: address must not contain spaces!");
        } else {
            if(socketRadioBtn.isSelected())
                connectionType = ConnectionType.SOCKET;
            else
                connectionType = ConnectionType.RMI;
            guiManager.getModelView().setUsername(username.getText());
            //LoaderController loaderController;
            try {
                guiManager.connectToServer(connectionType, ipaddress.getText(), parseInt(serverport.getText()), username.getText());
                Client.LOGGER.log(Level.INFO, "Client successfully connected");
            } catch (RemoteException | NotBoundException e) {
                Client.LOGGER.log(Level.SEVERE, "Failed to start client-server connection: ", e);
                System.exit(-1);
            }
            guiManager.getModelView().setIsYourTurn(true);
            guiManager.firePC("action", null, "PLAYERS 2");
                //guiManager.changeStage("loadingScene.fxml");
                //LoadingController loadingController = (LoadingController) gui.getControllerFromName("loadingScene.fxml");

                /*gui.getListeners()
                        .addPropertyChangeListener(
                                "action", new ActionParser(connection, gui.getModelView()));*/



             /*catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Duplicate nickname");
                alert.setHeaderText("Duplicate nickname!");
                alert.setContentText("This nickname is already in use! Please choose another one.");
                alert.showAndWait();
                gui.changeStage("joinScene.fxml");
            } /*catch (InvalidNicknameException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid character nickname");
                alert.setHeaderText("Special character contained in nickname!");
                alert.setContentText(
                        "Nickname can't contain - special character! Please choose another one");
                alert.showAndWait();
                gui.changeStage("joinScene.fxml");
            }*/

        }
    }

    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }


}
