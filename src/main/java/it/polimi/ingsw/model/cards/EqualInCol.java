package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.3,4.
 * </p>
 * Equal tiles in a column
 * @author Nicolo' Gandini
 */
public class EqualInCol extends CommonGoalCard {
    /**
     * Number of equal tiles
     */
    private int eq;

    /**
     * Number of repetition of the pattern
     */
    private int repetition;

    /**
     * Orientation of the check
     */
    private Direction dir;

    /**
     * Initialize repetition, direction and equal tiles based on the card number
     * @param cardNumber number of the card
     */
    public EqualInCol(int cardNumber) {
        super(cardNumber);
        switch (cardNumber) {
            case 3 -> {
                eq = 4;
                repetition = 4;
                //dir = Direction.N;
            }
            case 4 -> {
                eq = 2;
                repetition = 6;
                //dir = Direction.N;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer checkScheme(Player player) {
        int actualRepetition = 0;  // Rappresenta il numero di ripetizioni dello stesso algoritmo. Sulle carte indicate come "x2", "x3"...
        int actualEq = 0;  // Conto il numero di tessere uguali per poter incrementare le ripetizioni.
        final int maxRow;
        int k = 0;
        Cell[][] grid = player.getBookShelf().getGrid();
        Boolean[][] visited = new Boolean[MAXBOOKSHELFROW][MAXBOOKSHELFCOL];

        //maxRow = MAXBOOKSHELFROW - eq + 1;  // Definisco il numero massimo a cui può arrivare la tessera di riferimento, in base al numero di tessere che devo controllare.
        List<Tile> list = new ArrayList<>();
        for(int i=0; i<MAXBOOKSHELFCOL; i++) {
            list.clear();
            k=0;
            while(k<=MAXBOOKSHELFROW-eq) {
                list.clear();
                for(int j=k; j<eq+k; j++){
                    list.add(grid[j][i].getTile());
                }
                if(eqTiles(list))
                    actualRepetition++;
                if(actualRepetition >= repetition){
                    return getScore();
                }
                k++;
            }
        }
        for(int i=0; i<MAXBOOKSHELFROW; i++) {
            list.clear();
            k=0;
            while(k<MAXBOOKSHELFCOL-eq) {
                list.clear();
                for (int j=k; j<eq+k; j++)
                    list.add(grid[i][j].getTile());
                if(eqTiles(list))
                    actualRepetition++;
                if(actualRepetition>=repetition)
                    return getScore();
                k++;
            }
        }
//        maxRow = MAXBOOKSHELFROW - eq + 1;  // Definisco il numero massimo a cui può arrivare la tessera di riferimento, in base al numero di tessere che devo controllare.
//        for(int i = 0; i< MAXBOOKSHELFCOL; i++){
//            for(int j=0; j<maxRow; j++){
//                tileType = grid[j][i].getTile();
//                k=j+1;
//                while(k-j<eq){  // Perchè è un riferimento relativo non assoluto.
//                    nextTileType = grid[k][i].getTile();
//                    if(tileType == nextTileType && tileType != Tile.BLANK)
//                        actualEq++;
//                    k++;
//                }
//                if(actualEq >= eq)
//                    actualRepetition++;
//                if(actualRepetition >= repetition)
//                    return getScore();
//                actualEq = 0;  // Azzero per passare al prossimo gruppo di tessere uguali.
//            }
//        }
        return 0;
    }


    /**
     * Check whether all the tiles are equals.
     * @param list of tiles contained in one row/column.
     * @return true if all tiles are equals.
     */
    private boolean eqTiles(List<Tile> list){
        for(int i=0; i<list.size()-1; i++){
            for(int j=i+1; j<list.size(); j++)
                if(!list.get(i).equals(list.get(j)) || list.contains(Tile.BLANK))
                    return false;
        }
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void printCard(){

    }
}
