package it.polimi.ingsw.communications.clientmessages.messages;

public class JoinGameMessage implements Message{

    /**
     * Name of the match.
     */
    private String matchName;


    /**
     * Class constructor.
     * @param matchName
     */
    public JoinGameMessage(String matchName){
        this.matchName = matchName;
    }


    /**
     * MatchName getter.
     * @return
     */
    public String getMatchName() {
        return matchName;
    }
}
