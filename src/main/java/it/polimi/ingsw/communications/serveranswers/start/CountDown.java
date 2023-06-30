package it.polimi.ingsw.communications.serveranswers.start;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class CountDown implements Answer {
    private String answer;

    public CountDown(int time){
        this.answer = ("Game will start in " + time);
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
