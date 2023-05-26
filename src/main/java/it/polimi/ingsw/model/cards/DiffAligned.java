package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.List;

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
        switch (cardNumber) {
            case 2 -> {
                repetition = 2;
                dir = Direction.N;
            }
            case 6 -> {
                repetition = 2;
                dir = Direction.E;
            }
            default -> {
                repetition = -1;
                dir = Direction.N;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Integer checkScheme(Player player) {
        int actualRepetition = 0;  // Rappresenta il numero di ripetizioni dello stesso algoritmo. Sulle carte indicate come "x2", "x3"...
        final int maxI;
        final int maxJ;
        Cell[][] grid = player.getBookShelf().getGrid();

        /*
        Le variabili maxI e maxJ servono per dare un limite alla tabella. La variabile j è quella che incrementa sempre
        per controllare se le tessere sono uguali/deverse o no. La variabile i è quella che fa spostare di riga/colonna.
        Se devo controllare in verticale che le tessere siano diverse devo limitare la variaible j come MAXROW-1.
        Se devo controllare in orizzontale che le tessere siano diverse devo limitare la variaible j come MAXCOL-1.
         */
        if(dir == Direction.N || dir == Direction.S){
            maxJ = MAXBOOKSHELFROW;
            maxI = MAXBOOKSHELFCOL;
        } else {
            maxJ = MAXBOOKSHELFCOL;
            maxI = MAXBOOKSHELFROW;
        }

        List<Tile> list = new ArrayList<>();
        for(int i=0; i<maxI; i++){
            list.clear();
            for(int j=0; j<maxJ; j++){
                if(dir == Direction.N || dir == Direction.S)
                    list.add(grid[j][i].getTile()); // verticale
                else
                    list.add(grid[i][j].getTile()); // orizzontale
            }
            if(diffTiles(list))
                actualRepetition++;
            if(actualRepetition >= repetition){
                return getScore();
            }
        }
        return 0;
    }

    /**
     * Check whether all the tiles are different.
     * @param list of tiles contained in one row/column.
     * @return true if all tiles are different.
     */
    private boolean diffTiles(List<Tile> list){
        for(int i=0; i<list.size()-1; i++){
            for(int j=i+1; j<list.size(); j++)
                if(list.get(i).equals(list.get(j)) || list.contains(Tile.BLANK))
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
