package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.communications.clientmessages.Communication;
import it.polimi.ingsw.communications.clientmessages.SerializedCommunication;
import it.polimi.ingsw.communications.clientmessages.UsernameSetup;
import it.polimi.ingsw.communications.serveranswers.Answer;
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

//    /**
//     * This method handles the possible messages that can arrive from client's side.
//     * @param message
//     */
//    public void actionHandler(Communication message) {
//        if (message instanceof UsernameSetup) {
//            checkConnection((UsernameSetup) message);
//
//        }
//    }

//    /**
//     * This method is used to check if the player trying to connect to the server
//     * @param usernameChoice
//     */
//    public void checkConnection(UsernameSetup usernameChoice) {
//        try {
//            ID = server.newClientRegistration(usernameChoice.getUsername(), this);
//            if (ID == null) {
//                alive = false;
//                return;
//            }
//            server.lobby(this);
//        } catch (InterruptedException e) {
//            System.err.println(e.getMessage());
//            Thread.currentThread().interrupt();
//        }
//    }

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
    public abstract void sendAnswerToClient(SerializedAnswer answer) throws RemoteException;


//    /**
//     * This method is used to set up the players.
//     * @param request
//     */
//    public abstract void setupPlayers(HowManyPlayersRequest request);
}
