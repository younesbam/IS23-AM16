package it.polimi.ingsw.communications.serveranswers.errors;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Error answer from the server. Notify the client about specific errors during the game.
 */
public class ErrorAnswer implements Answer {
    /**
     * Custom message to be printed by the client.
     */
    private String answer;
    /**
     * Type of error that occurs.
     */
    private final ErrorClassification errorClassification;


    /**
     * Constructor.
     * @param errorClassification type of error that occurs.
     */
    public ErrorAnswer(ErrorClassification errorClassification){
        this.errorClassification = errorClassification;
        this.answer = null;
    }


    /**
     * Constructor with both ErrorClassification and answer.
     * @param answer Custom message to be printed by the client.
     * @param errorClassification type of error that occurs.
     */
    public ErrorAnswer(String answer, ErrorClassification errorClassification){
        this.errorClassification = errorClassification;
        this.answer = answer;
    }


    /**
     * Answer getter.
     * @return answer ready  to be printed by the client.
     */
    public Object getAnswer(){
        return this.answer;
    }

    /**
     * Error classification getter.
     * @return type of error that occurs.
     */
    public ErrorClassification getError(){
        return this.errorClassification;
    }
}
