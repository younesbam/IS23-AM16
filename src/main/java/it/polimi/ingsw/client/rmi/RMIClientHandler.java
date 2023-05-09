package it.polimi.ingsw.client.rmi;

import it.polimi.ingsw.Const;
import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.client.utils.PingClientTask;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.server.IRMIServer;
import it.polimi.ingsw.server.rmi.IRMIServer;

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
     * @param username username of the player
     * @throws RemoteException
     */
    public RMIClientHandler(String address, int port, String username, ModelView modelView, ActionHandler actionHandler) throws RemoteException {
        super(address, port, username, modelView, actionHandler);
        jsonParser = new JSONParser("json/network.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(getAddress(), getPort());
        server = (IRMIServer) registry.lookup(jsonParser.getServerName());
        server.login(getUsername(), this);
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
        super.pingTimer.schedule(new PingClientTask(), Const.CLIENT_PING_DELAY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectMe() throws RemoteException {
        server = null;
    }
}
