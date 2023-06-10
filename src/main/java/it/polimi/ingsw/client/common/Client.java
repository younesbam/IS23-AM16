package it.polimi.ingsw.client.common;

import it.polimi.ingsw.Const;
import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.utils.PingClientTask;
import it.polimi.ingsw.communications.clientmessages.messages.Message;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.logging.Logger;

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
     * Ping timer. Used to start a timer
     */
    protected Timer pingTimer = new Timer();

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
    public Client(String address, int port, String username, ModelView modelView, ActionHandler actionHandler) throws RemoteException {
        this.address = address;
        this.port = port;
        this.username = username;
        this.ID = null;
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

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * Handle ping request by cancelling a timer that shut the client down after n seconds.
     */
    protected void handlePingRequest(){
        pingTimer.cancel();
        pingTimer = new Timer();
        pingTimer.schedule(new PingClientTask(), Const.CLIENT_DISCONNECTION_TIME*1000);
    }

    /**
     * Connect to the server
     * @throws Exception
     */
    public abstract void connect() throws RemoteException, NotBoundException;

    /**
     * Disconnect from the server.
     * @throws RemoteException
     */
    public abstract void disconnect() throws RemoteException;

    public abstract void sendToServer(Message message);

    public abstract void sendToServer(GameAction gameAction);

}
