package it.polimi.ingsw.communications.serveranswers.info;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Information message to the client about the end of his turn.
 */
public class EndOfYourTurn implements Answer {

    private String answer;

    public EndOfYourTurn(){
        this.answer = "End of your turn!";
    }

    /**
     * {@inheritDoc}
     * @return answer from the server.
     */
    @Override
    public String getAnswer() {
        return answer;
    }
}
