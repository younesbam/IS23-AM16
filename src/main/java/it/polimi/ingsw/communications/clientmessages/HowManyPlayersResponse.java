package it.polimi.ingsw.communications.clientmessages;

public class HowManyPlayersResponse implements Communication{

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
