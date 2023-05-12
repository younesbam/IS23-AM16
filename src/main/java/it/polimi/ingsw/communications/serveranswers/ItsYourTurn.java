package it.polimi.ingsw.communications.serveranswers;

public class ItsYourTurn implements Answer{
    private String turnMessage;

    /**
     * Class constructor.
     */
    public ItsYourTurn(){
        this.turnMessage = "It's now you turn!";
    }


    /**
     * TurnMessage getter.
     * @return
     */
    public String getAnswer(){
        return this.turnMessage;
    }


}
