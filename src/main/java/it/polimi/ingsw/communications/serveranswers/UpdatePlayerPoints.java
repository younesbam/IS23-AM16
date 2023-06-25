package it.polimi.ingsw.communications.serveranswers;

public class UpdatePlayerPoints implements Answer{
    private String answer;

    public UpdatePlayerPoints(int points){
        this.answer = Integer.toString(points);
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
