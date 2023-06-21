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
 */
public class MaxDiffGroup extends CommonGoalCard {
    /**
     * Maximum number of different tiles in the group
     */
    private int maxNotEq;

    /**
     * Number of repetition of the pattern.
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
     * Checks whether the scheme is valid.
     * @param player actual player
     * @return points earned from the player.
     */
    public Integer checkScheme(Player player) {
        int actualRepetition = 0;  // Number of repetitions of the same pattern. On cards as "x2", "x3", ...
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
        Transformation of the row/column in a Set. Since the Set doesn't allow repetitions, we check the size of the Set:
        if it is lower than the maximum number of different tiles allowed, actualRepetition is incremented.
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
     * Prints the card on the CLI.
     * {@inheritDoc}
     */
    public void printCard(){
        switch (cardNumber) {
            case 5 -> {
                System.out.println(         "COMMON CARD NUMBER 5: \n" +
                                    "Three columns each formed by 6 tiles of maximum three different types.\n" +
                                    "One column can show the same or a different combination of another column.\n" +
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
                System.out.println( "   COMMON CARD NUMBER 7: \n" +
                                    "Four lines each formed by 5 tiles of maximum three different types.\n" +
                                    "One line can show the same or a different combination of another line.\n" +
                                    "++++++++++++++++++++++++++++ \n"+
                                    "+  |   |   |   |   |   |   + \n" +
                                    "+        MAX 3| ≠ |        + \n" +
                                    "+           x4             + \n"+
                                    "++++++++++++++++++++++++++++ \n");
            }
        }
    }
}
