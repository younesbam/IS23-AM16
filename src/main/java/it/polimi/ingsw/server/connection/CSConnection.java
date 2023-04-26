package it.polimi.ingsw.server.connection;

/**
 * Represent a generic connection client-server
 * @author Nicolo' Gandini
 */
public abstract class CSConnection {
    protected boolean alive = false;  // False initialization to ensure successful connection in the subclasses.

    /**
     * Check if the connection is alive.
     * @return connection status.
     */
    public boolean isAlive() {
        return alive;
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
}
