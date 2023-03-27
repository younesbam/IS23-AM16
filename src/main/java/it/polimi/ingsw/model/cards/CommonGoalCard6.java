package it.polimi.ingsw.model.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *     Represent card n.9.
 * </p>
 * Eight tiles in random order must me the same.
 * @author Nicolo' Gandini
 */
public class CommonGoalCard6 extends CommonGoalCard {

    public CommonGoalCard6(int playerNum) {
        super(playerNum);
    }

    /**
     * Check if the player respect the rules to obtain the card's points
     * @param player actual player
     * @param eq number of tiles that must me the same
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     * @author Nicolo' Gandini
     */
    public Integer checkScheme(Player player, int eq) {
        Cell[][] grid = player.getBookShelf().getGrid();
        List<Type> list = new ArrayList<>();  //Creo una lista per cercare le occorrenze

        for(int j=0; j<MAXROW; j++) {
            for (int i=0; i<MAXCOL; i++) {
                list.add(grid[i][j].getObjectTile().getType());
            }
        }
        /*
        Itero tutti i tipi delle tessere per cercare la frequenza con cui compaiono nella lista.
         */
        for(Type type : Type.values()){
            if(type != Type.BLANK) {
                int occurrences = Collections.frequency(list, type);
                if (occurrences >= eq)
                    return pickScoreTile();
            }
        }
        return 0;
    }
}
