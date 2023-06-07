package it.polimi.ingsw.communications.serveranswers.info;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class EndOfYourTurn implements Answer {

    private String answer;

    public EndOfYourTurn(){
        this.answer = "End of your turn!";
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
