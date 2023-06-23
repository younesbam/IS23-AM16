package it.polimi.ingsw.communications.serveranswers;

/**
 * Custom answer from the server. Use this class to print a custom message from the server to the client.
 */
public class CustomAnswer implements Answer{
    /**
     * Server answer to client.
     */
    private final String answer;

    /**
     * Constructor.
     * @param answer answer to be sent to client.
     */
    public CustomAnswer(String answer){
        this.answer = answer;
    }


    /**
     * {@inheritDoc}
     */
    public String getAnswer(){
        return this.answer;
    }


}
