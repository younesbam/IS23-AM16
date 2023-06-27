package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.messages.CreateGameMessage;
import it.polimi.ingsw.communications.clientmessages.messages.JoinGameMessage;
import it.polimi.ingsw.communications.clientmessages.messages.UsernameSetup;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.network.PingRequest;
import it.polimi.ingsw.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;

/**
 * Represent Socket connection. Here all the methods useful for Socket technology.
 */
public class SocketCSConnection extends CSConnection implements Runnable{
    /**
     * Input stream.
     */
    private ObjectInputStream inputStream;
    /**
     * Output stream.
     */
    private ObjectOutputStream outputStream;
    /**
     * Socket reference.
     */
    private final Socket socket;


    /**
     * Class constructor.
     * @param server server reference.
     * @param socket socket class reference.
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
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }


    /**
     * This method reads and reacts to client's messages, also deserializing them.
     * @throws IOException error during stream reading.
     * @throws ClassNotFoundException error during stream reading.
     */
    public synchronized void readStreamFromClient() throws IOException, ClassNotFoundException, InterruptedException {
        SerializedMessage input = (SerializedMessage) inputStream.readObject();
        if(input.message instanceof UsernameSetup){
            server.tryToConnect(((UsernameSetup) input.message).getUsername(), this);
        } else if(input.message instanceof CreateGameMessage){
            server.createNewMatch(this, ((CreateGameMessage) input.message).getMatchName());
        }
        else if(input.message instanceof JoinGameMessage){
            server.joinMatch(((JoinGameMessage) input.message).getMatchName(), this);
        }else {
            server.onClientMessage(input);
        }
    }


    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public void sendAnswerToClient(SerializedAnswer answer) {
        if(this.alive){
            try {
                outputStream.reset();
                outputStream.writeObject(answer);
                outputStream.flush();
            } catch (IOException e) {
                this.alive = false;
                Server.LOGGER.log(Level.WARNING, "Failed to send message to the client " + ID);
                server.suspendClient(this);
                //disconnect();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() {
        SerializedAnswer answer = new SerializedAnswer();
        answer.setAnswer(new PingRequest());
        sendAnswerToClient(answer);
    }


    /**
     * This method constantly calls the readStreamFromClient() method to read client's messages.
     */
    public void run() {
        try {
            while (alive)
                readStreamFromClient();
        } catch(IOException | ClassNotFoundException | InterruptedException e) {
            Server.LOGGER.log(Level.WARNING, "Failed to read the stream from client " + ID);
        }
    }
}
