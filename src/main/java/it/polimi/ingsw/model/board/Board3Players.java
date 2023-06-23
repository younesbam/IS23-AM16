package it.polimi.ingsw.model.board;

import it.polimi.ingsw.common.JSONParser;

/**
 * This is the board for 3 players.
 */
public class Board3Players extends Board implements IBoard {

    public Board3Players() {
        super();

        JSONParser jsonParser = new JSONParser("initSetup.json");
        this.grid = jsonParser.getBoard(3);
    }

}
