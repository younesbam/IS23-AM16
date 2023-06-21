package it.polimi.ingsw.communications.serveranswers;

/**
 * Custom answer from the server. Use this class to print a custom message from the server to the client.
 */
public class CustomAnswer implements Answer{
//TODO: eliminare questo bit inutile. C'è da fare un po' di refactor in giro per il codice.
    private final boolean can;
    private final String answer;


    public CustomAnswer(boolean can, String answer){
        this.can = can;
        this.answer = answer;
    }


    /**
     * {@inheritDoc}
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
