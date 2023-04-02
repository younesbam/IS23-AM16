package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import static it.polimi.ingsw.model.BookShelf.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.model.BookShelf.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.3,4.
 * </p>
 * Equal tiles in a column
 * @author Nicolo' Gandini
 */
public class EqualInCol extends CommonGoalCard {
    private int eq;
    private int repetition;
    private Direction dir;

    public EqualInCol(int cardNumber) {
        super(cardNumber);
        switch (cardNumber){
            case 3:
                eq = 4;
                repetition = 4;
                dir = Direction.N;
            case 4:
                eq = 2;
                repetition = 6;
                dir = Direction.N;
        }
    }

    /**
     * Check if the player respect the rules to obtain the card's points
     * @param player actual player
     * @param eq number of equal tiles
     * @param repetition number of pattern's repetition
     * @param dir orientation of the check
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     * @author Nicolo' Gandini
     */
    public Integer checkScheme(Player player) {
        int actualRepetition = 0;  // Rappresenta il numero di ripetizioni dello stesso algoritmo. Sulle carte indicate come "x2", "x3"...
        int actualEq = 0;  // Conto il numero di tessere uguali per poter incrementare le ripetizioni.
        final int maxRow;
        int k = 0;
        Cell[][] grid = player.getBookShelf().getGrid();
        Tile tileType;
        Tile nextTileType;

        maxRow = MAXBOOKSHELFROW - eq + 1;  // Definisco il numero massimo a cui può arrivare la tessera di riferimento, in base al numero di tessere che devo controllare.
        for(int i = 0; i< MAXBOOKSHELFCOL; i++){
            for(int j=0; j<maxRow; j++){
                tileType = grid[i][j].getTile();
                k=j+1;
                while(k-j<eq){  // Perchè è un riferimento relativo non assoluto.
                    nextTileType = grid[i][k].getTile();
                    if(tileType == nextTileType && tileType != Tile.BLANK)
                        actualEq++;
                    k++;
                }
                if(actualEq >= eq)
                    actualRepetition++;
                if(actualRepetition >= repetition)
                    return getScore();
                actualEq = 0;  // Azzero per passare al prossimo gruppo di tessere uguali.
            }
        }
        return 0;
    }
}
