package it.polimi.ingsw.model.board;

import it.polimi.ingsw.common.JSONParser;

/**
 * This is the board for 3 players.
 * @author Francesca Rosa Diz
 */
public class Board3Players extends Board implements IBoard {

    public Board3Players() {
        super();

        JSONParser jsonParser = new JSONParser("json/initSetup.json");
        this.grid = jsonParser.getBoard(3);
    }

}
