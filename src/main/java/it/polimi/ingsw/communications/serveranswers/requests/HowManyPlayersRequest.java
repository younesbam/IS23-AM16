package it.polimi.ingsw.communications.serveranswers.requests;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Request message to client: number of players.
 */
public class HowManyPlayersRequest implements Answer {
    /**
     * Number of players in the game.
     */
    private final String answer;

    public HowManyPlayersRequest(String answer) {
        this.answer = answer;
    }

    /**
     * {@inheritDoc}
     * @return answer from the server.
     */
    @Override
    public String getAnswer()  {
        return answer;
    }
}
