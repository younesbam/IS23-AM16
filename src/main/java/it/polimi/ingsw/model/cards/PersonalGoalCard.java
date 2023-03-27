package it.polimi.ingsw.model.cards;

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
    public PersonalGoalCard() {
        grid = new Cell[MAXCOL][MAXROW];
        points = new Hashtable<>();
        points.put(0,0);
        points.put(1,1);
        points.put(2,2);
        points.put(3,4);
        points.put(4,6);
        points.put(5,9);
        points.put(6,12);
    }

    /**
     *
     * @param x abscissa's coordinate of the grid.
     * @param y ordinate's coordinate of the grid.
     * @return ObjectTile corresponding to the related x,y coordinate inside the grid.
     * @author Nicolo' Gandini
     */
    public ObjectTile getTile(int x, int y){
        return grid[x][y].getObjectTile();
    }

    public Integer checkScheme(Player player) {
        int matches = 0;
        for(int j=MAXCOL-1; j>=0; j--){
            for(int i=MAXROW-1; i>=0; i--){
                if(grid[i][j].getType() != Type.BLANK){
                    if(grid[i][j].getObjectTile().getType() == player.getGrid()[i][j].getObjectTile().getType())
                        matches++;
                }
            }
        }
        return points.get(matches);
    }
}
