package it.polimi.ingsw.communications.serveranswers.requests;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class PickTilesRequest implements Answer {

    public String request;

    public PickTilesRequest(){
        this.request = "It's your turn now! Follow the blue friendly message instructions to know what you can do. Remember that it's the phase of the turn in which you should pick your tiles!";
    }


    /**
     * Request getter.
     * @return
     */
    public String getAnswer() {
        return request;
    }
}
