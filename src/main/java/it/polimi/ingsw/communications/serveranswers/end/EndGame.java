package it.polimi.ingsw.communications.serveranswers.end;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class EndGame implements Answer {
    public String answer;

    public EndGame(){
        answer = "The game has come to an end.\n";
    }
    @Override
    public String getAnswer() {
        return answer;
    }
}
