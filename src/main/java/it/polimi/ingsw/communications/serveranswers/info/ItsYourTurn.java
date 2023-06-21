package it.polimi.ingsw.communications.serveranswers.info;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Information message to the client. It notifies the client about the turn update.
 */
public class ItsYourTurn implements Answer {

    private String answer;

    public ItsYourTurn(){
        this.answer = "It's now your turn!";
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
