package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.8.
 * </p>
 * Equal tiles in corners.
 * @author Nicolo' Gandini
 */
public class EqualCorners extends CommonGoalCard {

    /**
     * Constructor.
     * @param cardNumber
     */
    public EqualCorners(int cardNumber) {
        super(cardNumber);
    }

    /**
     * Checks whether the scheme is valid.
     * @param player actual player
     * @return points earned from the player.
     */
    public Integer checkScheme(Player player) {
        Cell[][] grid = player.getBookShelf().getGrid();

        // Check if the tiles in the 4 corners are the same and if they are not blank.
        if(grid[0][0].getTile() == grid[0][MAXBOOKSHELFCOL-1].getTile() &&
                grid[0][0].getTile() == grid[MAXBOOKSHELFROW-1][MAXBOOKSHELFCOL-1].getTile() &&
                grid[0][0].getTile() == grid[MAXBOOKSHELFROW-1][0].getTile() &&
                grid[0][0].getTile() != Tile.BLANK){
            return getScore();
        }
        return 0;
    }


    /**
     * Prints the card on the CLI.
     * {@inheritDoc}
     */
    public void printCard(){
        System.out.println( "     COMMON CARD NUMBER 8: \n"+
                            "Four tiles of the same type in the four corners of the bookshelf. \n" +
                            "+++++++++++++++++++++++++++++ \n"   +
                            "+   | = |   |   |   | = |   + \n" +
                            "+   |   |   |   |   |   |   + \n" +
                            "+   |   |   |   |   |   |   + \n" +
                            "+   |   |   |   |   |   |   + \n" +
                            "+   |   |   |   |   |   |   + \n" +
                            "+   | = |   |   |   | = |   + \n" +
                            "+++++++++++++++++++++++++++++ \n");
    }
}
