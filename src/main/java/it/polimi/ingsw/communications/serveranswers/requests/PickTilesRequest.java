package it.polimi.ingsw.communications.serveranswers.requests;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Request message to client: pick tiles notification.
 */
public class PickTilesRequest implements Answer {
    private final String answer;

    public PickTilesRequest(){
        this.answer = "It's your turn now! Follow the blue friendly message instructions to know what you can do. Remember that it's the phase of the turn in which you should pick your tiles!";
    }


    /**
     * {@inheritDoc}
     */
    public String getAnswer() {
        return answer;
    }
}
