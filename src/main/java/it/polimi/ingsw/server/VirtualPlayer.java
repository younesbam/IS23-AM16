package it.polimi.ingsw.server;

import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.connection.CSConnection;

/**
 * Represent a virtual connected player. This not represent a real client, but a virtual one, distinguish by the connection type.
 */
public class VirtualPlayer {
    /**
     * Username of the player.
     */
    private final String username;
    /**
     * Game handler of the player.
     */
    private GameHandler gameHandler;
    /**
     * Client-server connection of the player.
     */
    private CSConnection connection;


    /**
     * Class constructor.
     * @param username username of the player.
     * @param c Client-server connection of the player.
     */
    public VirtualPlayer(String username, CSConnection c) {
        this.username = username;
        this.connection = c;
    }

    /**
     * Restore the player by assign a new client-server connection.
     * @param connection Client-server connection of the player.
     */
    public void restorePlayer(CSConnection connection){
        this.connection = connection;
    }

    /**
     * Unique ID getter.
     * @return ID of the player.
     */
    public int getID() {
        return connection.getID();
    }

    /**
     * Username getter.
     * @return username of the player.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Client-server getter.
     * @return Client-server connection of the player.
     */
    public CSConnection getConnection() {
        return this.connection;
    }


    /**
     * GameHandler setter.
     * @param gameHandler
     */
    public void setGameHandler(GameHandler gameHandler){
        this.gameHandler = gameHandler;
    }


    /**
     * GameHandler getter.
     * @return game handler of the player.
     */
    public GameHandler getGameHandler() {
        return this.gameHandler;
    }

    /**
     * This method is used to send the server's answer to the proper client.
     * @param a answer to be sent to the plauer.
     */
    public void send(Answer a) {
        SerializedAnswer answer = new SerializedAnswer();
        answer.setAnswer(a);
        connection.sendAnswerToClient(answer);
    }
}