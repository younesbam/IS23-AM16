package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Board;

public class Board2Players extends Board implements IBoard{

    @Override
    public void updateBoard(){
        System.out.println("I'm in Board2Players.");
    }

    public boolean refillNeeded(){
        return true;
    }
}
