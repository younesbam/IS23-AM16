package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.9.
 * </p>
 * Eight tiles in random order must me the same.
 * @author Nicolo' Gandini
 */
public class EqualRand extends CommonGoalCard {
    /**
     * Number of tiles that must me the same, in a random order
     */
    int eq;

    /**
     * Initialize number of equal tiles based on the card number
     * @param cardNumber number of the card
     */
    public EqualRand(int cardNumber) {
        super(cardNumber);
        eq = 8;
    }

    /**
     * {@inheritDoc}
     */
    public Integer checkScheme(Player player) {
        Cell[][] grid = player.getBookShelf().getGrid();
        List<Tile> list = new ArrayList<>();  //Creo una lista per cercare le occorrenze

        for(int j = 0; j< MAXBOOKSHELFROW; j++) {
            for (int i = 0; i< MAXBOOKSHELFCOL; i++) {
                list.add(grid[j][i].getTile());
            }
        }
        /*
        Itero tutti i tipi delle tessere per cercare la frequenza con cui compaiono nella lista.
         */
        for(Tile type : Tile.values()){
            if(type != Tile.BLANK) {
                int occurrences = Collections.frequency(list, type);
                if (occurrences >= eq)
                    return getScore();
            }
        }
        return 0;
    }


    /**
     * {@inheritDoc}
     */
    public void printCard(){
        System.out.println( "COMMON CARD NUMBER 9 \n" +
                            "++++++++++++++++++++++++++++ \n"+
                            "+      | = |   | = |       + \n" +
                            "+  | = |   | = |   | = |   + \n" +
                            "+  | = |   | = |   | = |   + \n" +
                            "++++++++++++++++++++++++++++");
    }
}
