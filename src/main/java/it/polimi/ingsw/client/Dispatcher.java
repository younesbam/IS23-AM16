package it.polimi.ingsw.client;

import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class receives the action performed by the user from the CLI, and calls the right method to execute it.
 */

public class Dispatcher implements PropertyChangeListener {

    private final ModelView modelView;
    private final InputChecker inputChecker;
    private final Client client;


    /**
     * Class constructor.
     * @param modelView
     * @param client generic client connection.
     */
    public Dispatcher(ModelView modelView, Client client) {
        this.client = client;
        this.modelView = modelView;
        this.inputChecker = new InputChecker(getModelView().getCli(), modelView, client);
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
        GameAction messageToServer = null;

        String[] input = value.split(" ");

        switch (nameOfAction.toUpperCase()){
            case "PICKTILES" -> messageToServer = inputChecker.checkTiles(input);
        }





        if (messageToServer != null) {
            client.sendToServer(messageToServer);
            return true;
        }
        else
            return false;
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
