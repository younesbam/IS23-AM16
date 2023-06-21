package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.Hashtable;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;

/**
 * Personal goal card
 */
public class PersonalGoalCard extends Card {
    /**
     * Grid that represent the bookshelf of the card
     */
    private final Cell[][] grid;
    /**
     * Hashtable of Integer type used to transform the number of matches into points.
     */
    private final Hashtable<Integer, Integer> points;

    /**
     * Initialize the personal goal card grid with empty Cells and initialize the hashtable, used to get the points, based on the number of matches.
     */
    public PersonalGoalCard(int cardNumber, Hashtable<Integer, Integer> points, Cell[][] grid){
        this.cardNumber = cardNumber;
        this.points = points;
        this.grid = grid;
    }

    /**
     *
     * @param x abscissa's coordinate of the grid.
     * @param y ordinate's coordinate of the grid.
     * @return ObjectTile corresponding to the related x,y coordinate inside the grid.
     * @author Nicolo' Gandini
     */
    public Tile getTile(int x, int y){
        return grid[x][y].getTile();
    }


    /**
     * {@inheritDoc}
     */
    public Integer checkScheme(Player player) {
        int matches = 0;
        for(int j=0; j<MAXBOOKSHELFCOL; j++){
            for(int i=0; i<MAXBOOKSHELFROW; i++){
                if(grid[i][j].getTile() != Tile.BLANK){
                    if(grid[i][j].getTile() == player.getBookShelf().getGrid()[i][j].getTile())
                        matches++;
                }
            }
        }
        return points.get(matches);
    }


    /**
     * Prints the card on the CLI.
     * {@inheritDoc}
     */
    public void printCard(){
        System.out.println("This is how you will earn points with your Personal Goal Card: \n"+
                            "\n" +
                "+-------------------------+---+---+---+---+---+----+\n" +
                "| Number of correct tiles | 1 | 2 | 3 | 4 | 5 | 6  |\n" +
                "+-------------------------+---+---+---+---+---+----+\n" +
                "| Earned points           | 1 | 2 | 4 | 6 | 9 | 12 |\n" +
                "+-------------------------+---+---+---+---+---+----+\n");
    }
}
