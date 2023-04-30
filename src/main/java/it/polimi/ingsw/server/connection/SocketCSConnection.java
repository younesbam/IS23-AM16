package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.Communication;
import it.polimi.ingsw.communications.clientmessages.SerializedCommunication;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.GameHandler;
import it.polimi.ingsw.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;

public class SocketCSConnection extends CSConnection {

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final Socket socket;
    private Integer ID;


    /**
     * Class constructor.
     * @param server
     * @param socket
     */
    public SocketCSConnection(Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
        ID = -1;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            alive = true;
        } catch (IOException e) {
            System.err.println("An error has occurred while initializing the client:" + e.getMessage());
        }
    }


    /**
     * Socket getter.
     * @return
     */
    public Socket getSocket() {
        return socket;
    }


    /**
     * This method reads and reacts to client's messages, also deserializing them.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public synchronized void readStreamFromClient() throws IOException, ClassNotFoundException {
        SerializedCommunication input = (SerializedCommunication) inputStream.readObject();
        if (input.communication != null) {
            Communication command = input.communication;
            server.actionHandler(command);
        } else if (input.action != null) {
            GameAction action = input.gameAction;
            server.actionHandler(action);
        }
    }


    /**
     * This method ends the player's connection.
     */
    public void disconnect() {
        server.removePlayer(ID);
        try {
            socket.close();
        } catch (IOException e) {
            Server.LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }


    /**
     * Method that sends a message to the server.
     * @param answer to the client
     */
    public void sendAnswerToClient(SerializedAnswer answer) {
        if(alive){
            try {
                outputStream.reset();
                outputStream.writeObject(answer);
                outputStream.flush();
            } catch (IOException e) {
                Server.LOGGER.log(Level.SEVERE, "Failed to send message to the client: " + e.getMessage());
                disconnect();
            }
        }
    }


    /**
     * This method constantly calls the readStreamFromClient() method to read client's messages.
     */
    public void run() {
        try {
            while (isAlive()) {
                readStreamFromClient();
            }
        } catch(IOException e) {
            GameHandler game = server.getGameHandlerByID(ID);
            String player = server.getVirtualPlayerByID(ID);
            server.removePlayer(ID);
            if (game.isAlreadyStarted()) {
                game.endPlayerGame(player);
            }
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        SerializedAnswer serverOut = new SerializedAnswer();
        serverOut.setServerAnswer(new ServerError(ServerErrorTypes.SERVEROUT));
        sendServerMessage(serverOut);
    }

}
