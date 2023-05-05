package it.polimi.ingsw.communications.clientmessages;

import it.polimi.ingsw.client.common.Connection;

/**
 * This class encapsulates player's username information in a communication object, reaady to be sent to the server.
 */
public class UsernameSetup implements Communication {
    private final String username;
    //forse è inutile!!! avere un attributo connessione qui intendo.
    private final Connection connectionType;

    /**
     * Class constructor.
     * @param username
     */
    public UsernameSetup(String username, Connection connectionType){
        this.username = username;
        this.connectionType = connectionType;
    }

    /**
     * Username getter.
     * @return
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * ConnectioType getter.
     * @return
     */
    public Connection getConnectionType(){
        return this.connectionType;
    }
}
