package it.polimi.ingsw.model.board;

/**
 * Interface needed to implement the factory pattern.
 */
public interface IBoard {
    public void updateBoard();
    public boolean refillNeeded();
}
