package it.polimi.ingsw.client.gui.controllers;


import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.common.ConnectionType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;

import static java.lang.Integer.parseInt;
/**
 * The SetupController class implements the GUIController interface
 * and controls the setup process for connecting to a server.
 * @author Younes Bamhaoud
 */
public class SetupController implements GUIController {

    private GUIManager guiManager;

    @FXML
    private TextField username;

    @FXML
    private TextField ipaddress;

    @FXML
    private TextField serverport;

    @FXML
    private Label message;

    @FXML
    private RadioButton socketRadioBtn;

    /**
     * Handles the join action when the user clicks the play button.
     * It performs validation on the input fields and connects to the server if the inputs are valid.
     */
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
            if (socketRadioBtn.isSelected())
                connectionType = ConnectionType.SOCKET;
            else
                connectionType = ConnectionType.RMI;

            guiManager.getModelView().setUsername(username.getText());

            try {
                guiManager.connectToServer(connectionType, ipaddress.getText(), parseInt(serverport.getText()), username.getText());
            } catch (RemoteException | NotBoundException e) {
                Client.LOGGER.log(Level.SEVERE, "Failed to start client-server connection. Check the parameters and try again");
                System.exit(-1);
            }
        }
    }

    /**
     * Sets the GUIManager instance for this controller.
     *
     * @param guiManager the GUIManager instance to set
     */
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }
}
