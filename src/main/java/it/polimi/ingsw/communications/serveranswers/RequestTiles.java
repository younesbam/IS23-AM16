package it.polimi.ingsw.communications.serveranswers;

public class RequestTiles implements Answer{

    private String request;

    public RequestTiles(){
        this.request = "Please select from the board how many tiles you want to pick.";
    }

    public String getAnswer() {
        return this.request;
    }
}
