package it.polimi.ingsw.communications.clientmessages;

import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.Communication;

import java.io.Serializable;

/**
 * This class is used to enclose both communications and actions needed to be sent to the server in a single object,
 * which is as well serializable.
 */

public class SerializedCommunication implements Serializable {

    public final Communication communication;
    public final GameAction gameAction;


    /**
     * Class constructor with a communication.
     * @param communication
     */
    public SerializedCommunication(Communication communication){
        this.gameAction = null;
        this.communication = communication;
    }


    /**
     * Class constructor with an action performed by the user.
     * @param a
     */
    public SerializedCommunication(GameAction a){
        this.gameAction = a;
        this.communication = null;
    }
}
