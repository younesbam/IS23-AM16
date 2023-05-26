package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.*;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.5,7.
 * </p>
 * Maximum 3 different types og tiles in the row/column
 * @author Nicolo' Gandini
 */
public class MaxDiffGroup extends CommonGoalCard {
    /**
     * Maximum number of different tiles in the group
     */
    private int maxNotEq;

    /**
     *
     */
    private int repetition;

    /**
     * Specify the number of tiles to take into consideration
     */
    private int group;

    /**
     * Direction of the group
     */
    private Direction dir;

    /**
     * Initialize all the attributes based on the card number
     * @param cardNumber number of the card
     */
    public MaxDiffGroup(int cardNumber) {
        super(cardNumber);
        switch (cardNumber) {
            case 5 -> {
                maxNotEq = 3;
                repetition = 3;
                group = 6;
                dir = Direction.N;
            }
            case 7 -> {
                maxNotEq = 3;
                repetition = 4;
                group = 5;
                dir = Direction.E;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer checkScheme(Player player) {
        int occurrences = 0;  // Viene incrementato se il tipo è trovato all'interno del Set.
        final int maxI;
        final int maxJ;
        Set<Tile> set = new HashSet<>();
        Cell[][] grid = player.getBookShelf().getGrid();

        if(dir == Direction.N || dir == Direction.S){
            maxJ = MAXBOOKSHELFROW;
            maxI = MAXBOOKSHELFCOL;
        } else {
            maxJ = MAXBOOKSHELFCOL;
            maxI = MAXBOOKSHELFROW;
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
                    set.add(grid[j][i].getTile());
                else
                    set.add(grid[i][j].getTile());
            }
            for(Tile type : Tile.values()){
                if(type == Tile.BLANK)
                    break;
                if(set.contains(type))
                    occurrences++;
                if(occurrences <= maxNotEq)
                    return getScore();
            }
        }
        return 0;
    }


    /**
     * {@inheritDoc}
     */
    public void printCard(){

    }
}
