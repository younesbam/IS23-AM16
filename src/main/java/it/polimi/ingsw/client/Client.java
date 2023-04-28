package it.polimi.ingsw.client;

import it.polimi.ingsw.server.Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.logging.Logger;

public abstract class Client extends UnicastRemoteObject {
    /**
     * Logger of the client.
     */
    public static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private final String address;
    private final int port;
    private final String nickname;

    /**
     * Ping timer. Used to start a timer
     */
    protected Timer pingTimer;

    public Client(String address, int port, String nickname) throws RemoteException {
        this.address = address;
        this.port = port;
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getNickname() {
        return nickname;
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
