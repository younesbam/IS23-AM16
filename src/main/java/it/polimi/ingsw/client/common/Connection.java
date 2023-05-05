package it.polimi.ingsw.client.common;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.logging.Logger;

public abstract class Connection extends UnicastRemoteObject {
    /**
     * Logger of the client.
     */
    public static final Logger LOGGER = Logger.getLogger(Connection.class.getName());

    /**
     * Address of the client.
     */
    private final String address;

    /**
     * Port of the client.
     */
    private final int port;

    /**
     * Username of the current player.
     */
    protected final String username;

    /**
     * Ping timer. Used to start a timer
     */
    protected Timer pingTimer;

    /**
     * Client's action handler.
     */
    protected ActionHandler actionHandler;

    /**
     * Client's model view.
     */
    protected ModelView modelView;


    /**
     * Constructor.
     * @param address of the client
     * @param port of the client
     * @param username of the user
     * @throws RemoteException
     */
    public Connection(String address, int port, String username, ModelView modelView, ActionHandler actionHandler) throws RemoteException {
        this.address = address;
        this.port = port;
        this.username = username;
        this.modelView = modelView;
        this.actionHandler = actionHandler;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Connect to the server
     * @throws Exception
     */
    public abstract void connect() throws Exception;

    /**
     * Disconnect from the server.
     * @throws RemoteException
     */
    public abstract void disconnect() throws RemoteException;

}
