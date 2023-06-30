package it.polimi.ingsw.client.rmi;

import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface used to communicate with the client from the server. This is what the client shows to the server.
 * <p>
 * All these methods are used by the server.
 */
public interface IRMIClient extends Remote {
    /**
     * Start a ping task to check disconnections.
     * @throws RemoteException thrown if the communication is not established.
     */
    void ping() throws RemoteException;


    /**
     * Disconnect the current client from the server.
     * @throws RemoteException thrown if the communication is not established.
     */
    void disconnectMe() throws RemoteException;


    /**
     * Handle the answer from the server.
     * @throws RemoteException thrown if the communication is not established.
     */
    void onServerAnswer(SerializedAnswer answer) throws RemoteException;
}
