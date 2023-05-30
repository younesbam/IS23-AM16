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
     * Checks whether the scheme is valid.
     * @param player actual player
     * @return points earned from the player.
     */
    public Integer checkScheme(Player player) {
        Cell[][] grid = player.getBookShelf().getGrid();
        List<Tile> firstDiag = new ArrayList<>();  // Principal diagonal.
        List<Tile> secDiag = new ArrayList<>();  // Secondary diagonal.
        Map<Tile, Integer> map = new HashMap<>();  // Keeps track of tile and corresponding number of matches.
        Tile ref;  // Reference tile.
        Boolean[][] registeredGrid = new Boolean[MAXBOOKSHELFROW][MAXBOOKSHELFCOL];
        boolean cellAlreadyRegistered = false;


        for(int i=0; i<MAXBOOKSHELFROW; i++)
            for(int j=0; j<MAXBOOKSHELFCOL; j++)
                registeredGrid[i][j] = false;


        /*
        Creation of the map with all possible values for the tiles. Every time a square is found, the key is increased.
        When 2 squares with the same values are found, the goal is reached.
         */
        for (Tile tile : Tile.values()) {
            if (tile == Tile.BLANK || tile == Tile.UNAVAILABLE)
                map.put(tile, -1);
            else
                map.put(tile, 0);
        }

        /*
        The square side is used to limit the reference tile. As reference tile, we use the one in the top left corner
        for the first diagonal and the one in the top right corner for the second diagonal.
        Move forward in the first diagonal: ++ row, ++ column.
        Move forward in the second diagonal: ++ row, -- column.
         */
        for (int j = 0; j< MAXBOOKSHELFROW-squareSide+1; j++) {
            for(int i = 0; i< MAXBOOKSHELFCOL-squareSide+1; i++){
                firstDiag.clear();
                secDiag.clear();
                cellAlreadyRegistered = false;

                /*
                Tiles are added on the diagonals. We don't care about how many equal tiles are there, we just
                want them to be the equals.
                 */
                ref = grid[j][i].getTile();
                for(int k=0; k<squareSide; k++){
                    firstDiag.add(grid[j+k][i+k].getTile());
                    secDiag.add(grid[j+k][i+(squareSide-1)-k].getTile()); // Reference tile. Coordinates: i+squareSide.
                }

                // Check if there are blank tiles: if so, discard. Check that lists are equals.
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
     * Prints the card on the CLI.
     * {@inheritDoc}
     */
    public void printCard(){
        switch (cardNumber) {
            case 1 -> {
                System.out.println( "   COMMON CARD NUMBER 1: \n"+
                                    "Two groups each containing 4 tiles of the same type in a 2x2 square.\n" +
                                    "The tiles of one square can be different from those of the other square.\n" +
                                    "++++++++++++++++++++++++ \n" +
                                    "+      | = | = |       + \n" +
                                    "+      | = | = |  x2   + \n"+
                                    "++++++++++++++++++++++++ \n");
            }
            case 10 -> {
                System.out.println( "COMMON CARD NUMBER 10: \n"+
                                    "Five tiles of the same type forming an X. \n" +
                                    "+++++++++++++++++++++ \n" +
                                    "+   | = |   | = |   + \n" +
                                    "+   |   | = |   |   + \n" +
                                    "+   | = |   | = |   + \n"+
                                    "+++++++++++++++++++++ \n");
            }
        }
    }
}
