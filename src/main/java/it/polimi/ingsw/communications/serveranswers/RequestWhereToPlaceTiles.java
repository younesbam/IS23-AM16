package it.polimi.ingsw.communications.serveranswers;

import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;

public class RequestWhereToPlaceTiles implements Answer{

    private String request;


    public RequestWhereToPlaceTiles(ArrayList<Tile> tiles){
        String string = new String();
        for(Tile t : tiles){
            string = string + " " + t.name();
        }
        this.request = "Please now place your tiles in your Bookshelf!" + "\nYou have to place the following tiles: " + string;
    }


    public String getAnswer() {
        return request;
    }
}
