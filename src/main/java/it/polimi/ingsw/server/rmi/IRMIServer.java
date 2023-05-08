package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.client.rmi.IRMIClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface used to communicate with the server from the client. This is what the server shows to the clients.
 * <p>
 * All these methods are used by the client.
 * @author Nicolo' Gandini
 */
public interface IRMIServer extends Remote {
    /**
     * Connect the client to the server.
     * @param nickname name of the player.
     * @throws RemoteException
     */
    public void login(String username, IRMIClient client) throws RemoteException;

    /**
     * Disconnect the client from the server.
     * @throws RemoteException
     */
    public void logout() throws RemoteException;
}
