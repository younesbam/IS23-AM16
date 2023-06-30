package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.client.rmi.IRMIClient;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface used to communicate with the server from the client. This is what the server shows to the clients.
 * <p>
 * All these methods are used by the client.
 */
public interface IRMIServer extends Remote {
    /**
     * Connect the client to the server.
     * @param username name of the player.
     * @param client client reference
     * @throws RemoteException error during communication.
     */
    void login(String username, IRMIClient client) throws RemoteException;


    /**
     * Disconnect the client from the server.
     * @throws RemoteException error during communication.
     */
    void logout(IRMIClient client) throws RemoteException;


    /**
     * Send message to the server.
     * @param message to send to the server.
     * @throws RemoteException error during communication.
     */
    void sendMessageToServer(IRMIClient client, SerializedMessage message) throws RemoteException, InterruptedException;
}
