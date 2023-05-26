package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.1,10.
 * </p>
 * Equal tiles in a diagonal orientation
 * @author Nicolo' Gandini
 */
public class EqualCross extends CommonGoalCard {
    /**
     * Repetition of the pattern
     */
    private int repetition;

    /**
     * Measure [cells] of the square side
     */
    private int squareSide;

    /**
     * Initialize repetition and square side based on the card number
     * @param cardNumber number of the card
     */
    public EqualCross(int cardNumber) {
        super(cardNumber);
        switch(cardNumber){
            case 1:
                repetition = 2;
                squareSide = 2;
            case 2:
                repetition = 1;
                squareSide = 3;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer checkScheme(Player player) {
        int actualRepetition = 0;  // Rappresenta il numero di ripetizioni dello stesso algoritmo. Sulle carte indicate come "x2", "x3"...
        Cell[][] grid = player.getBookShelf().getGrid();
        List<Tile> firstDiag = new ArrayList<>();  // Diagonale principale
        List<Tile> secDiag = new ArrayList<>();  // Diagonale secondaria.
        Tile ref;  // Tessera di riferimento
        int k=0;  // Contatore per prendere le tessere sulla prima diagonale.

        /*
        Uso il lato del quadrato per porre un limite alla tessera di riferimento. In questo caso è sempre quella in alto a sx per la prima diagonale,
        e in alto a dx per la seconda diagonale. Si andrà poi a scendere (++ dell'indice) di riga in entrambe le diagonali e rispettivamente:
        per la prima diagonale aumentare di colonna, per la seconda diagonale diminuire.
         */
        for (int j = 0; j< MAXBOOKSHELFROW-squareSide+1; j++) {
            for(int i = 0; i< MAXBOOKSHELFCOL-squareSide+1; i++){
                firstDiag.clear();
                secDiag.clear();
                k=0;
                /*
                Aggiungo le tessere sulla prima e seconda diagonale. Non mi interessa delle ripetizioni,
                tanto devo controllare che siano tutte uguali, non quante.
                 */
                ref = grid[j][i].getTile();
                while(k<=squareSide){
                    firstDiag.add(grid[j+k][i+k].getTile());
                    secDiag.add(grid[j+k][i+(squareSide-1)-k].getTile());  // Tessera di riferimento di coordinare i+lato del quadrato
                    k++;
                }
                /*
                Controllo se contiene qualche tessera di tipo BLANK; in tal caso, discard. Controllo che le due liste siano uguali.
                 */
                if(Utils.equalLists(firstDiag, secDiag) && !firstDiag.contains(Tile.BLANK) && !secDiag.contains(Tile.BLANK))
                    actualRepetition++;
                if(actualRepetition>=repetition)
                    return getScore();
            }
        }
        return 0;
    }


    /**
     * {@inheritDoc}
     */
    public void printCard(){

    }
}
