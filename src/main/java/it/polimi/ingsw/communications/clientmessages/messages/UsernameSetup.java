package it.polimi.ingsw.communications.clientmessages.messages;

/**
 * This class encapsulates player's username information in a communication object, reaady to be sent to the server.
 */
public class UsernameSetup implements Message {
    private final String username;
    //forse è inutile!!! avere un attributo connessione qui intendo.
    // private final Client client;

    /**
     * Class constructor.
     * @param username
     */
    public UsernameSetup(String username){
        this.username = username;
        //this.client = client;
    }

    /**
     * Username getter.
     * @return
     */
    public String getUsername(){
        return this.username;
    }
}
