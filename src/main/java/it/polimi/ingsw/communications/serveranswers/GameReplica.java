package it.polimi.ingsw.communications.serveranswers;

import it.polimi.ingsw.model.Game;

public class GameReplica implements Answer{

    private Game gameReplica;


    /**
     * Class constructor.
     * @param gameReplica
     */
    public GameReplica(Game gameReplica){
        this.gameReplica = gameReplica;
    }


    /**
     * Game replica getter.
     * @return
     */
    public Game getGameReplica() {
        return gameReplica;
    }
}
