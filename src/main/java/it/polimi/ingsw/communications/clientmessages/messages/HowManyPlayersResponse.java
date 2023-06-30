package it.polimi.ingsw.communications.clientmessages.messages;

/**
 * Number of maximum players in the game. Sent by the first connected client.
 */
public class HowManyPlayersResponse implements Message {
    /**
     * Number of players in the game.
     */
    public final int numChoice;

    /**
     * Class constructor.
     * @param numChoice number of players in the game.
     */
    public HowManyPlayersResponse(int numChoice){
        this.numChoice = numChoice;
    }

    /**
     * Method to return the number of players chosen by the first player.
     * @return number of players in the game.
     */
    public int getNumChoice(){
        return this.numChoice;
    }
}
