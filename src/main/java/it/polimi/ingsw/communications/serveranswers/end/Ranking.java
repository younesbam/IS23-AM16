package it.polimi.ingsw.communications.serveranswers.end;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class Ranking implements Answer {
    public String answer;

    public Ranking(String ranking){
        answer = ranking;
    }
    @Override
    public String getAnswer() {
        return answer;
    }
}
