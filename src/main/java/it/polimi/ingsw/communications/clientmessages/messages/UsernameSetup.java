package it.polimi.ingsw.communications.clientmessages.messages;

/**
 * This class encapsulates player's username information in a communication object, ready to be sent to the server.
 */
public class UsernameSetup implements Message {
    private final String username;

    /**
     * Class constructor.
     * @param username username of the player
     */
    public UsernameSetup(String username){
        this.username = username;
    }

    /**
     * Username getter.
     * @return username of the player
     */
    public String getUsername(){
        return this.username;
    }
}
