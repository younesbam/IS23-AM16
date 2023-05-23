package it.polimi.ingsw.communications.clientmessages.actions;

public class TilesPlaced implements GameAction{

    private String[] coordinates;

    public TilesPlaced(String[] coordinates){
        this.coordinates = coordinates;
    }



    /**
     * Tiles getter.
     */
    public String[] getCoordinates(){
        return this.coordinates;
    }
}
