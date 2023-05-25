package it.polimi.ingsw.communications.serveranswers;

public class PlayerNumberChosen implements Answer{

    private String answer;

    public PlayerNumberChosen(int numOfPlayers){
        this.answer = "The number of players for this match has been chosen: it's a " + numOfPlayers + " players match!";
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
