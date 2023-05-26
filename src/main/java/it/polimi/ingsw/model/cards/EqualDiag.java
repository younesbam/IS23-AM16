package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

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

        boolean retry = false;
        // Controllo la prima diagonale, quella più sopra.
        for(int i = 0; i<Math.min(MAXBOOKSHELFROW, MAXBOOKSHELFCOL)-1; i++) {
            if(grid[i][i].getTile() == Tile.BLANK || grid[i][i].getTile() != grid[i+1][i+1].getTile()){
                retry = true;
                break;
            }
        }
        // Controllo non andato a buon fine. Controllo al diagonale sotto
        if(retry){
            for(int i = 0; i<Math.min(MAXBOOKSHELFROW, MAXBOOKSHELFCOL)-1; i++) {
                if(grid[i+1][i].getTile() == Tile.BLANK || grid[i+1][i].getTile() != grid[i+2][i+1].getTile())
                    return 0;
            }
        }
        return getScore();
    }


    /**
     * {@inheritDoc}
     */
    public void printCard(){

    }
}
