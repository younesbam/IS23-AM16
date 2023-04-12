package it.polimi.ingsw.model.board;

import it.polimi.ingsw.common.JSONParser;

/**
 * This is the board for 2 players.
 * @author Francesca Rosa Diz.
 */
public class Board2Players extends Board implements IBoard {

    /**
     * Constructor for the board.
     */
    public Board2Players() {
        super();

        JSONParser jsonParser = new JSONParser("json/initSetup.json");
        this.grid = jsonParser.getBoard(2);
    }

}
