package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.12.
 * </p>
 * All the cells below the main diagonal must be full and not BLANK.
 */
public class SubMatrix extends CommonGoalCard {

    public SubMatrix(int cardNumber) {
        super(cardNumber);
    }

    /**
     * Checks whether the scheme is valid.
     * @param player actual player
     * @return points earned from the player.
     */
    public Integer checkScheme(Player player) {
        Cell[][] grid = player.getBookShelf().getGrid();
        int numOfBlank;
        boolean valid;
        List<Tile> lastRow = new ArrayList<>();

        // This cycle checks decreasing submatrix starting from row 5.
        valid = true;
        numOfBlank = 0;
        for(int i=MAXBOOKSHELFROW-1; i>=0; i--){
            if(!checkDecreasingRow(grid, i, numOfBlank)){
                valid = false;
                break;
            }
            numOfBlank++;
        }
        if(valid)
            return getScore();

        // This cycle checks decreasing submatrix with both rows 4 and 5 filled.
        valid = true;
        numOfBlank = 0;
        for(int i=0; i<MAXBOOKSHELFCOL; i++)
            lastRow.add(grid[MAXBOOKSHELFROW-1][i].getTile());

        if(!lastRow.contains(Tile.BLANK)){
            for(int i=MAXBOOKSHELFROW-2; i>=0; i--){
                if(!checkDecreasingRow(grid, i, numOfBlank)){
                    valid = false;
                    break;
                }
                numOfBlank++;
            }
        } else
            valid = false;
        if(valid)
            return getScore();


        // This cycle checks increasing submatrix starting from row 5.
        valid = true;
        numOfBlank = 0;
        for(int i=MAXBOOKSHELFROW-1; i>=0; i--){
            if(!checkIncreasingRow(grid, i, numOfBlank)){
                valid = false;
                break;
            }
            numOfBlank++;
        }
        if(valid)
            return getScore();

        // This cycle checks increasing submatrix with both rows 4 and 5 filled.
        valid = true;
        numOfBlank = 0;
        lastRow.clear();
        for(int i=0; i<MAXBOOKSHELFCOL; i++)
            lastRow.add(grid[MAXBOOKSHELFROW-1][i].getTile());

        if(!lastRow.contains(Tile.BLANK)){
            for(int i=MAXBOOKSHELFROW-2; i>=0; i--){
                if(!checkIncreasingRow(grid, i, numOfBlank)){
                    valid = false;
                    break;
                }
                numOfBlank++;
            }
        } else
            valid = false;
        if(valid)
            return getScore();

        return 0;
    }


    /**
     * Checks whether the row is filled correctly in order to verify increasing submatrix.
     * @param grid player's bookshelf
     * @param row row to check
     * @param numOfBlank number of expected blanks
     * @return true if the row is filled correctly.
     */
    private boolean checkDecreasingRow(Cell[][] grid, int row, int numOfBlank){
        for(int i=0; i<MAXBOOKSHELFCOL-numOfBlank; i++){
            if(grid[row][i].getTile() == Tile.BLANK){
                return false;
            }
        }
        for(int i=MAXBOOKSHELFCOL-1; i>=MAXBOOKSHELFCOL-numOfBlank; i--){
            if(grid[row][i].getTile() != Tile.BLANK){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the row is filled correctly in order to verify increasing submatrix.
     * @param grid player's bookshelf
     * @param row row to check
     * @param numOfBlank number of blanks to check
     * @return true if the row respects the pattern.
     */
    private boolean checkIncreasingRow(Cell[][] grid, int row, int numOfBlank){
        for(int i=MAXBOOKSHELFCOL-1; i>=numOfBlank; i--){
            if(grid[row][i].getTile() == Tile.BLANK){
                return false;
            }
        }
        for(int i=0; i<numOfBlank; i++){
            if(grid[row][i].getTile() != Tile.BLANK){
                return false;
            }
        }
        return true;
    }


    /**
     * Prints the card on the CLI.
     * {@inheritDoc}
     */
    public void printCard(){
        System.out.println( "COMMON CARD NUMBER 12 \n" +
                            "Five columns of increasing or decreasing height.\n" +
                            "Starting from the first column on the left or on the right, each next column must \n" +
                            "be made of exactly one more tile. \n" +
                            "Tiles can be of any type. \n"+
                            "++++++++++++++++++++++++++++ \n"+
                            "+  |   |                   + \n" +
                            "+  |   |   |               + \n" +
                            "+  |   |   |   |           + \n" +
                            "+  |   |   |   |   |       + \n" +
                            "+  |   |   |   |   |   |   + \n"+
                            "++++++++++++++++++++++++++++ \n");
    }
}
