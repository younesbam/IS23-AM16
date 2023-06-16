package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.Hashtable;

import static it.polimi.ingsw.Const.*;
import static it.polimi.ingsw.Const.RESET_COLOR;

/**
 * Personal goal card
 * @author Nicolo' Gandini
 */
public class PersonalGoalCard extends Card {
    /**
     * Grid that represent the bookshelf of the card
     */
    private Cell[][] grid;
    /**
     * Hashtable of Integer type used to transform the number of matches into points.
     */
    private Hashtable<Integer, Integer> points;

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
        for(int i=0; i<MAXBOOKSHELFROW; i++){
            System.out.print(CYAN_BOLD_COLOR + i + RESET_COLOR);
            System.out.print(" [  ");
            for (int j=0; j<MAXBOOKSHELFCOL; j++){
                switch(grid[i][j].getTile().name()) {
                    case "BLANK" -> System.out.print(BLACK_COLOR + " " + RESET_COLOR + "  ");
                    case "PINK" -> System.out.print(PURPLE_COLOR + " " + RESET_COLOR + "  ");
                    case "GREEN" -> System.out.print(GREEN_COLOR + " " + RESET_COLOR + "  ");
                    case "YELLOW" -> System.out.print(YELLOW_COLOR + " " + RESET_COLOR + "  ");
                    case "WHITE" -> System.out.print(WHITE_COLOR + " " + RESET_COLOR + "  ");
                    case "BLUE" -> System.out.print(BLUE_COLOR + " " + RESET_COLOR + "  ");
                    case "LIGHTBLUE" -> System.out.print(CYAN_COLOR + " " + RESET_COLOR + "  ");
                }
            }
            System.out.print("]");
            System.out.println();
        }
        // Print column numbers
        System.out.print("     ");
        for (int k = 0; k < MAXBOOKSHELFCOL; k++) {
            System.out.print(CYAN_BOLD_COLOR + k + RESET_COLOR);
            System.out.print("  ");
        }
        System.out.println();

        System.out.println("\n This is how you will earn points with your Personal Goal Card: \n"+
                            "\n" +
                "+-------------------------+---+---+---+---+---+----+\n" +
                "| Number of correct tiles | 1 | 2 | 3 | 4 | 5 | 6  |\n" +
                "+-------------------------+---+---+---+---+---+----+\n" +
                "| Earned points           | 1 | 2 | 4 | 6 | 9 | 12 |\n" +
                "+-------------------------+---+---+---+---+---+----+\n");
    }
}
