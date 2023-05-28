package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.sql.SQLOutput;
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
        int actualRepetition = 0;  // Rappresenta il numero di ripetizioni dello stesso algoritmo. Sulle carte indicate come "x2", "x3"...
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
        Trasformo la colonna/riga in un Set. Dal momento che il set non ammette ripetizioni, controllo la
        dimensione del Set: se è minore del massimo delle tessere diverse che posso avere, incremento le ripetizioni.
         */
        for(int i=0; i<maxI; i++){
            set.clear();
            for(int j=0; j<maxJ; j++){
                if(dir == Direction.N || dir == Direction.S)
                    set.add(grid[j][i].getTile());
                else
                    set.add(grid[i][j].getTile());
            }
            if (set.size() <= maxNotEq && !set.contains(Tile.BLANK))
                actualRepetition++;

            if (actualRepetition >= repetition)
                return getScore();
        }
        return 0;
    }


    /**
     * {@inheritDoc}
     */
    public void printCard(){
        switch (cardNumber) {
            case 5 -> {
                System.out.println(         "COMMON CARD NUMBER 5 \n" +
                                    "++++++++++++++++++++++++++++++++ \n"+
                                    "+          |   |               + \n" +
                                    "+          |   |               + \n" +
                                    "+          |   |  MAX 3| ≠ |   + \n" +
                                    "+          |   |       x3      + \n" +
                                    "+          |   |               + \n" +
                                    "+          |   |               + \n"+
                                    "++++++++++++++++++++++++++++++++ \n");
            }
            case 7 -> {
                System.out.println( "COMMON CARD NUMBER 7 \n" +
                                    "++++++++++++++++++++++++++++ \n"+
                                    "+  |   |   |   |   |   |   + \n" +
                                    "+        MAX 3| ≠ |        + \n" +
                                    "+           x4             + \n"+
                                    "++++++++++++++++++++++++++++ \n");
            }
        }
    }
}
