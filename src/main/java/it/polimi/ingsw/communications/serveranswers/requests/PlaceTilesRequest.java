package it.polimi.ingsw.communications.serveranswers.requests;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class PlaceTilesRequest implements Answer {

    private String request;


    /**
     * Class constructor.
     */
    public PlaceTilesRequest(){
        this.request = "Please now place the tiles in your Bookshelf!";
    }

    /**
     * Answer getter.
     * @return
     */
    public String getAnswer() {
        return request;
    }
}
