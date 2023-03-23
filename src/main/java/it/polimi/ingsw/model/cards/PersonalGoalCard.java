package it.polimi.ingsw.model.cards;

import java.util.Hashtable;

public class PersonalGoalCard extends Card {
    private Cell[][] grid;
    private Hashtable<Integer, Integer> points;

    /**
     * Initialize the personal goal card grid with empty Cells and initialize the hashtable, used to get the points, based on the number of matches.
     */
    public PersonalGoalCard() {
        grid = new Cell[5][6];
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
     */
    public ObjectTile getTile(int x, int y){
        return this.grid[x][y].getObjectTile();
    }

    public int checkScheme(Player player) {
        int actualMatches = 0;
        for(int j=4; j>=0; j--){
            for(int i=5; i>=0; i--){
                if(grid[i][j].getOccupied()){
                    if(grid[i][j].getObjectTile().getType().equals(player.getGrid()[i][j].getObjectTile()))
                        actualMatches++;
                }
            }
        }
        return this.points.get(actualMatches);
    }
}
