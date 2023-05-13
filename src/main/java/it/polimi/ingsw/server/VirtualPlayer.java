package it.polimi.ingsw.server;

import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.SocketCSConnection;

import java.io.IOException;
import java.util.logging.Level;

public class VirtualPlayer {

    public final String username;
    public final int ID;
    public final GameHandler gameHandler;
    public final CSConnection connection;


    /**
     * Class constructor.
     *
     * @param username
     * @param ID
     * @param c
     * @param g
     */
    public VirtualPlayer(String username, Integer ID, CSConnection c, GameHandler g) {
        this.username = username;
        this.ID = ID;
        this.connection = c;
        this.gameHandler = g;
    }

    /**
     * ID getter.
     *
     * @return
     */
    public int getID() {
        return this.ID;
    }

    /**
     * Username getter.
     *
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * ClientSocketConnection getter.
     *
     * @return
     */
    public CSConnection getConnection() {
        return this.connection;
    }

    /**
     * GameHandler getter.
     *
     * @return
     */
    public GameHandler getGameHandler() {
        return this.gameHandler;
    }

    /**
     * This method is used to send the server's answer to the proper client.
     * @param a
     */
    public void send(Answer a) {
        SerializedAnswer answer = new SerializedAnswer();
        answer.setAnswer(a);
        try{
            connection.sendAnswerToClient(answer);
        } catch (IOException e){
            Server.LOGGER.log(Level.SEVERE, "Failed to send the answer to the client" + username, e);
        }
    }
}