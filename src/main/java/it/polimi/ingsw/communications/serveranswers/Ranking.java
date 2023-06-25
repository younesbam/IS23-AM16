package it.polimi.ingsw.communications.serveranswers;

public class Ranking implements Answer{
    public String answer;

    public Ranking(String ranking){
        answer = ranking;
    }
    @Override
    public String getAnswer() {
        return answer;
    }
}
