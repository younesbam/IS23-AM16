package it.polimi.ingsw.communications.serveranswers.end;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class PlayerFinalResult implements Answer {
    public String answer;

    public PlayerFinalResult(String message){
        answer = message;
    }
    @Override
    public String getAnswer() {
        return answer;
    }
}
