package it.polimi.ingsw.communications.serveranswers.start;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class ChairAssigned implements Answer {

    private String answer;

    public ChairAssigned(){
        this.answer = "You are the first player!";
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
