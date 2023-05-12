package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.2,6.
 * </p>
 * Different cards in a row/column
 * @author Nicolo' Gandini
 */
public class DiffAligned extends CommonGoalCard {
    /**
     * Repetition of the pattern
     */
    private int repetition;

    /**
     * Direction
     */
    private Direction dir;

    /**
     * Initialize repetition and direction based on the card number
     * @param cardNumber number of the card
     */
    public DiffAligned(int cardNumber) {
        super(cardNumber);
        switch(cardNumber){
            case 2:
                repetition = 2;
                dir = Direction.N;
            case 6:
                repetition = 2;
                dir = Direction.E;
            default:
                repetition = -1;
                dir = Direction.N;
        }
    }

    /**
     * Check if the player respect the rules to obtain the card's points
     * @param player actual player
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     */
    public Integer checkScheme(Player player) {
        int actualRepetition = 0;  // Rappresenta il numero di ripetizioni dello stesso algoritmo. Sulle carte indicate come "x2", "x3"...
        int k = 0;  // Variabile incrementale che controlla le tessere sulla stessa riga/colonna.
        final int maxI;
        final int maxJ;
        Cell[][] grid = player.getBookShelf().getGrid();
        Tile tileType;
        Tile nextTileType;

        /*
        Le variabili maxI e maxJ servono per dare un limite alla tabella. La variabile j è quella che incrementa sempre
        per controllare se le tessere sono uguali/deverse o no. La variabile i è quella che fa spostare di riga/colonna.
        Se devo controllare in verticale che le tessere siano diverse devo limitare la variaible j come MAXROW-1.
        Se devo controllare in orizzontale che le tessere siano diverse devo limitare la variaible j come MAXCOL-1.
         */
        if(dir == Direction.N || dir == Direction.S){
            maxJ = MAXBOOKSHELFROW -1;
            maxI = MAXBOOKSHELFCOL;
        } else {
            maxJ = MAXBOOKSHELFCOL -1;
            maxI = MAXBOOKSHELFROW;
        }
        for(int i=0; i<maxI; i++){
            for(int j=0; j<maxJ; j++){
                /*
                Devo invertire la variabile j per i controlli, in base a se sto controllando sulla riga o colonna.
                 */
                if(dir == Direction.N || dir == Direction.S)
                    tileType = grid[j][i].getTile();
                else
                    tileType = grid[i][j].getTile();
                k = j+1;
                /*
                Uso la variaible k per controllare le tessere successive alla tessera di riferimento (controllata da j)
                 */
                while(k<maxJ){
                    if(dir == Direction.N || dir == Direction.S)
                        nextTileType = grid[k][i].getTile();
                    else
                        nextTileType = grid[i][k].getTile();

                    if(tileType == nextTileType || tileType== Tile.BLANK || nextTileType== Tile.BLANK)
                        break;
                    /*
                    Se le tessere sono tutte diverse, vuol dire che sono arrivato in fondo, sia con j che con k.
                    j arriverà alla riga o colonna massima -1, k invece deve arrivare in fondo.
                     */
                    if(j==maxJ-2 && k==maxJ-1)
                        actualRepetition++;
                    k++;
                }
            }
            if(actualRepetition >= repetition){
                return getScore();
            }
        }
        return 0;
    }
}
