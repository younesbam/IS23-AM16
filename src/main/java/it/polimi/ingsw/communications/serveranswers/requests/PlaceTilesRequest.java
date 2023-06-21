package it.polimi.ingsw.communications.serveranswers.requests;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Request message to client: place tiles notification.
 */
public class PlaceTilesRequest implements Answer {
    private final String answer;

    /**
     * Class constructor.
     */
    public PlaceTilesRequest(){
        this.answer = "Please now place the tiles in your Bookshelf!";
    }

    /**
     * {@inheritDoc}
     */
    public String getAnswer() {
        return answer;
    }
}
