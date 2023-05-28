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
        Boolean[][] registeredGrid = new Boolean[MAXBOOKSHELFROW][MAXBOOKSHELFCOL];
        boolean cellAlreadyRegistered = false;

        for(int i=0; i<MAXBOOKSHELFROW; i++)
            for(int j=0; j<MAXBOOKSHELFCOL; j++)
                registeredGrid[i][j] = false;

        //maxRow = MAXBOOKSHELFROW - eq + 1;  // Definisco il numero massimo a cui può arrivare la tessera di riferimento, in base al numero di tessere che devo controllare.
        List<Tile> list = new ArrayList<>();
        /*
        Questo ciclo controlla in verticale se trova dei pattern; quando trova un pattern, segna le celle come visitate in modo da non fare il controllo 2 volte.
         */
        for(int i=0; i<MAXBOOKSHELFCOL; i++) {
            list.clear();
            k=0;
            // Scorro le n tessere richieste dalla carta.
            while(k<=MAXBOOKSHELFROW-eq) {
                list.clear();
                cellAlreadyRegistered = false;
                // Aggiungo le tessere ad una lista per controllare che siano uguali.
                for(int j=k; j<eq+k; j++)
                    list.add(grid[j][i].getTile());
                // Se le tessere sono uguali, se il pattern non è già stato registrato lo registro e lo conto come pattern trovato.
                if(eqTiles(list)){
                    for(int j=k; j<eq+k; j++){
                        if(registeredGrid[j][i]){
                            cellAlreadyRegistered = true;
                            break;
                        }
                    }
                    // Se non avevo già contato il pattern come trovato, lo conto e segno le celle come già registrate.
                    if (!cellAlreadyRegistered){
                        for(int j=k; j<eq+k; j++)
                            registeredGrid[j][i] = true;
                        actualRepetition++;
                    }
                }

                if(actualRepetition >= repetition){
                    return getScore();
                }
                k++;
            }
        }
        /*
        Questo ciclo controlla in orizzontale se trova dei pattern; quando trova un pattern, segna le celle come visitate in modo da non fare il controllo 2 volte.
         */
        for(int i=0; i<MAXBOOKSHELFROW; i++) {
            list.clear();
            k=0;
            while(k<=MAXBOOKSHELFCOL-eq) {
                list.clear();
                cellAlreadyRegistered = false;
                for (int j=k; j<eq+k; j++)
                    list.add(grid[i][j].getTile());
                if(eqTiles(list)) {
                    for (int j=k; j<eq+k; j++) {
                        if(registeredGrid[i][j]){
                            cellAlreadyRegistered = true;
                            break;
                        }
                    }
                    if(!cellAlreadyRegistered){
                        for (int j=k; j<eq+k; j++)
                            registeredGrid[i][j] = true;
                        actualRepetition++;
                    }
                }
                if(actualRepetition>=repetition)
                    return getScore();
                k++;
            }
        }
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
        switch (cardNumber) {
            case 3 -> {
                System.out.println( "COMMON CARD NUMBER 3 \n" +
                                    "++++++++++++++++++++ \n"+
                                    "+      | = |       + \n" +
                                    "+      | = | x4    + \n" +
                                    "+      | = |       + \n" +
                                    "+      | = |       + \n"+
                                    "++++++++++++++++++++ \n");
            }
            case 4 -> {
                System.out.println( "COMMON CARD NUMBER 4  \n" +
                                    "+++++++++++++++++++++ \n"+
                                    "+       | = |       + \n" +
                                    "+       | = |  x2   + \n"+
                                    "+++++++++++++++++++++ \n");
            }
        }
    }
}
