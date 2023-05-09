package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.communications.clientmessages.HowManyPlayersResponse;
import it.polimi.ingsw.communications.clientmessages.Message;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.serveranswers.HowManyPlayersRequest;
import it.polimi.ingsw.communications.serveranswers.PersonalizedAnswer;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.exceptions.OutOfBoundException;
import it.polimi.ingsw.server.GameHandler;
import it.polimi.ingsw.server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;

public class SocketCSConnection extends CSConnection implements Runnable{

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
        SerializedMessage input = (SerializedMessage) inputStream.readObject();
        onMessage(input);
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
     * Override of superclass method, used to setup the number of players in the initial phase of the game.
     * @param request
     */
    public void setupPlayers(HowManyPlayersRequest request) {

        SerializedAnswer answer = new SerializedAnswer();
        answer.setAnswer(request);
        sendAnswerToClient(answer);

        while(true) {
            try {
                SerializedMessage input = (SerializedMessage) inputStream.readObject();
                Message messageFromClient = input.message;
                if(messageFromClient instanceof HowManyPlayersResponse) {
                    try {
                        int numOfPlayers = ((HowManyPlayersResponse) messageFromClient).getNumChoice();
                        server.setNumOfPlayers(numOfPlayers);
                        server.getGameHandlerByID(ID).setNumOfPlayers(numOfPlayers);
                        server.getVirtualPlayerByID(ID).send(new PersonalizedAnswer(false, "The number of players for this match has been chosen: it's a " + numOfPlayers + " players match!"));
                        break;
                    } catch(OutOfBoundException e) {
                        server.getVirtualPlayerByID(ID).send(new PersonalizedAnswer(true, "A number of players between 2 and 4 is required!"));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                disconnect();
                System.err.println("Error occurred while setting-up the game mode: " + e.getMessage());
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
