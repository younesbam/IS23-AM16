package it.polimi.ingsw.communications.clientmessages.actions;

import it.polimi.ingsw.common.Coordinate;

import java.util.List;

/**
 * Pick tiles request from the player.
 */
public class PickTilesAction implements GameAction{
    /**
     * Coordinates of the selected tiles.
     */
    private final List<Coordinate> coordinates;

    /**
     * Constructor.
     * @param coordinates coordinates of the tiles, chosen by the user.
     */
    public PickTilesAction(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Get coordinates list.
     * @return list of coordinates where the user want to pick the tiles from the board.
     */
    public List<Coordinate> getCoordinates() {
        return coordinates;
    }
}
