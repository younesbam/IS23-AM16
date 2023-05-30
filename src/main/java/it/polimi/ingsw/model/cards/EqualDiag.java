package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.sql.SQLOutput;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.11.
 * </p>
 * Equal tiles on the diagonal.
 * <p>
 *     <b>Note:</b>
 *     there are two diagonals to check. Based on requirements, no preferred diagonal to check is specified.
 * </p>
 *
 * @author Nicolo' Gandini
 */
public class EqualDiag extends CommonGoalCard {

    /**
     * Constructor.
     * @param cardNumber
     */
    public EqualDiag(int cardNumber) {
        super(cardNumber);
    }

    /**
     * Checks whether the scheme is valid.
     * @param player actual player
     * @return points earned from the player.
     */
    public Integer checkScheme(Player player) {
        /*
        Check if the cells on the diagonal are the same and not blank. Since there are 2 diagonals to check, we check
        them both.
        We look for the minimum between MAXROW and MAXCOL because if the grid changes, there could be one side longer
        than the other.
         */
        Cell[][] grid = player.getBookShelf().getGrid();
        boolean valid;

        // Check of the first diagonal, starting from the top left corner (moving from L to R).
        valid = true;
        for(int i = 0; i<Math.min(MAXBOOKSHELFROW, MAXBOOKSHELFCOL)-1; i++) {
            if(grid[i][i].getTile() == Tile.BLANK || grid[i][i].getTile() != grid[i+1][i+1].getTile()){
                valid = false;
                break;
            }
        }
        if(valid)
            return getScore();

        // Invalid check. Check of the other diagonal, starting from cell [1,0] (moving from L to R).
        valid = true;
        for(int i = 0; i<Math.min(MAXBOOKSHELFROW, MAXBOOKSHELFCOL)-1; i++) {
            if(grid[i+1][i].getTile() == Tile.BLANK || grid[i+1][i].getTile() != grid[i+2][i+1].getTile()) {
                valid = false;
                break;
            }
        }
        if(valid)
            return getScore();

        // Check of the first diagonal, starting from the top right corner (moving from R to L).
        valid = true;
        int j=0;
        for(int i=MAXBOOKSHELFCOL-1; i>0; i--) {
            if(grid[j][i].getTile() == Tile.BLANK || grid[j][i].getTile() != grid[j+1][i-1].getTile()) {
                valid = false;
                break;
            }
            j++;
        }
        if(valid)
            return getScore();

        // Invalid check. Check of the other diagonal, starting from cell [1, MAXCOL-1] (moving from R to L).
        valid = true;
        j=1;
        for(int i=MAXBOOKSHELFCOL-1; i>0; i--) {
            if(grid[j][i].getTile() == Tile.BLANK || grid[j][i].getTile() != grid[j+1][i-1].getTile()) {
                valid = false;
                break;
            }
            j++;
        }
        if(valid)
            return getScore();

        return 0;
    }


    /**
     * Prints the card on the CLI.
     * {@inheritDoc}
     */
    public void printCard(){
        System.out.println( "    COMMON CARD NUMBER 11: \n"+
                            "Five tiles of the same type forming a diagonal. \n" +
                            "++++++++++++++++++++++++++++ \n" +
                            "+  | = |                   + \n" +
                            "+      | = |               + \n" +
                            "+          | = |           + \n" +
                            "+              | = |       + \n" +
                            "+                  | = |   + \n"+
                            "++++++++++++++++++++++++++++ \n");
    }
}
