package it.polimi.ingsw.communications.serveranswers;

public class SetupCompleted implements Answer{
    private final String answer;


    public SetupCompleted(String answer){
        this.answer = answer;
    }


    /**
     * Answer getter.
     * @return
     */
    public String getAnswer(){
        return this.answer;
    }

}
