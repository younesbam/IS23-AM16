package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.client.rmi.IRMIClient;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.RMICSConnection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerHandler extends UnicastRemoteObject implements IRMIServer {
    private final Server server;
    private CSConnection connection;
    public RMIServerHandler(Server server) throws RemoteException {
        this.server = server;
    }

    /**
     * Login with
     * @param username username of the player
     * @param client client which want to connect with the server.
     */
    @Override
    public void login(String username, IRMIClient client) {
        connection = new RMICSConnection(server, client);
        server.newClientRegistration(username, connection);
    }

    /**
     * Cut off the communication client-server.
     */
    @Override
    public void logout() {
        connection.disconnect();
    }
}
