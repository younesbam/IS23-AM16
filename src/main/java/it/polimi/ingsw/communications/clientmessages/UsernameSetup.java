package it.polimi.ingsw.communications.clientmessages;

import it.polimi.ingsw.communications.clientmessages.Communication;

/**
 * This class encapsulates player's username information in a communication object, reaady to be sent to the server.
 */
public class UsernameSetup implements Communication {
    private final String username;

    /**
     * Class constructor.
     * @param username
     */
    public UsernameSetup(String username){
        this.username = username;
    }

    /**
     * Username getter.
     * @param username
     */
    public GetUsername(String username){
        return this.username;
    }
}
