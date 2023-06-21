package it.polimi.ingsw.communications.serveranswers;

public class GameReady implements Answer{
    private String answer;

    public GameReady(){
        this.answer = "Ready to play!";
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
