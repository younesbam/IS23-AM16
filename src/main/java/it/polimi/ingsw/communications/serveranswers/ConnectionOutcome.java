package it.polimi.ingsw.communications.serveranswers;

/**
 * This class contains the server's informations about the status of connection.
 */
public class ConnectionOutcome implements Answer{
    private final boolean isConnected;
    private final String answer;

    /**
     * Class constructor.
     * @param bool
     * @param answer
     */
    public ConnectionOutcome(boolean bool, String answer){
        this.answer = answer;
        this.isConnected = bool;
    }

    /**
     * Answer getter.
     * @return
     */
    public Object getAnswer() {
        return answer;
    }

    /**
     * Method to know if the connection was successful.
     * @return
     */
    public boolean isConnected(){
        return isConnected;
    }



}
