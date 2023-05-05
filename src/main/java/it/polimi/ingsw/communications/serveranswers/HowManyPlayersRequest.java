package it.polimi.ingsw.communications.serveranswers;

public class HowManyPlayersRequest implements Answer{

    private String playersNumber;

    public HowManyPlayersRequest(String playersNumber) {
        this.playersNumber = playersNumber;
    }

    @Override
    public String getMessage() {
        return playersNumber;
    }
}
