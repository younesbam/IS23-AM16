package it.polimi.ingsw.client.common;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.Dispatcher;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.rmi.RMIClientHandler;
import it.polimi.ingsw.client.socket.SocketClientHandler;
import it.polimi.ingsw.common.ConnectionType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;

/**
 * Manage the game. Listen for messages also.
 */
public abstract class UI implements PropertyChangeListener {
    /**
     * Generic client, RMI or socket.
     */
    protected Client client;

    /**
     * Client's action handler.
     */
    protected ActionHandler actionHandler;

    /**
     * Client's model view.
     */
    protected ModelView modelView;

    /**
     * Property change support.
     */
    protected PropertyChangeSupport pcsDispatcher;

    /**
     * Game active
     */
    private boolean activeGame;

    protected int tmp = 0;


    /**
     * Connect client to server, based on chosen communication protocol.
     * @param connectionType Type of connection (Socket or RMI)
     * @param address of the server
     * @param port of the server
     * @param username of the player
     * @param disconnectionListener
     * @throws Exception
     */
    public void connectToServer(ConnectionType connectionType, String address, int port, String username) throws RemoteException, NotBoundException {
        /*
        Connect to server based on type of connection.
         */
        if (connectionType == ConnectionType.SOCKET) {
            client = new SocketClientHandler(address, port, username, modelView, actionHandler);
        } else {
            client = new RMIClientHandler(address, port, username, modelView, actionHandler);
        }


        /*
        Add the property change listener. Create a new dispatcher of the user command, NOT based on the type of connection.
        WARNING: it's mandatory to start the listener before calling the connect method. That method triggers an action in the server that
        send a message to the client (number of max player, only for the first player). the client has to response, and then loop the input, waiting for new messages.
        See "lobby" method in server for more information
         */
        pcsDispatcher.addPropertyChangeListener(new Dispatcher(modelView, client));

        client.connect();
    }


    /**
     * Disconnect from server.
     */
    public void disconnectFromServer() {
        int status;
        try {
            client.disconnect();
            Client.LOGGER.log(Level.INFO, "Client successfully disconnected from the server");
            status = 0;
        } catch (RemoteException e) {
            Client.LOGGER.log(Level.WARNING, "Error while properly disconnect the server. Client will be shut down in any case");
            status = -1;
        }
        System.exit(status);
    }


    public synchronized boolean isActiveGame() {
        return activeGame;
    }

    public synchronized void setActiveGame(boolean activeGame) {
        this.activeGame = activeGame;
    }

}
