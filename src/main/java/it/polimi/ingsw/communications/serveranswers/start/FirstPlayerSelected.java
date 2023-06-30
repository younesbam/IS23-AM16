package it.polimi.ingsw.communications.serveranswers.start;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class FirstPlayerSelected implements Answer {
    private String answer;

    public FirstPlayerSelected(String username){
        this.answer = (username);
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
