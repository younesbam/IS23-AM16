package it.polimi.ingsw.model.cards;

import java.util.ArrayList;
import java.util.Collections;

/**
 * <p>
 *     Represent card n.9.
 * </p>
 * Eight tiles in random order must me the same.
 * @author Nicolo' Gandini
 */
public class EqualRand extends CommonGoalCard {
    int eq;

    public EqualRand(int playerNum, int cardNumber) {
        super(playerNum, cardNumber);
        eq = 8;
    }

    /**
     * Check if the player respect the rules to obtain the card's points
     * @param player actual player
     * @param eq number of tiles that must me the same
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     * @author Nicolo' Gandini
     */
    public Integer checkScheme(Player player) {
        Cell[][] grid = player.getBookShelf().getGrid();
        List<Tile> list = new ArrayList<>();  //Creo una lista per cercare le occorrenze

        for(int j=0; j<MAXROW; j++) {
            for (int i=0; i<MAXCOL; i++) {
                list.add(grid[i][j].getTile());
            }
        }
        /*
        Itero tutti i tipi delle tessere per cercare la frequenza con cui compaiono nella lista.
         */
        for(Tile type : Tile.values()){
            if(type != Tile.BLANK) {
                int occurrences = Collections.frequency(list, type);
                if (occurrences >= eq)
                    return getScore();
            }
        }
        return 0;
    }
}
