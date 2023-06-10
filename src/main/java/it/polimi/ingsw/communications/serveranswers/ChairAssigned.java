package it.polimi.ingsw.communications.serveranswers.info;

<<<<<<<< HEAD:src/main/java/it/polimi/ingsw/communications/serveranswers/ChairAssigned.java
public class ChairAssigned implements Answer{
========
import it.polimi.ingsw.communications.serveranswers.Answer;

public class EndOfYourTurn implements Answer {

>>>>>>>> 03a2833 (Merge pull request #20 from younesbam/resilience):src/main/java/it/polimi/ingsw/communications/serveranswers/info/EndOfYourTurn.java
    private String answer;

    public ChairAssigned(){
        this.answer = "You are the first player!";
    }

    @Override
    public String getAnswer() {
        return answer;
    }
}
