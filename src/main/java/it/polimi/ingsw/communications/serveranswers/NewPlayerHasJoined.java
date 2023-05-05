package it.polimi.ingsw.communications.serveranswers;

public class NewPlayerHasJoined implements Answer{

    private String message;

    /**
     * Constructor of the class.
     * @param message
     */
    public NewPlayerHasJoined(String message){
        this.message = message;
    }


    @Override
    public Object getAnswer(){
        return this.message;
    }
}
