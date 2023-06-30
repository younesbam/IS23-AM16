package it.polimi.ingsw.communications.serveranswers.end;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class PlayerFinalPoints implements Answer {
    public String answer;

    public PlayerFinalPoints(String message){
        answer = message;
    }
    @Override
    public String getAnswer() {
        return answer;
    }
}
