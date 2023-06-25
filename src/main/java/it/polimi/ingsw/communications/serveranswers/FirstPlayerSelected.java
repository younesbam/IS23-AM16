package it.polimi.ingsw.communications.serveranswers;

public class FirstPlayerSelected implements Answer{
    private String answer;

    public FirstPlayerSelected(String username){
        this.answer = (username);
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
