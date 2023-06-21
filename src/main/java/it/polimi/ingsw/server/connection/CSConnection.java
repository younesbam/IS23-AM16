package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.Server;


/**
 * Represent a generic client-server connection used by the server to manage the clients.
 */
public abstract class CSConnection {
    /**
     * Boolean used by the server to know if the client is alive (connected) or not.
     */
    protected boolean alive = false;  // False initialization to ensure successful connection in the subclasses.

    /**
     * Server reference.
     */
    protected Server server;

    /**
     * Unique ID that represent the connection client-server.
     * <p></p>
     * Note: The associated client must have the same ID.
     */
    protected Integer ID;


    /**
     * Check if the connection is alive.
     * @return connection status.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Set unique client ID
     * @param ID
     */
    public void setID(Integer ID){
        this.ID = ID;
    }

    /**
     * Client ID getter.
     * @return ID
     */
    public Integer getID() {
        return this.ID;
    }


    /**
     * Send a ping message to clients, to know if they are still connected. Otherwise, puts the client in standby, waiting for reconnection
     */
    public abstract void ping();


    /**
     * Disconnect the client.
     */
    public abstract void disconnect();


    /**
     * Send an answer to the client.
     * @param answer answer to the client.
     */
    public abstract void sendAnswerToClient(SerializedAnswer answer);

}
