package it.polimi.ingsw.communications.serveranswers.info;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Information message to the client. It notifies about the number of players in the game.
 */
public class PlayerNumberChosen implements Answer {

    private String answer;

    public PlayerNumberChosen(int numOfPlayers){
        this.answer = "The number of players for this match has been chosen: it's a " + numOfPlayers + " players match!";
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
