package it.polimi.ingsw.communications.serveranswers;

public class PersonalizedAnswer implements Answer{

    private final boolean can;
    private final String answer;


    public PersonalizedAnswer(boolean can, String answer){
        this.can = can;
        this.answer = answer;
    }


    /**
     * Answer getter.
     * @return
     */
    public String getAnswer(){
        return this.answer;
    }


    /**
     * Can getter.
     * @return
     */
    public boolean getCan(){
        return this.can;
    }


}
