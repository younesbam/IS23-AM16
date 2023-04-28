package it.polimi.ingsw.client;

import it.polimi.ingsw.client.listeners.ClientDisconnectionListener;
import it.polimi.ingsw.client.rmi.RMIClientHandler;
import it.polimi.ingsw.client.socket.SocketClientHandler;
import it.polimi.ingsw.common.ConnectionType;

import java.rmi.RemoteException;

/**
 * Manage the game. Listen for messages also.
 */
public abstract class ClientManager {
    /**
     * Generic client, RMI or socket.
     */
    Client client;

    /**
     * Connect client to server, based on chosen communication protocol.
     * @param connection
     * @param username
     * @param address
     * @param port
     * @param disconnectionListener
     * @throws Exception
     */
    public void connectToServer(ConnectionType connection, String username, String address, int port, ClientDisconnectionListener disconnectionListener) throws Exception {
        if (connection == ConnectionType.SOCKET) {
            client = new SocketClientHandler(username, address, port, disconnectionListener);
        } else {
            client = new RMIClientHandler(username, address, port, disconnectionListener);
        }

        client.connect();

        /*
        Qui bisogna far partire il listener del client per notificare dell'arrivo di messaggi.
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
