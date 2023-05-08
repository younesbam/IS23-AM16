package it.polimi.ingsw.client;

import it.polimi.ingsw.client.common.Connection;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class receives the action performed by the user from the CLI, and calls the right method to execute it.
 */

public class Dispatcher implements PropertyChangeListener {

    private final ModelView modelView;
    private final InputChecker inputChecker;
    private final Connection clientConnection;


    /**
     * Class constructor.
     * @param modelView
     * @param clientConnection generic client connection.
     */
    public Dispatcher(ModelView modelView, Connection clientConnection) {
        this.clientConnection = clientConnection;
        this.inputChecker = new InputChecker(getModelView().getCli(), modelView, clientConnection);
        this.modelView = modelView;
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
     * @param nameOfAction
     * @return
     */
    public synchronized boolean actionTaken(String value, String nameOfAction){
        GameAction action;

        String[] input = value.split(" ");

        switch (nameOfAction.toUpperCase()){
            case "PICKTILES" -> message = inputChecker.checkTiles(input);
        }

        if (message != null) {
            clientConnection.sendToServer(message);
            return true;
        }


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
            if (!actionTaken(evt.getNewValue().toString(), evt.getPropertyName())) {
                modelView.setIsYourTurn(true);
            }
        }
    }


}
