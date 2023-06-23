package it.polimi.ingsw.model.board;

import it.polimi.ingsw.common.JSONParser;

/**
 * This is the board for 4 players.
 * @author Francesca Rosa Diz
 */
public class Board4Players extends Board implements IBoard {
    public Board4Players() {
        super();

        JSONParser jsonParser = new JSONParser("initSetup.json");
        this.grid = jsonParser.getBoard(4);
    }

}
