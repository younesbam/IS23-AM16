package it.polimi.ingsw.communications.clientmessages;

import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.messages.Message;

import java.io.Serializable;

/**
 * Enclose both communications and actions needed to be sent to the server in a single object,
 * which is as well serializable.
 */
public class SerializedMessage implements Serializable {
    /**
     * Message to the server.
     */
    public final Message message;

    /**
     * Game action performed by the user.
     */
    public final GameAction gameAction;

    /**
     * Unique ID that represent the client.
     */
    public final Integer playerID;


    /**
     * Class constructor to instantiate a message to the server.
     * @param playerID ID of the player.
     * @param message message to be sent to the server.
     */
    public SerializedMessage(Integer playerID, Message message){
        this.playerID = playerID;
        this.gameAction = null;
        this.message = message;
    }


    /**
     * Class constructor to instantiate a game action to the server.
     * @param playerID ID of the player.
     * @param gameAction game action performed by the user.
     */
    public SerializedMessage(Integer playerID, GameAction gameAction){
        this.playerID = playerID;
        this.gameAction = gameAction;
        this.message = null;
    }
}
