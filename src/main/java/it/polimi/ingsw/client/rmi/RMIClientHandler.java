package it.polimi.ingsw.client.rmi;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.utils.PingClientTask;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.server.IRMIServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;

/**
 * Client RMI handler.
 * @author Nicolo' Gandini
 */
public class RMIClientHandler extends Client implements IRMIClient {
    private IRMIServer server;

    /**
     * Parameters from JSON file.
     */
    private JSONParser jsonParser;

    /**
     *
     * @param address address of the server.
     * @param port port of the server.
     * @param nickname nickname of the player
     * @throws RemoteException
     */
    public RMIClientHandler(String address, int port, String nickname) throws RemoteException {
        super(address, port, nickname);
        jsonParser = new JSONParser("json/network.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(getAddress(), getPort());
        server = (IRMIServer) registry.lookup(jsonParser.getServerName());
        server.login(getNickname(), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() throws RemoteException {
        server.logout();
        disconnectMe();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() throws RemoteException {
        super.pingTimer.cancel();
        super.pingTimer = new Timer();
        super.pingTimer.schedule(new PingClientTask(), Utils.CLIENT_PING_DELAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectMe() throws RemoteException {
        server = null;
    }
}
