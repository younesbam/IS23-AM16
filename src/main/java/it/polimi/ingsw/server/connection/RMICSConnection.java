package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.rmi.IRMIClient;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.Server;

import java.rmi.RemoteException;
import java.util.logging.Level;

/**
 * Represent RMI connection to a client
 * @author Nicolo' Gandini
 */
public class RMICSConnection extends CSConnection {
    /**
     * Client reference.
     */
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
            Server.LOGGER.log(Level.WARNING, "Disconnect directive. No response from client", e);
            disconnect();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        if(this.alive){
            this.alive = false;
            try {
                client.disconnectMe();
                Server.LOGGER.log(Level.INFO, "Client successfully disconnected");
            }catch (RemoteException e){
                Server.LOGGER.log(Level.WARNING, "Failed to disconnect the client", e);
            }

            if(getID() != null){
                server.removePlayer(getID());
                return;
            }
            Server.LOGGER.log(Level.WARNING, "Client never registered in the server");
        }
    }


    /**
     * {@inheritDoc}
     */
    public void sendAnswerToClient(SerializedAnswer answer) {
        try{
            client.onServerAnswer(answer);
        } catch (RemoteException e) {
            Server.LOGGER.log(Level.SEVERE, "Failed to send message to the client: ", e);
            disconnect();
        }
    }
}
