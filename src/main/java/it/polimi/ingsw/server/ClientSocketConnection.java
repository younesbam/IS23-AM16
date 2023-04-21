package it.polimi.ingsw.server;

import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.Communication;
import it.polimi.ingsw.communications.clientmessages.SerializedCommunication;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketConnection {

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final Socket socket;
    private final Server server;
    private Integer ID;
    private boolean isActive;


    /**
     * Class constructor.
     * @param server
     * @param socket
     */
    public ClientSocketConnection(Server server, Socket socket) {
        isActive = true;

        this.socket = socket;
        this.server = server;
        ID = -1;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
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
            actionHandler(command);
        } else if (input.action != null) {
            GameAction action = input.gameAction;
            actionHandler(action);
        }
    }

    /**
     * This method ends the player's cnnection.
     */
    public void end() {
        server.removePlayer(ID);
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * Method that sends a message to the server.
     * @param answer
     */
    public void sendMessage(SerializedAnswer answer) {
        try {
            outputStream.reset();
            outputStream.writeObject(answer);
            outputStream.flush();
        } catch (IOException e) {
            end();
        }
    }


    /**
     * This method constantly calls the readStreamFromClient() method to read client's messages.
     */
    public void run() {
        try {
            while (isActive) {
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
