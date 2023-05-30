package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.2,6.
 * </p>
 * Different cards in a row/column
 * @author Nicolo' Gandini
 */
public class DiffAligned extends CommonGoalCard {
    /**
     * Repetition of the pattern
     */
    private int repetition;

    /**
     * Direction
     */
    private Direction dir;

    /**
     * Initialize repetition and direction based on the card number
     * @param cardNumber number of the card
     */
    public DiffAligned(int cardNumber) {
        super(cardNumber);
        switch (cardNumber) {
            case 2 -> {
                repetition = 2;
                dir = Direction.N;
            }
            case 6 -> {
                repetition = 2;
                dir = Direction.E;
            }
            default -> {
                repetition = -1;
                dir = Direction.N;
            }
        }
    }

    /**
     * Checks whether the scheme is valid.
     * @param player actual player
     * @return points earned from the player.
     */
    public Integer checkScheme(Player player) {
        int actualRepetition = 0; // Number of repetitions of the same algorithm. On cards as "x2", "x3", ...
        final int maxI;
        final int maxJ;
        Cell[][] grid = player.getBookShelf().getGrid();

        /*
        Variables maxI and maxJ are used to give boundaries to the table. j controls whether tiles are equals or not.
        i lets you change row or column.
        To check vertically: j must be limited to MAXROW-1.
        To check horizontally: j must be limited to MAXCOL-1.
         */
        if(dir == Direction.N || dir == Direction.S){
            maxJ = MAXBOOKSHELFROW;
            maxI = MAXBOOKSHELFCOL;
        } else {
            maxJ = MAXBOOKSHELFCOL;
            maxI = MAXBOOKSHELFROW;
        }

        List<Tile> list = new ArrayList<>();
        for(int i=0; i<maxI; i++){
            list.clear();
            for(int j=0; j<maxJ; j++){
                if(dir == Direction.N || dir == Direction.S)
                    list.add(grid[j][i].getTile()); // Vertically
                else
                    list.add(grid[i][j].getTile()); // Horizontally
            }
            if(diffTiles(list))
                actualRepetition++;
            if(actualRepetition >= repetition){
                return getScore();
            }
        }
        return 0;
    }

    /**
     * Check whether all the tiles are different.
     * @param list of tiles contained in one row/column.
     * @return true if all tiles are different.
     */
    private boolean diffTiles(List<Tile> list){
        for(int i=0; i<list.size()-1; i++){
            for(int j=i+1; j<list.size(); j++)
                if(list.get(i).equals(list.get(j)) || list.contains(Tile.BLANK))
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
            case 2 -> {
                System.out.println( "COMMON CARD NUMBER 2:  \n"+
                                    "Two columns each formed by 6 different types of tiles.\n"+
                                    "++++++++++++++++++ \n" +
                                    "+      | ≠ |     + \n" +
                                    "+      | ≠ |     + \n" +
                                    "+      | ≠ | x2  + \n" +
                                    "+      | ≠ |     + \n" +
                                    "+      | ≠ |     + \n" +
                                    "+      | ≠ |     + \n" +
                                    "++++++++++++++++++ \n");
            }
            case 6 -> {
                System.out.println( "       COMMON CARD NUMBER 6:  \n"+
                                    "Two lines each formed by 5 different types of tiles. \n" +
                                    "One line can show the same or a different combination of the other line. \n"+
                                    "++++++++++++++++++++++++++++++++ \n" +
                                    "+   | ≠ | ≠ | ≠ | ≠ | ≠ | x2   + \n" +
                                    "++++++++++++++++++++++++++++++++ \n");
            }
        }
    }
}
