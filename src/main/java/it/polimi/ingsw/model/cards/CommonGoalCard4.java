package it.polimi.ingsw.model.cards;

import java.util.*;

/**
 * <p>
 *     Represent card n.5,7.
 * </p>
 * Maximum 3 different types og tiles in the row/column
 * @author Nicolo' Gandini
 */
public class CommonGoalCard4 extends CommonGoalCard {

    public CommonGoalCard4(int playerNum) {
        super(playerNum);
    }

    /**
     * Check if the player respect the rules to obtain the card's points
     * @param player actual player
     * @param maxNotEq maximum number of different tiles in the group
     * @param group specify the number of tiles to take into consideration
     * @param dir direction of the group
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     * @author Nicolo' Gandini
     */
    public Integer checkScheme(Player player, int maxNotEq, int group, Direction dir) {
        int occurrences = 0;  // Viene incrementato se il tipo è trovato all'interno del Set.
        final int maxI;
        final int maxJ;
        Set<Type> set = new HashSet<>();
        Cell[][] grid = player.getBookShelf().getGrid();

        if(dir == Direction.N || dir == Direction.S){
            maxJ = MAXROW;
            maxI = MAXCOL;
        } else {
            maxJ = MAXCOL;
            maxI = MAXROW;
        }
        /*
        Trasformo la colonna/riga in un Set. In questo modo trovo subito se Type.X è contenuto nel Set e decido se può ottenere punteggio o no il player.
        Itero l'enum Type per cercare almeno una presenza del tipo di carta all'interno del Set.
        Se è >= al mumero massimo di carte diverse, allora il giocatore ha fatto l'obiettivo.
         */
        for(int i=0; i<maxI; i++){
            for(int j=0; j<maxJ; j++){
                set.clear();
                if(dir == Direction.N || dir == Direction.S)
                    set.add(grid[i][j].getObjectTile().getType());
                else
                    set.add(grid[j][i].getObjectTile().getType());
            }
            for(Type type : Type.values()){
                if(type == Type.BLANK)
                    break;
                if(set.contains(type))
                    occurrences++;
                if(occurrences <= maxNotEq)
                    return pickScoreTile();
            }
        }
        return 0;
    }
}
