package it.polimi.ingsw.communications.serveranswers.requests;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class HowManyPlayersRequest implements Answer {

    private String playersNumber;

    public HowManyPlayersRequest(String playersNumber) {
        this.playersNumber = playersNumber;
    }

    @Override
    public String getAnswer()  {
        return playersNumber;
    }
}
