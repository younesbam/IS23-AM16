package it.polimi.ingsw.communications.serveranswers.network;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Ping request message from server.
 */
public class PingRequest implements Answer {
    private final String answer;


    /**
     * Class constructor.
     */
    public PingRequest(){
        this.answer = "Ping request from server";
    }

    /**
     * {@inheritDoc}
     */
    public String getAnswer() {
        return answer;
    }
}
