package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.connection.SocketCSConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Socket handler.
 */
public class ServerSideSocket implements Runnable{
    private final ExecutorService executorService;
    /**
     * Port of the server.
     */
    private final int numOfPort;
    /**
     * Server reference.
     */
    private final Server server;


    /**
     * Class constructor
     *
     * @param server server reference
     * @param numOfPort server port number (socket side).
     */
    public ServerSideSocket(Server server, int numOfPort) {
        this.server = server;
        this.numOfPort = numOfPort;
        executorService = Executors.newCachedThreadPool();
    }


    /**
     * Method that accepts client's connections to the server, while the server socket is active.
     * The accept() method remains blocked until a client tries to connect.
     * @param serverSocket
     */
    private void newConnection(ServerSocket serverSocket) {
        while (true) {
            try {
                SocketCSConnection clientSocket = new SocketCSConnection(server, serverSocket.accept());
                executorService.submit(clientSocket);
            } catch (IOException e) {
                Server.LOGGER.log(Level.SEVERE, "An error has occurred while trying to establish a connection. Shutting down...");
            }
        }
    }


    /**
     * Shutdown active threads.
     */
    public void shutdown(){
        executorService.shutdownNow();
    }


    /**
     * Runnable method that creates a ServerSocket instance and calls the connection method.
     */
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(numOfPort);
            newConnection(serverSocket);
        } catch(IOException e) {
            Server.LOGGER.log(Level.SEVERE, "An error has occurred while trying to establish a connection. Shutting down...");
            System.exit(0);
        }
    }
}
