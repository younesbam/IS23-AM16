package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Board2Players;
import it.polimi.ingsw.model.board.Board3Players;
import it.polimi.ingsw.model.board.Board4Players;

/**
 * This class is used to apply the factory pattern.
 * @author Francesca Rosa Diz
 */
public class CreationFactory {

    /**
     * This method instantiates the board based on the number of players.
     */
    public Board createBoard(int numPlayers) throws IllegalArgumentException {
        if(numPlayers < 2 || numPlayers > 4)
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
