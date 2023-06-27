package it.polimi.ingsw.client.common;

import it.polimi.ingsw.Const;
import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.utils.ConnectionTimeoutTask;
import it.polimi.ingsw.client.utils.PingTimeoutTask;
import it.polimi.ingsw.communications.clientmessages.messages.Message;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.logging.Logger;

/**
 * Superclass representing a generic client. Extended by Socket or RMI handler.
 * @see it.polimi.ingsw.client.rmi.RMIClientHandler rmiHandler
 * @see it.polimi.ingsw.client.socket.SocketClientHandler socketHandler
 */
public abstract class Client extends UnicastRemoteObject {
    /**
     * Logger of the client.
     */
    public static final Logger LOGGER = Logger.getLogger(Client.class.getName());

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
     * Unique ID of the client. It is assigned by the server when the connection is done.
     */
    protected Integer ID;

    /**
     * Ping timer. It closes the client if it doesn't receive the ping command from the server.
     */
    protected Timer pingTimer = new Timer();

    /**
     * Connection timer. It closes the client if can't connect to the server.
     */
    protected Timer connectionTimer = new Timer();

    /**
     * Client's action handler.
     */
    protected ActionHandler actionHandler;

    /**
     * Client's model view.
     */
    protected ModelView modelView;


    /**
     * Class constructor.
     * @param address of the client
     * @param port of the client
     * @param username of the user
     * @throws RemoteException
     */
    public Client(String address, int port, String username, ModelView modelView, ActionHandler actionHandler) throws RemoteException {
        this.address = address;
        this.port = port;
        this.username = username;
        this.ID = null;
        this.modelView = modelView;
        this.actionHandler = actionHandler;
    }

    /**
     * Get client's address.
     * @return client's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get client's port.
     * @return client's port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get client's username.
     * @return client's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get client's unique ID generated by the server.
     * @return client's ID.
     */
    public Integer getID() {
        return ID;
    }

    /**
     * Set client's unique ID.
     * @param ID
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * Activate ping request by cancelling a timer that shut the client down after {@link Const#CLIENT_DISCONNECTION_TIME x} seconds.
     */
    protected synchronized void activatePingTimeout(){
        pingTimer.cancel();
        pingTimer = new Timer();
        pingTimer.schedule(new PingTimeoutTask(), Const.CLIENT_DISCONNECTION_TIME*1000);
    }

    /**
     * Activate ping request by cancelling a timer that shut the client down after {@link Const#CLIENT_DISCONNECTION_TIME x} seconds.
     */
    protected synchronized void deactivatePingTimeout(){
        pingTimer.cancel();
    }

    /**
     * Activate connection timeout request by cancelling a timer that shut the client down after {@link Const#CLIENT_DISCONNECTION_TIME x} seconds.
     * @see Client#deactivateConnectionTimeout()
     */
    protected synchronized void activateConnectionTimeout(){
        connectionTimer.cancel();
        connectionTimer = new Timer();
        connectionTimer.schedule(new ConnectionTimeoutTask(), Const.CLIENT_DISCONNECTION_TIME*1000);
    }

    /**
     * Permanently deactivate connection timeout task.
     * @see Client#activateConnectionTimeout()
     */
    protected synchronized void deactivateConnectionTimeout(){
        connectionTimer.cancel();
    }

    /**
     * Connect to the server.
     * @throws RemoteException exception thrown by RMI methods.
     * @throws NotBoundException thrown by {@link java.rmi.registry.Registry#lookup(String) lookup} method.
     */
    public abstract void connect() throws RemoteException, NotBoundException;

    /**
     * Disconnect from the server.
     * @throws RemoteException thrown by RMI methods.
     */
    public abstract void disconnect() throws RemoteException;

    /**
     * Send message to the server.
     * @param message message to be sent.
     */
    public abstract void sendToServer(Message message) throws RemoteException;

    /**
     * Send a game action to the server.
     * @param gameAction game action to be sent.
     */
    public abstract void sendToServer(GameAction gameAction) throws RemoteException;

}
