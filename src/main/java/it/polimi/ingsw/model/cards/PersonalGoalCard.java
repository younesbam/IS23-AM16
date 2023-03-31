package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.Hashtable;

/**
 * Personal goal card
 * @author Nicolo' Gandini
 */
public class PersonalGoalCard extends Card {
    private Cell[][] grid;
    /**
     * Hashtable of Integer type used to transform the number of matches into points
     */
    private Hashtable<Integer, Integer> points;

    /**
     * Initialize the personal goal card grid with empty Cells and initialize the hashtable, used to get the points, based on the number of matches.
     */
    public PersonalGoalCard(int cardNumber) {
        this.cardNumber = cardNumber;
        grid = new Cell[MAXCOL][MAXROW];
        for(int i=0; i<MAXCOL; i++)
            for(int j=0; j<MAXROW; j++)
                grid[i][j].setTile(Tile.BLANK);

        /*
        Initialize the hastable based on the personal card.
         */
        points = new Hashtable<>();
        points.put(0,0);
        points.put(1,1);
        points.put(2,2);
        points.put(3,4);
        points.put(4,6);
        points.put(5,9);
        points.put(6,12);

        /*
        Initialize the grid based on the card number.
         */
        switch (cardNumber){
            // DA FARE SONO SBAGLIATI
            case 1:
                grid[0][0].setTile(Tile.PINK);
                grid[2][0].setTile(Tile.BLUE);
                grid[4][1].setTile(Tile.GREEN);
                grid[3][2].setTile(Tile.WHITE);
                grid[4][1].setTile(Tile.GREEN);
                grid[5][2].setTile(Tile.LIGHTBLUE);
            case 2:
                grid[1][1].setTile(Tile.PINK);
                grid[2][0].setTile(Tile.GREEN);
                grid[2][2].setTile(Tile.YELLOW);
                grid[3][4].setTile(Tile.WHITE);
                grid[4][3].setTile(Tile.LIGHTBLUE);
                grid[5][4].setTile(Tile.BLUE);
            case 3:
                grid[0][1].setTile(Tile.BLUE);
                grid[3][1].setTile(Tile.YELLOW);
                grid[2][2].setTile(Tile.PINK);
                grid[1][3].setTile(Tile.GREEN);
                grid[5][0].setTile(Tile.WHITE);
                grid[5][2].setTile(Tile.LIGHTBLUE);
        }
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

    public Integer checkScheme(Player player) {
        int matches = 0;
        for(int j=MAXCOL-1; j>=0; j--){
            for(int i=MAXROW-1; i>=0; i--){
                if(grid[i][j].getTile() != Tile.BLANK){
                    if(grid[i][j].getTile() == player.getBookShelf().getGrid()[i][j].getTile())
                        matches++;
                }
            }
        }
        return points.get(matches);
    }
}
