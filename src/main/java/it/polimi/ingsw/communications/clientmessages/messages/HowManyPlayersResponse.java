package it.polimi.ingsw.communications.clientmessages.messages;

/**
 * Number of maximum players in the game. Used by the first connected client.
 */
public class HowManyPlayersResponse implements Message {

    public final int numChoice;

    /**
     * Class constructor.
     * @param numChoice
     */
    public HowManyPlayersResponse(int numChoice){
        this.numChoice = numChoice;
    }

    /**
     * Method to return the number of players chosen by the first player.
     * @return
     */
    public int getNumChoice(){
        return this.numChoice;
    }
}
