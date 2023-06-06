package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.Server;

import java.rmi.RemoteException;

/**
 * Represent a generic connection client-server
 * @author Nicolo' Gandini
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
     * ID setter
     * @param ID
     */
    public void setID(Integer ID){
        this.ID = ID;
    }

    /**
     * ID getter.
     * @return
     */
    public Integer getID() {
        return this.ID;
    }


    /**
     * Send a ping message to clients, to know if they are still connected. Otherwise, disconnect the client.
     * @see #disconnect()
     */
    public abstract void ping();


    /**
     * Disconnect the client if it doesn't respond to the ping signal from the server.
     * @see #ping()
     */
    public abstract void disconnect();


    /**
     * Send an answer to the client.
     * @param answer from the server
     */
    public abstract void sendAnswerToClient(SerializedAnswer answer);

}
