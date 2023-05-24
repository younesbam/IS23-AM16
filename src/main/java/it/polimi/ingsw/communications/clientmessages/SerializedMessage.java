package it.polimi.ingsw.communications.clientmessages;

import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.messages.Message;

import java.io.Serializable;

/**
 * This class is used to enclose both communications and actions needed to be sent to the server in a single object,
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
     * Class constructor with a message to the server.
     * @param message
     */
    public SerializedMessage(Integer playerID, Message message){
        this.playerID = playerID;
        this.gameAction = null;
        this.message = message;
    }


    /**
     * Class constructor with an action performed by the user.
     * @param a
     */
    public SerializedMessage(Integer playerID, GameAction a){
        this.playerID = playerID;
        this.gameAction = a;
        this.message = null;
    }
}
