package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.9.
 * </p>
 * Eight tiles in random order must me the same.
 */
public class EqualRand extends CommonGoalCard {
    /**
     * Number of tiles that must be the same, in a random order
     */
    int eq;

    /**
     * Initialize number of equal tiles based on the card number
     * @param cardNumber number of the card
     */
    public EqualRand(int cardNumber) {
        super(cardNumber);
        eq = 8;
    }

    /**
     * Checks whether the scheme is valid.
     * @param player actual player
     * @return points earned from the player.
     */
    public Integer checkScheme(Player player) {
        Cell[][] grid = player.getBookShelf().getGrid();
        List<Tile> list = new ArrayList<>(); // List to look for occurrences.

        for(int j = 0; j< MAXBOOKSHELFROW; j++) {
            for (int i = 0; i< MAXBOOKSHELFCOL; i++) {
                list.add(grid[j][i].getTile());
            }
        }

        // Iteration on tile's type to find how many times they are found on the list.
        for(Tile type : Tile.values()){
            if(type != Tile.BLANK) {
                int occurrences = Collections.frequency(list, type);
                if (occurrences >= eq)
                    return getScore();
            }
        }
        return 0;
    }


    /**
     * Prints the card on the CLI.
     * {@inheritDoc}
     */
    public void printCard(){
        System.out.println( "COMMON CARD NUMBER 9: \n" +
                            "Eight tiles of the same type. \n" +
                            "There’s no restriction about the position of these tiles.\n" +
                            "++++++++++++++++++++++++++++ \n"+
                            "+      | = |   | = |       + \n" +
                            "+  | = |   | = |   | = |   + \n" +
                            "+  | = |   | = |   | = |   + \n" +
                            "++++++++++++++++++++++++++++");
    }
}
