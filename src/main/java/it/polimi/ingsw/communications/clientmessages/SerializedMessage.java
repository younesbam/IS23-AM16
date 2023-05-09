package it.polimi.ingsw.communications.clientmessages;

import it.polimi.ingsw.communications.clientmessages.actions.GameAction;

import java.io.Serializable;

/**
 * This class is used to enclose both communications and actions needed to be sent to the server in a single object,
 * which is as well serializable.
 */

public class SerializedMessage implements Serializable {

    public final Message message;
    public final GameAction gameAction;


    /**
     * Class constructor with a communication.
     * @param message
     */
    public SerializedMessage(Message message){
        this.gameAction = null;
        this.message = message;
    }


    /**
     * Class constructor with an action performed by the user.
     * @param a
     */
    public SerializedMessage(GameAction a){
        this.gameAction = a;
        this.message = null;
    }
}
