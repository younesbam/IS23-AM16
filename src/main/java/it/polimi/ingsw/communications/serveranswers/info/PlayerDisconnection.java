package it.polimi.ingsw.communications.serveranswers.info;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class PlayerDisconnection implements Answer {
    public String answer;

    public PlayerDisconnection(String answer){
        this.answer = answer;
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
