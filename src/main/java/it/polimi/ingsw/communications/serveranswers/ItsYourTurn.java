package it.polimi.ingsw.communications.serveranswers;

public class ItsYourTurn implements Answer{

    private String answer;

    public ItsYourTurn(){
        this.answer = "It's now your turn!";
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
