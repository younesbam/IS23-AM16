package it.polimi.ingsw.client;

import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.messages.ExitFromGame;
import it.polimi.ingsw.communications.clientmessages.messages.Message;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class receives the action performed by the user from the CLI, and calls the right method to execute it.
 */

public class Dispatcher implements PropertyChangeListener {

    private final ModelView modelView;
    private final InputValidator inputValidator;
    private final Client client;


    /**
     * Class constructor.
     * @param modelView
     * @param client generic client connection.
     */
    public Dispatcher(ModelView modelView, Client client) {
        this.client = client;
        this.modelView = modelView;
        if(modelView.isGui())
            this.inputValidator = new InputValidator(getModelView().getGui(), modelView, client);
        else
            this.inputValidator = new InputValidator(getModelView().getCli(), modelView, client);
    }


    /**
     * ModelView getter.
     * @return
     */
    public ModelView getModelView() {
        return modelView;
    }


    /**
     * This method is being called by the PropertyChange method, and it handles the dispatch of the action taken by the user, by calling the right methods
     * in order to do so. It also makes a check if the user can actually perform that action by calling the InputChecker.
     * @param value
     * @param propertyName
     * @return
     */
    public synchronized boolean dispatchAction(String value, String propertyName){
        SerializedMessage messageToServer = null;

        String[] splitInput = value.split(" ");
        String cmd = splitInput[0];

        if("action".equals(propertyName)){
            switch (cmd.toUpperCase()){
                case "PLAYERS" -> messageToServer = new SerializedMessage(client.getID(), inputValidator.players(splitInput));
                case "PICKTILES" -> messageToServer = new SerializedMessage(client.getID(), inputValidator.pickTiles(splitInput));
                case "PLACETILES" -> messageToServer = new SerializedMessage(client.getID(), inputValidator.placeTiles(splitInput));
                case "CHECKSCHEME" -> messageToServer = new SerializedMessage(client.getID(), inputValidator.checkScheme(splitInput));

                case "MAN" -> inputValidator.manual();
                case "EXIT" -> exitGame();
                default -> System.out.println("Incomprehensible input. Please try again");
            }

            // Send game action to server
            if (messageToServer != null) {
                if(messageToServer.message != null)
                    client.sendToServer(messageToServer.message);
                else if (messageToServer.gameAction != null)
                    client.sendToServer(messageToServer.gameAction);
                return true;
            }
        }
        return false;
    }


    /**
     * Quit game command.
     */
    public void exitGame(){
        Message message = new ExitFromGame();
        client.sendToServer(message);
        System.err.println("Disconnected from the server.");
        //System.exit(0);
        //cli.disconnectFromServer();
    }


    /**
     * This method manages the event changes happening in the CLI.
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (!modelView.getIsYourTurn()) {
            System.out.println("Unable to complete the action, wait for your turn!");
        } else {
            if (!dispatchAction(evt.getNewValue().toString(), evt.getPropertyName())) {
                modelView.setIsYourTurn(true);
            }
        }
    }
}
