package it.polimi.ingsw.server;

import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;

public class VirtualPlayer {

    public final String username;
    public final int ID;
    public final GameHandler gameHandler;
    public final ClientSocketConnection clientSocketConnection;


    /**
     * Class constructor.
     * @param username
     * @param ID
     * @param c
     * @param g
     */
    public VirtualPlayer(String username, int ID, ClientSocketConnection c, GameHandler g){
        this.username = username;
        this.ID = ID;
        this.clientSocketConnection = c;
        this.gameHandler = g;
    }

    /**
     * ID getter.
     * @return
     */
    public int getID(){
        return this.ID;
    }

    /**
     * Username getter.
     * @return
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * ClientSocketConnection getter.
     * @return
     */
    public ClientSocketConnection getClientSocketConnection(){
        return this.clientSocketConnection;
    }

    /**
     * GameHandler getter.
     * @return
     */
    public GameHandler getGameHandler(){
        return this.gameHandler;
    }


    public void send(Answer a){
        SerializedAnswer answer = new SerializedAnswer();
        answer.setAnswer(a);
        clientSocketConnection.s
}
