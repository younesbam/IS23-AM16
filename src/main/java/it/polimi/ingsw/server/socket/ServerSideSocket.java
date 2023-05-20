package it.polimi.ingsw.server.socket;

import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.connection.SocketCSConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSideSocket implements Runnable{

    private final ExecutorService executorService;
    private final int numOfPort;
    private final Server server;
    private boolean isActive;


    /**
     * Class constructor
     *
     * @param server
     * @param numOfPort
     */
    public ServerSideSocket(Server server, int numOfPort) {
        isActive = true;
        this.server = server;
        this.numOfPort = numOfPort;
        executorService = Executors.newCachedThreadPool();
    }


    /**
     * Method that accepts client's connections to the server, while the server socket is active.
     * The accept() method remains blocked until a client tries to connect.
     * @param serverSocket
     */
    public void newConnection(ServerSocket serverSocket) {
        while (isActive) {
            try {
                SocketCSConnection clientSocket = new SocketCSConnection(server, serverSocket.accept());
                executorService.submit(clientSocket);
            } catch (IOException e) {
                System.err.println("An error has occurred while trying to establish a connection. Shutting down..." + e.getMessage());
            }
        }
    }


    /**
     * This method sets the server side socket connection as active/inactive.
     * @param bool
     */
    public void setIsActive(boolean bool) {
        this.isActive = bool;
    }


    /**
     * IsActive getter.
     * @return
     */
    public boolean getIsActive(){
        return this.isActive;
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
            System.err.println("An error has occurred while trying to establish a connection. Shutting down...");
            System.exit(0);
        }
    }
}
