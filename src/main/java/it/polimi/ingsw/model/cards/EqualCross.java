package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        switch (cardNumber) {
            case 1 -> {
                repetition = 2;
                squareSide = 2;
            }
            case 10 -> {
                repetition = 1;
                squareSide = 3;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer checkScheme(Player player) {
        //int actualRepetition = 0;  // Rappresenta il numero di ripetizioni dello stesso algoritmo. Sulle carte indicate come "x2", "x3"...
        Cell[][] grid = player.getBookShelf().getGrid();
        List<Tile> firstDiag = new ArrayList<>();  // Diagonale principale
        List<Tile> secDiag = new ArrayList<>();  // Diagonale secondaria.
        Map<Tile, Integer> map = new HashMap<>();  // Mappa che tiene traccia della tessera e di quanti match ha trovato.
        Tile ref;  // Tessera di riferimento
        Boolean[][] registeredGrid = new Boolean[MAXBOOKSHELFROW][MAXBOOKSHELFCOL];
        boolean cellAlreadyRegistered = false;


        for(int i=0; i<MAXBOOKSHELFROW; i++)
            for(int j=0; j<MAXBOOKSHELFCOL; j++)
                registeredGrid[i][j] = false;


        /*
         Viene creata una mappa con tutte le tiles possibili. Una volta che viene creato un quadrato,
         il valore viene incrementato. Quando trovo 2 quadrati con gli stessi valori all'interno ho soddisfatto il goal.
         */

        for (Tile tile : Tile.values()) {
            if (tile == Tile.BLANK || tile == Tile.UNAVAILABLE)
                map.put(tile, -1);
            else
                map.put(tile, 0);
        }
        /*
        Uso il lato del quadrato per porre un limite alla tessera di riferimento. In questo caso è sempre quella in alto a sx per la prima diagonale,
        e in alto a dx per la seconda diagonale. Si andrà poi a scendere (++ dell'indice) di riga in entrambe le diagonali e rispettivamente:
        per la prima diagonale aumentare di colonna, per la seconda diagonale diminuire.
         */
        for (int j = 0; j< MAXBOOKSHELFROW-squareSide+1; j++) {
            for(int i = 0; i< MAXBOOKSHELFCOL-squareSide+1; i++){
                firstDiag.clear();
                secDiag.clear();
                cellAlreadyRegistered = false;
                /*
                Aggiungo le tessere sulla prima e seconda diagonale. Non mi interessa delle ripetizioni,
                tanto devo controllare che siano tutte uguali, non quante.
                 */
                ref = grid[j][i].getTile();
                for(int k=0; k<squareSide; k++){
                    firstDiag.add(grid[j+k][i+k].getTile());
                    secDiag.add(grid[j+k][i+(squareSide-1)-k].getTile());  // Tessera di riferimento di coordinare i+lato del quadrato
                }
                //Controllo se contiene qualche tessera di tipo BLANK; in tal caso, discard. Controllo che le due liste siano uguali.
                if(eqDiags(firstDiag, secDiag)){
                    for(int k=0; k<squareSide; k++){
                        if(registeredGrid[j+k][i+k]) {
                            cellAlreadyRegistered = true;
                            break;
                        }
                    }
                    if (!cellAlreadyRegistered){
                        for(int k=0; k<squareSide; k++){
                            registeredGrid[j+k][i+k] = true;
                            registeredGrid[j+k][i+(squareSide-1)-k] = true;
                        }
                        map.replace(ref, map.get(ref)+1);
                    }

                }

                if(map.get(ref)>=repetition)
                    return getScore();
            }
        }
        return 0;
    }

    /**
     * This method checks whether the two diagonals contain both the same tile.
     * @param l1 first diagonal.
     * @param l2 second diagonal.
     * @return true if diagonals contain the same tile.
     */
    private boolean eqDiags(List<Tile> l1, List<Tile> l2) {
        Tile eqTile = l1.get(0);
        for(int n=0; n<l1.size(); n++){
            if(l1.contains(Tile.BLANK) || l2.contains(Tile.BLANK) ||
                    !l1.get(n).equals(eqTile) ||
                    !l2.get(n).equals(eqTile)) {
                return false;
            }
        }
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void printCard(){
        switch (cardNumber) {
            case 1 -> {
                System.out.println( "   COMMON CARD NUMBER 1 \n"+
                                    "++++++++++++++++++++++++ \n" +
                                    "+      | = | = |       + \n" +
                                    "+      | = | = |  x2   + \n"+
                                    "++++++++++++++++++++++++ \n");
            }
            case 10 -> {
                System.out.println( "COMMON CARD NUMBER 10 \n"+
                                    "+++++++++++++++++++++ \n" +
                                    "+   | = |   | = |   + \n" +
                                    "+   |   | = |   |   + \n" +
                                    "+   | = |   | = |   + \n"+
                                    "+++++++++++++++++++++ \n");
            }
        }
    }
}
