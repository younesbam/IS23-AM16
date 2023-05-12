package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.communications.clientmessages.Message;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.UsernameSetup;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.actions.TilesPicked;
import it.polimi.ingsw.communications.serveranswers.HowManyPlayersRequest;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.Server;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Represent a generic connection client-server
 * @author Nicolo' Gandini
 */
public abstract class CSConnection {
    /**
     * Boolean used by the server to know if the client is alive (connected) or not.
     */
    protected boolean alive = false;  // False initialization to ensure successful connection in the subclasses.

    protected Server server;
    private Integer ID;

    /**
     * ID getter.
     * @return
     */
    public Integer getID() {
        return this.ID;
    }


    /**
     * Dispatch the message to the right action handler, based on the type of the serialized message.
     * @param serializedMessage
     */
    public void onMessage(SerializedMessage serializedMessage) {
        if (serializedMessage.message != null) {
            actionHandler(serializedMessage.message);
        } else if (serializedMessage.gameAction != null) {
            actionHandler(serializedMessage.gameAction);
        }
    }


    /**
     * Handles the possible messages that can arrive from client's side.
     * @param message message from client
     */
    private void actionHandler(Message message){
        if (message instanceof UsernameSetup) {
            checkConnection((UsernameSetup) message);
        }

    }


    /**
     * Handles the possible game action that can arrive from client's side.
     * @param action game action from client
     */
    private void actionHandler(GameAction action){
        if(action instanceof TilesPicked){
            server.getGameHandlerByID(ID).dispatchActions(action);
        }
    }


    /**
     * This method is used to check if the player trying to connect to the server
     * @param usernameChoice
     */
    private void checkConnection(UsernameSetup usernameChoice){
        try {
            ID = server.newClientRegistration(usernameChoice.getUsername(), this);
            if (ID == null) {
                disconnect();
                return;
            }
            server.lobby(this);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Check if the connection is alive.
     * @return connection status.
     */
    public boolean isAlive() {
        return alive;
    }


    /**
     * Send a ping message to clients, to know if they are still connected. Otherwise, disconnect the client.
     * @see #disconnect()
     */
    public abstract void ping();


    /**
     * Disconnect the client if it doesn't respond to the ping signal from the server.
     * @see #ping()
     */
    public abstract void disconnect();


    /**
     * Send an answer to the client.
     * @param answer from the server
     */
    public abstract void sendAnswerToClient(SerializedAnswer answer) throws IOException;


    /**
     * This method is used to set up the players.
     * @param request
     */
    public abstract void setupPlayers(HowManyPlayersRequest request);
}
