package it.polimi.ingsw.communications.serveranswers.requests;

import it.polimi.ingsw.communications.serveranswers.Answer;

/**
 * Ping request message from server
 */
public class PingRequest implements Answer {

    private final String request;


    /**
     * Class constructor.
     */
    public PingRequest(){
        this.request = "Ping request from server";
    }

    /**
     * Answer getter.
     * @return
     */
    public String getAnswer() {
        return request;
    }
}
