package it.polimi.ingsw.model.cards;

import java.util.Hashtable;

public class PersonalGoalCard extends Card {
    private Cell[][] grid;
    private Hashtable<Integer, Integer> points;

    /**
     * Initialize the personal goal card grid with empty Cells and initialize the hashtable, used to get the points, based on the number of matches.
     */
    public PersonalGoalCard() {
        this.grid = new Cell[5][6];
        this.points = new Hashtable<>();
        this.points.put(0,0);
        this.points.put(1,1);
        this.points.put(2,2);
        this.points.put(3,4);
        this.points.put(4,6);
        this.points.put(5,9);
        this.points.put(6,12);
    }

    /**
     *
     * @param x abscissa's coordinate of the grid.
     * @param y ordinate's coordinate of the grid.
     * @return ObjectTile corresponding to the related x,y coordinate inside the grid.
     */
    public ObjectTile getTile(int x, int y){
        return this.grid[x][y].getObjectTile();
    }

    public int checkScheme(Player player) {
        int actualMatches = 0;
        for(int j=4; j>=0; j--){
            for(int i=5; i>=0; i--){
                if(grid[i][j].getOccupied()){
                    if(grid[i][j].getObjectTile().getType().equals(player.grid[i][j].getObjectTile()))
                        actualMatches++;
                }
            }
        }
        return this.points.get(actualMatches);
    }
}
