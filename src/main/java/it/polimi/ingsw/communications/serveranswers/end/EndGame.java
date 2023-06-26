package it.polimi.ingsw.communications.serveranswers.end;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class EndGame implements Answer {
    public String answer;

    public EndGame(){
        answer = "Game is ended.\nType exit to close the application";
    }
    @Override
    public String getAnswer() {
        return answer;
    }
}
