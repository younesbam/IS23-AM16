package it.polimi.ingsw.communications.serveranswers;

public class UpdateTurn implements Answer{
    /**
     * Message to the client.
     */
    private String message;


    /**
     * Tell the player if it's his turn.
     */
    private boolean yourTurn;

    /**
     * Class constructor.
     */
    public UpdateTurn(){
        this.message = "It's now you turn!";
        this.yourTurn = false;
    }


    /**
     * Get the information bit, to know if it's the player's turn.
     * @return
     */
    public boolean isYourTurn(){
        return this.yourTurn;
    }


    /**
     * TurnMessage getter.
     * @return
     */
    public String getAnswer(){
        return this.message;
    }
}
