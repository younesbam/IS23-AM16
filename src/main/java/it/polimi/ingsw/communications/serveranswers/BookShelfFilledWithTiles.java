package it.polimi.ingsw.communications.serveranswers;

public class BookShelfFilledWithTiles implements Answer{
    public String answer;

    public BookShelfFilledWithTiles(){
        answer = "You have correctly placed your tiles!";
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}