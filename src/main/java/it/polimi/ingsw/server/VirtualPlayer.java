package it.polimi.ingsw.server;

import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.connection.CSConnection;

public class VirtualPlayer {

    private final String username;
    private final GameHandler gameHandler;
    private CSConnection connection;


    /**
     * Class constructor.
     *
     * @param username
     * @param c connection
     * @param g game handler
     */
    public VirtualPlayer(String username, CSConnection c, GameHandler g) {
        this.username = username;
        this.connection = c;
        this.gameHandler = g;
    }

    public void restorePlayer(CSConnection connection){
        this.connection = connection;
    }

    /**
     * ID getter.
     *
     * @return
     */
    public int getID() {
        return connection.getID();
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
        connection.sendAnswerToClient(answer);
    }
}