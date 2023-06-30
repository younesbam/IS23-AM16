package it.polimi.ingsw.communications.serveranswers.info;

import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.model.Game;

/**
 * Information message to the client about new game status.
 */
public class GameReplica implements Answer {
    /**
     * Game reference.
     */
    private final Game gameReplica;


    /**
     * Class constructor.
     * @param gameReplica game reference to update the status of the client.
     */
    public GameReplica(Game gameReplica){
        this.gameReplica = gameReplica;
    }

    /**
     * {@inheritDoc}
     * @return new game reference.
     */
    @Override
    public Game getAnswer() {
        return gameReplica;
    }
}
