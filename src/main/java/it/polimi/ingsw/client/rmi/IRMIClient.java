package it.polimi.ingsw.client.rmi;

import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface used to communicate with the client from the server. This is what the client shows to the server.
 * <p>
 * All these methods are used by the server.
 * @author Nicolo' Gandini
 */
public interface IRMIClient extends Remote {
    /**
     * Start a ping task to check disconnections.
     * @throws RemoteException
     */
    void ping() throws RemoteException;


    /**
     * Disconnect the current client from the server.
     * @throws RemoteException
     */
    void disconnectMe() throws RemoteException;


    /**
     * Client has received an answer from the server.
     * @throws RemoteException
     */
    void onServerAnswer(SerializedAnswer answer) throws RemoteException;
}