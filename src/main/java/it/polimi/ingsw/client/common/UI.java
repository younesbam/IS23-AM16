package it.polimi.ingsw.client.common;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.Dispatcher;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.rmi.RMIClientHandler;
import it.polimi.ingsw.client.socket.SocketClientHandler;
import it.polimi.ingsw.common.ConnectionType;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.rmi.RemoteException;

/**
 * Manage the game. Listen for messages also.
 */
public abstract class UI implements PropertyChangeListener {
    /**
     * Generic client, RMI or socket.
     */
    Client client;

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
    protected PropertyChangeSupport pcs;
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
    public void connectToServer(ConnectionType connectionType, String address, int port, String username) throws Exception {
        /*
        Connect to server based on tpye of connection.
         */
        if (connectionType == ConnectionType.SOCKET) {
            client = new SocketClientHandler(address, port, username, modelView, actionHandler);
        } else {
            client = new RMIClientHandler(address, port, username, modelView, actionHandler);
        }
        client.connect();

        /*
        Add the property change listener. Create a new dispatcher of the user command, NOT based on the type of connection.
         */
        pcs.addPropertyChangeListener(new Dispatcher(modelView, client));

        /*
        Qui bisogna far partire il listener del client per notificare dell'arrivo di messaggi.
        Forse viene fatto già dal metodo sopra.
         */
    }


    /**
     * Disconnect from server.
     */
    public void disconnectFromServer() {
        try{
            client.disconnect();
        }catch (RemoteException e){
            // Fai qualcosa
        }

    }
}
