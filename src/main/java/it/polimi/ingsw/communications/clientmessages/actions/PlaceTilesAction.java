package it.polimi.ingsw.communications.clientmessages.actions;

public class PlaceTilesAction implements GameAction{

    private String[] coordinates;

    public PlaceTilesAction(String[] coordinates){
        this.coordinates = coordinates;
    }

    /**
     * Tiles getter.
     */
    public String[] getCoordinates(){
        return this.coordinates;
    }
}
