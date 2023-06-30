package it.polimi.ingsw.communications.serveranswers.info;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Information message to the client. It notifies the client that can prints the cards.
 */
public class PrintCardsAnswer implements Answer {
    /**
     * {@inheritDoc}
     * @return answer from the server.
     */
    @Override
    public Object getAnswer() {
        return null;
    }
}
