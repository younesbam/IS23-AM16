package it.polimi.ingsw.model.board;

import it.polimi.ingsw.common.JSONParser;

/**
 * This is the board for 4 players.
 */
public class Board4Players extends Board {

    /**
     * Constructor for the board.
     */
    public Board4Players() {
        super();

        JSONParser jsonParser = new JSONParser("initSetup.json");
        this.grid = jsonParser.getBoard(4);
    }

}
