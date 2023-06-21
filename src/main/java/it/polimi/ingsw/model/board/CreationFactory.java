package it.polimi.ingsw.model.board;

import java.io.Serializable;

import static it.polimi.ingsw.Const.MAXPLAYERS;
import static it.polimi.ingsw.Const.MINPLAYERS;

/**
 * This class is used to apply the factory pattern.
 */
public class CreationFactory implements Serializable {

    /**
     * This method instantiates the board based on the number of players.
     */
    public Board createBoard(int numPlayers) throws IllegalArgumentException {
        if(numPlayers < MINPLAYERS || numPlayers > MAXPLAYERS)
            throw new IllegalArgumentException("Wrong number of players.");

        switch (numPlayers) {
            case 2:
                return new Board2Players();

            case 3:
                return new Board3Players();

            case 4:
                return new Board4Players();

            default:
                throw new IllegalArgumentException("Wrong number of players");
        }
    }
}
