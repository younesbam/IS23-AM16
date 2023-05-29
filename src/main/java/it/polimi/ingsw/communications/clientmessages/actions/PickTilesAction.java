package it.polimi.ingsw.communications.clientmessages.actions;

import it.polimi.ingsw.common.Coordinate;

import java.util.List;

/**
 * Pick tiles request from the player
 */
public class PickTilesAction implements GameAction{

    /**
     * Coordinates of the selected tiles
     */
    List<Coordinate> coordinates;

    public PickTilesAction(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }
}
