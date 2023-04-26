package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.IRMIClient;
import it.polimi.ingsw.server.Server;

import java.rmi.RemoteException;
import java.util.logging.Level;

/**
 * Represent RMI connection to a client
 * @author Nicolo' Gandini
 */
public class RMICSConnection extends CSConnection {
    private final Server server;
    private final IRMIClient client;

    public RMICSConnection(Server server, IRMIClient client) {
        this.server = server;
        this.client = client;
        this.alive = true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() {
        try {
            client.ping();
        }catch (RemoteException e){
            Server.LOGGER.log(Level.WARNING, "Disconnect directive. No response", e);
            disconnect();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        if(this.alive){
            try {
                client.disconnectMe();
                this.alive = false;
                Server.LOGGER.log(Level.INFO, "Client successfully disconnected");
            }catch (RemoteException e){
                Server.LOGGER.log(Level.WARNING, "Failed to disconnect the client", e);
            }
            server.onClientDisconnection(this);
        }
    }
}
