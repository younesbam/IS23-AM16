package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.3,4.
 * </p>
 * Equal tiles in a column
 */
public class EqualInCol extends CommonGoalCard {
    /**
     * Number of equal tiles
     */
    private int eq;

    /**
     * Number of repetition of the pattern
     */
    private int repetition;

    /**
     * Initialize repetition, direction and equal tiles based on the card number
     * @param cardNumber number of the card
     */
    public EqualInCol(int cardNumber) {
        super(cardNumber);
        switch (cardNumber) {
            case 3 -> {
                eq = 4;
                repetition = 4;
                //dir = Direction.N;
            }
            case 4 -> {
                eq = 2;
                repetition = 6;
                //dir = Direction.N;
            }
        }
    }

    /**
     * Checks whether the scheme is valid.
     * @param player actual player
     * @return points earned from the player.
     */
    public Integer checkScheme(Player player) {
        int actualRepetition = 0; // Number of repetitions of the same pattern. On cards as "x2", "x3", ...
        final int maxRow;
        int k = 0;
        Cell[][] grid = player.getBookShelf().getGrid();
        Boolean[][] registeredGrid = new Boolean[MAXBOOKSHELFROW][MAXBOOKSHELFCOL];
        boolean cellAlreadyRegistered = false;

        for(int i=0; i<MAXBOOKSHELFROW; i++)
            for(int j=0; j<MAXBOOKSHELFCOL; j++)
                registeredGrid[i][j] = false;

        List<Tile> list = new ArrayList<>();

        /*
        This cycle controls vertically if patterns are present.
        When a pattern is found, it marks the cells as already visited in order to not check them twice.
         */
        for(int i=0; i<MAXBOOKSHELFCOL; i++) {
            list.clear();
            k=0;

            // Scan the n tiles requested by the card.
            while(k<=MAXBOOKSHELFROW-eq) {
                list.clear();
                cellAlreadyRegistered = false;

                // Add the tiles to a list to check if they are equals.
                for(int j=k; j<eq+k; j++)
                    list.add(grid[j][i].getTile());

                // If tiles are equals, if the pattern hasn't already been registered, I regiter it and count it as found.
                if(eqTiles(list)){
                    for(int j=k; j<eq+k; j++){
                        if(registeredGrid[j][i]){
                            cellAlreadyRegistered = true;
                            break;
                        }
                    }

                    // If the pattern hadn't already been found, I count it and mark the cells as already registered.
                    if (!cellAlreadyRegistered){
                        for(int j=k; j<eq+k; j++)
                            registeredGrid[j][i] = true;
                        actualRepetition++;
                    }
                }

                if(actualRepetition >= repetition){
                    return getScore();
                }
                k++;
            }
        }

        /*
        This cycle check horizontally if there are patterns.
        When it finds a pattern, it marks cells as visited in order to not check them twice.
         */
        for(int i=0; i<MAXBOOKSHELFROW; i++) {
            list.clear();
            k=0;
            while(k<=MAXBOOKSHELFCOL-eq) {
                list.clear();
                cellAlreadyRegistered = false;
                for (int j=k; j<eq+k; j++)
                    list.add(grid[i][j].getTile());
                if(eqTiles(list)) {
                    for (int j=k; j<eq+k; j++) {
                        if(registeredGrid[i][j]){
                            cellAlreadyRegistered = true;
                            break;
                        }
                    }
                    if(!cellAlreadyRegistered){
                        for (int j=k; j<eq+k; j++)
                            registeredGrid[i][j] = true;
                        actualRepetition++;
                    }
                }
                if(actualRepetition>=repetition)
                    return getScore();
                k++;
            }
        }
        return 0;
    }


    /**
     * Check whether all the tiles are equals.
     * @param list of tiles contained in one row/column.
     * @return true if all tiles are equals.
     */
    private boolean eqTiles(List<Tile> list){
        for(int i=0; i<list.size()-1; i++){
            for(int j=i+1; j<list.size(); j++)
                if(!list.get(i).equals(list.get(j)) || list.contains(Tile.BLANK))
                    return false;
        }
        return true;
    }


    /**
     * Prints the card on the CLI.
     * {@inheritDoc}
     */
    public void printCard(){
        switch (cardNumber) {
            case 3 -> {
                System.out.println( "COMMON CARD NUMBER 3: \n" +
                                    "Four groups each containing at least 4 tiles of the same type (not necessarily\n" +
                                    "in the depicted shape).\n" +
                                    "The tiles of one group can be different from those of another group.\n" +
                                    "++++++++++++++++++++ \n"+
                                    "+      | = |       + \n" +
                                    "+      | = | x4    + \n" +
                                    "+      | = |       + \n" +
                                    "+      | = |       + \n"+
                                    "++++++++++++++++++++ \n");
            }
            case 4 -> {
                System.out.println( "COMMON CARD NUMBER 4:  \n" +
                                    "Six groups each containing at least 2 tiles of the same type (not necessarily \n" +
                                    "in the depicted shape).\n" +
                                    "The tiles of one group can be different from those of another group.\n" +
                                    "+++++++++++++++++++++ \n"+
                                    "+       | = |       + \n" +
                                    "+       | = |  x6   + \n"+
                                    "+++++++++++++++++++++ \n");
            }
        }
    }
}
