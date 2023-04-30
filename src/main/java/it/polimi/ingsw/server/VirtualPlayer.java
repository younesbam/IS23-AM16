package it.polimi.ingsw.server;

import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.SocketCSConnection;

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
    public VirtualPlayer(String username, int ID, SocketCSConnection c, GameHandler g) {
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


    public void send(Answer a) {
        SerializedAnswer answer = new SerializedAnswer();
        answer.setAnswer(a);
        connection.s;
    }
}