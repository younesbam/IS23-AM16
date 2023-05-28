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
 * @author Nicolo' Gandini
 */
public class SubMatrix extends CommonGoalCard {

    public SubMatrix(int cardNumber) {
        super(cardNumber);
    }

    /**
     * {@inheritDoc}
     */
    public Integer checkScheme(Player player) {
//        /*
//        Creo una variabile k che incrementa ad ogni riga che scorro(al contrario, dal basso verso l'alto).
//        In tal modo riesco a sottrarre al numero delle colonne da controllare, fino ad arrivare a 1 cella da controllare
//        in alto. In questo caso poi esco dal ciclo.
//         */

//        int k = 0;
//        for(int j = MAXBOOKSHELFROW -1; j>=0; j--) {
//            for(int i = MAXBOOKSHELFCOL -1-k; i>=0; i--){
//                if(grid[j][i].getTile() == Tile.BLANK)
//                    return 0;
//            }
//            k++;
//            if(k>= MAXBOOKSHELFCOL -1)
//                return 0;
//        }
//        return getScore();
        Cell[][] grid = player.getBookShelf().getGrid();
        int numOfBlank;
        boolean valid;
        List<Tile> lastRow = new ArrayList<>();

        // Questo ciclo controlla la sottomatrice decrescente che parte dalla riga 5 (è piena solo la riga più in basso).
        valid = true;
        numOfBlank = 0;
        for(int i=MAXBOOKSHELFROW-1; i>=0; i--){
            if(!checkRowDecrescente(grid, i, numOfBlank)){
                valid = false;
                break;
            }
            numOfBlank++;
        }
        if(valid)
            return getScore();

        // Questo ciclo controlla la sottomatrice decrescente che ha le righe 4 e 5 piene.
        valid = true;
        numOfBlank = 0;
        for(int i=0; i<MAXBOOKSHELFCOL; i++)
            lastRow.add(grid[MAXBOOKSHELFROW-1][i].getTile());

        if(!lastRow.contains(Tile.BLANK)){
            for(int i=MAXBOOKSHELFROW-2; i>=0; i--){
                if(!checkRowDecrescente(grid, i, numOfBlank)){
                    valid = false;
                    break;
                }
                numOfBlank++;
            }
        } else
            valid = false;
        if(valid)
            return getScore();


        // Questo ciclo controlla la sottomatrice crescente che parte dalla riga 5 (è piena solo la riga più in basso).
        valid = true;
        numOfBlank = 0;
        for(int i=MAXBOOKSHELFROW-1; i>=0; i--){
            if(!checkRowCrescente(grid, i, numOfBlank)){
                valid = false;
                break;
            }
            numOfBlank++;
        }
        if(valid)
            return getScore();

        // Questo ciclo controlla la sottomatrice crescente con le righe 4 e 5 piene.
        valid = true;
        numOfBlank = 0;
        lastRow.clear();
        for(int i=0; i<MAXBOOKSHELFCOL; i++)
            lastRow.add(grid[MAXBOOKSHELFROW-1][i].getTile());

        if(!lastRow.contains(Tile.BLANK)){
            for(int i=MAXBOOKSHELFROW-2; i>=0; i--){
                if(!checkRowCrescente(grid, i, numOfBlank)){
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
     * Questo metodo controlla se la riga è riempita in modo corretto verifcando la matrice decrescente.
     * @param grid
     * @param row
     * @param numOfBlank
     * @return
     */
    private boolean checkRowDecrescente(Cell[][] grid, int row, int numOfBlank){
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
     * Questo metodo controlla se la riga è riempita in modo corretto verificando la matrice crescente.
     * @param grid
     * @param row
     * @param numOfBlank
     * @return
     */
    private boolean checkRowCrescente(Cell[][] grid, int row, int numOfBlank){
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
     * {@inheritDoc}
     */
    public void printCard(){
        System.out.println( "    COMMON CARD NUMBER 12 \n" +
                            "++++++++++++++++++++++++++++ \n"+
                            "+  |   |                   + \n" +
                            "+  |   |   |               + \n" +
                            "+  |   |   |   |           + \n" +
                            "+  |   |   |   |   |       + \n" +
                            "+  |   |   |   |   |   |   + \n"+
                            "++++++++++++++++++++++++++++ \n");
    }
}
