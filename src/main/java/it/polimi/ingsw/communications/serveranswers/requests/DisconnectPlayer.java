package it.polimi.ingsw.communications.serveranswers.requests;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Request message to client: disconnect the player
 */
public class DisconnectPlayer implements Answer {

    public DisconnectPlayer(){};

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAnswer() {
        return null;
    }
}
