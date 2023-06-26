package it.polimi.ingsw.communications.serveranswers.end;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class BookShelfCompleted implements Answer {
    public String answer;

    public BookShelfCompleted(){
        answer = "\nYou have completed your Bookshelf!\nthe remaining players will play their turn\nOnce the round is completed, the winner is awarded!\n";
    }

    public BookShelfCompleted(String username){
        answer = "\nPlayer " + username + " has completed his Bookshelf!\nNow we will go on with turns\n until we reach the player that started the match!\n(The one and only with the majestic chair!)\n";
    }
    @Override
    public String getAnswer() {
        return answer;
    }
}
