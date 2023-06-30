package it.polimi.ingsw.communications.serveranswers.network;

import it.polimi.ingsw.communications.serveranswers.Answer;

public class RestorePlayer implements Answer {
    private final String answer;

    public RestorePlayer(){
        this.answer = ("Restore player");
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
