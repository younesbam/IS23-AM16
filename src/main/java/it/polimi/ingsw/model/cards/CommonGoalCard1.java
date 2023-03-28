package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *     Represent card n.1,10.
 * </p>
 * Equal tiles in a diagonal orientation
 * @author Nicolo' Gandini
 */
public class CommonGoalCard1 extends CommonGoalCard {
    private int repetition;
    private int squareSide;

    public CommonGoalCard1(int playerNum, int cardNumber) {
        super(playerNum, cardNumber);
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
     * Check if the player respect the rules to obtain the card's points
     * @param player actual player
     * @param repetition number of pattern's repetition
     * @param squareSide measure [cells] of the square side
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     * @author Nicolo' Gandini
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
        for (int j=0; j<MAXROW-squareSide+1; j++) {
            for(int i=0; i<MAXCOL-squareSide+1; i++){
                firstDiag.clear();
                secDiag.clear();
                k=0;
                /*
                Aggiungo le tessere sulla prima e seconda diagonale. Non mi interessa delle ripetizioni,
                tanto devo controllare che siano tutte uguali, non quante.
                 */
                ref = grid[i][j].getTile();
                while(k<=squareSide){
                    firstDiag.add(grid[i+k][j+k].getTile());
                    secDiag.add(grid[i+(squareSide-1)-k][j+k].getTile());  // Tessera di riferimento di coordinare i+lato del quadrato
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
}
