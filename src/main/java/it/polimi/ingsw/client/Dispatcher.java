package it.polimi.ingsw.client;

import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.actions.PrintCardsAction;
import it.polimi.ingsw.communications.serveranswers.Answer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static it.polimi.ingsw.Const.CLI_INCOMPR_INPUT;

/**
 * Receives the action performed by the user from the CLI, and calls the right method to execute it.
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
     * @return model view.
     */
    public ModelView getModelView() {
        return modelView;
    }


    /**
     * Called by the PropertyChange method, and it handles the dispatch of the action taken by the user, by calling the right methods
     * in order to do so. It also makes a check if the user can actually perform that action by calling the InputChecker.
     * @param value
     * @param propertyName
     * @return true when the client send message to server. False if no message has to send.
     */
    public synchronized boolean dispatchAction(String value, String propertyName){
        SerializedMessage messageToServer = null;

        String[] splitInput = value.split(" ");
        String cmd = splitInput[0];

        if("action".equals(propertyName)){
            switch (cmd.toUpperCase()){
                // Messages
                case "PLAYERS" -> messageToServer = new SerializedMessage(client.getID(), inputValidator.players(splitInput));
                // Actions
                case "PICKTILES" -> messageToServer = new SerializedMessage(client.getID(), inputValidator.pickTiles(splitInput));
                case "PLACETILES" -> messageToServer = new SerializedMessage(client.getID(), inputValidator.placeTiles(splitInput));

                // Miscellaneous
                case "MAN" -> inputValidator.printManual();
                case "PRINTCARDS" -> messageToServer = new SerializedMessage(client.getID(), new PrintCardsAction());
                case "EXIT" -> exitGame();
                default -> System.out.print(CLI_INCOMPR_INPUT);
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
        modelView.getCli().setActiveGame(false);
    }


    /**
     * {@inheritDoc}
     * <p></p>
     * This method is called by the cli after a command written by the user (client).
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // Exit command. Shutdown of the client NOT based on game turns.
        if(evt.getNewValue().toString().equalsIgnoreCase("exit")){
            exitGame();
            return;
        }

        // Dispatch the command from the user.
        if (!modelView.getIsYourTurn()) {
            System.out.println("Unable to complete the action, wait for your turn!");
        } else {
            if (!dispatchAction(evt.getNewValue().toString(), evt.getPropertyName())) {
                modelView.setIsYourTurn(true);
            }
        }
    }
}
