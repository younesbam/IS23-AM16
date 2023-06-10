package it.polimi.ingsw.communications.serveranswers.info;

<<<<<<<< HEAD:src/main/java/it/polimi/ingsw/communications/serveranswers/GameReady.java
public class GameReady implements Answer{
========
import it.polimi.ingsw.communications.serveranswers.Answer;

public class ItsYourTurn implements Answer {

>>>>>>>> 03a2833 (Merge pull request #20 from younesbam/resilience):src/main/java/it/polimi/ingsw/communications/serveranswers/info/ItsYourTurn.java
    private String answer;

    public GameReady(){
        this.answer = "Ready to play!";
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
