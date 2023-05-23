package it.polimi.ingsw.communications.clientmessages.actions;

public class TilesPicked implements GameAction{

    private int[][] coordinates;

    public TilesPicked(int row1, int col1){
        coordinates = new int[1][2];

        coordinates[0][0] = row1;
        coordinates[0][1] = col1;
    }

    public TilesPicked(int row1, int col1, int row2, int col2){
        coordinates = new int[2][2];

        coordinates[0][0] = row1;
        coordinates[0][1] = col1;
        coordinates[1][0] = row2;
        coordinates[1][1] = col2;

    }

    public TilesPicked(int row1, int col1, int row2, int col2, int row3, int col3){
        coordinates = new int[3][2];

        coordinates[0][0] = row1;
        coordinates[0][1] = col1;
        coordinates[1][0] = row2;
        coordinates[1][1] = col2;
        coordinates[2][0] = row2;
        coordinates[2][1] = col2;
    }


    /**
     * Tiles getter.
     */
    public int[][] getTiles(){
        return this.coordinates;
    }
}
