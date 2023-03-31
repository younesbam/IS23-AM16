package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board;

public class Board3Players extends Board implements IBoard {
    @Override
    public void updateBoard() {

    }

    public boolean refillNeeded(){
        return true;
    }
}
