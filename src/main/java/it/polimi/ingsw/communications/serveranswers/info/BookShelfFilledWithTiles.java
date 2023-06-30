package it.polimi.ingsw.communications.serveranswers.info;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Information message to the client about successfully tiles placing action.
 */
public class BookShelfFilledWithTiles implements Answer {
    public String answer;

    public BookShelfFilledWithTiles(){
        answer = "You have correctly placed your tiles!";
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
