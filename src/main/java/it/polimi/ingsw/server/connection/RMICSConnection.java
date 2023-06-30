package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.rmi.IRMIClient;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.Server;

import java.rmi.RemoteException;
import java.util.logging.Level;

/**
 * Represent RMI connection. Here all the methods useful for RMI technology.
 */
public class RMICSConnection extends CSConnection {
    /**
     * Client reference.
     */
    private final IRMIClient client;

    /**
     * Constructor.
     * @param server server reference.
     * @param client client reference.
     */
    public RMICSConnection(Server server, IRMIClient client) {
        this.server = server;
        this.client = client;
        this.alive = true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void ping(){
        if(this.alive){
            try{
                client.ping();
            }catch (RemoteException e){
                this.alive = false;
                server.suspendClient(this);
            }
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
                Server.LOGGER.log(Level.INFO, "Client " + ID + " successfully disconnected");
            }catch (RemoteException e){
                Server.LOGGER.log(Level.WARNING, "Failed to disconnect the client " + ID);
            }
        }
        if(getID() != null){
            server.removePlayer(getID());
            return;
        }
        Server.LOGGER.log(Level.WARNING, "Client " + ID + " never registered in the server");
    }


    /**
     * {@inheritDoc}
     */
    public void sendAnswerToClient(SerializedAnswer answer) {
        if(this.alive){
            try{
                client.onServerAnswer(answer);
            } catch (RemoteException e) {
                Server.LOGGER.log(Level.WARNING, "Failed to send message to the client " + ID);
            }
        }
    }

    /**
     * Get client reference.
     * @return client reference interface.
     */
    public IRMIClient getClient() {
        return client;
    }
}
