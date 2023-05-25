package it.polimi.ingsw.communications.serveranswers;

import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;

public class RequestWhereToPlaceTiles implements Answer{

    private String request;


    /**
     * Class constructor.
     */
    public RequestWhereToPlaceTiles(){
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
