package it.polimi.ingsw.server.utils;

import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.connection.CSConnection;

import java.util.Map;
import java.util.logging.Level;

/**
 * Thread to check if every client is still alive.
 */
public class ServerPing implements Runnable {
    /**
     * Timeout value get from json
     */
    private final int timeout = 1000;
    private Map<String, CSConnection> clients;

    public ServerPing(Map<String, CSConnection> clients) {
        this.clients = clients;
    }

    /**
     * Iterate each client and check if they are still alive.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (clientsLock) {
                /*
                Scorro tutti i client per vedere se sono connessi.
                 */
                for (Map.Entry<String, CSConnection> client : clients.entrySet()) {
                    if (client.getValue().isAlive()) {
                        client.getValue().ping();
                    }
                }
            }

            try {
                Thread.sleep(this.timeout);
            } catch (InterruptedException e) {
                Server.LOGGER.log(Level.SEVERE, "Ping thread error" + e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
