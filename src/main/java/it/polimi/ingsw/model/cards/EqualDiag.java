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

    public EqualDiag(int cardNumber) {
        super(cardNumber);
    }

    /**
     * {@inheritDoc}
     */
    public Integer checkScheme(Player player) {
        /*
        Controllo che le celle in diagonale siano uguali e non BLANK. Siccome ci sono due diagonali da controllare
        le controllo entrambe (non è specificato quale controllare).
        Cerco il minimo tra MAXROW e MAXCOL perchè se cambia la dimensione della matrice potrebbe esserci un lato più lungo dell'altro.
         */
        Cell[][] grid = player.getBookShelf().getGrid();
        boolean valid;

        valid = true;
        // Controllo la prima diagonale, quella più sopra (da sx a dx).
        for(int i = 0; i<Math.min(MAXBOOKSHELFROW, MAXBOOKSHELFCOL)-1; i++) {
            if(grid[i][i].getTile() == Tile.BLANK || grid[i][i].getTile() != grid[i+1][i+1].getTile()){
                valid = false;
                break;
            }
        }
        if(valid)
            return getScore();

        // Controllo non andato a buon fine. Controllo la diagonale sotto (da sx a dx).
        valid = true;
        for(int i = 0; i<Math.min(MAXBOOKSHELFROW, MAXBOOKSHELFCOL)-1; i++) {
            if(grid[i+1][i].getTile() == Tile.BLANK || grid[i+1][i].getTile() != grid[i+2][i+1].getTile()) {
                valid = false;
                break;
            }
        }
        if(valid)
            return getScore();

        // Controllo la prima diagonale, quella più sopra (da dx a sx).
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

        // Controllo non andato a buon fine. Controllo la diagonale sotto (da dx a sx).
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
     * {@inheritDoc}
     */
    public void printCard(){
        System.out.println( "    COMMON CARD NUMBER 11 \n"+
                            "++++++++++++++++++++++++++++ \n" +
                            "+  | = |                   + \n" +
                            "+      | = |               + \n" +
                            "+          | = |           + \n" +
                            "+              | = |       + \n" +
                            "+                  | = |   + \n"+
                            "++++++++++++++++++++++++++++ \n");
    }
}
