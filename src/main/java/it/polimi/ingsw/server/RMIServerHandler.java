package it.polimi.ingsw.server;

import it.polimi.ingsw.client.IRMIClient;
import it.polimi.ingsw.server.connection.RMICSConnection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerHandler extends UnicastRemoteObject implements IRMIServer {
    private final Server server;
    private RMICSConnection connection;
    public RMIServerHandler(Server server) throws RemoteException {
        this.server = server;
    }

    /**
     * Login with
     * @param nickname username of the player
     * @param client client which want to connect with the server.
     */
    @Override
    public void login(String nickname, IRMIClient client) {
        connection = new RMICSConnection(server, client);
        server.login(nickname, connection);
    }

    /**
     * Cut off the communication client-server.
     */
    @Override
    public void logout() {
        connection.disconnect();
    }
}
