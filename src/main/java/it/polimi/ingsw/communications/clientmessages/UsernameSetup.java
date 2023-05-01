package it.polimi.ingsw.communications.clientmessages;

import it.polimi.ingsw.client.common.ClientConnection;

/**
 * This class encapsulates player's username information in a communication object, reaady to be sent to the server.
 */
public class UsernameSetup implements Communication {
    private final String username;
    private final ClientConnection connectionType;

    /**
     * Class constructor.
     * @param username
     */
    public UsernameSetup(String username, ClientConnection connectionType){
        this.username = username;
        this.connectionType = connectionType;
    }

    /**
     * Username getter.
     * @param username
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * ConnectioType getter.
     * @return
     */
    public ClientConnection getConnectionType(){
        return this.connectionType;
    }
}
