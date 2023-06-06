package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.messages.UsernameSetup;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.server.GameHandler;
import it.polimi.ingsw.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.IDN;
import java.net.Socket;
import java.util.logging.Level;

public class SocketCSConnection extends CSConnection implements Runnable{

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final Socket socket;


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
        SerializedMessage input = (SerializedMessage) inputStream.readObject();
        if(input.message instanceof UsernameSetup){
            server.tryToConnect(((UsernameSetup) input.message).getUsername(), this);
        } else {
            server.onClientMessage(input);
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
                Server.LOGGER.log(Level.SEVERE, "Failed to send message to the client: ", e);
                disconnect();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() {
        System.out.println("PING DA IMPLEMENTARE LATO SOCKET!!!!");
    }


    /**
     * This method constantly calls the readStreamFromClient() method to read client's messages.
     */
    public void run() {
        try {
            while (alive) {
                readStreamFromClient();
            }
        } catch(IOException e) {
            //TODO: A cosa servono questi metodi qui? Il server deve fare queste cose non le singole connessioni
//            GameHandler game = server.getGameHandlerByID(ID);
//            String username = server.getUsernameByID(ID);
//            server.removePlayerFromWaitingList(ID);
//            if (game.isAlreadyStarted()) {
//                game.endMatch(username);
//            }
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
//        SerializedAnswer serverDown = new SerializedAnswer();
//        serverDown.setAnswer(new ErrorAnswer(ErrorClassification.SERVERISDOWN));
//        sendAnswerToClient(serverDown);
    }

}
