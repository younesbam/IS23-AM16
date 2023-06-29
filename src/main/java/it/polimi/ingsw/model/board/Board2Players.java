package it.polimi.ingsw.model.board;

import it.polimi.ingsw.common.JSONParser;

/**
 * This is the board for 2 players.
 */
public class Board2Players extends Board{

    /**
     * Constructor for the board.
     */
    public Board2Players() {
        super();

        JSONParser jsonParser = new JSONParser("initSetup.json");
        this.grid = jsonParser.getBoard(2);
    }

}
