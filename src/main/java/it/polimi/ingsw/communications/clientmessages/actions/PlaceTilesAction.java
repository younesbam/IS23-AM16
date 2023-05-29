package it.polimi.ingsw.communications.clientmessages.actions;

import it.polimi.ingsw.model.Tile;

import java.util.List;

/**
 * Place tiles request from the player
 */
public class PlaceTilesAction implements GameAction{
    /**
     * List of tiles to be placed in the bookshelf
     */
    private List<Tile> tiles;

    /**
     * Column where to place tiles.
     */
    private int col;

    public PlaceTilesAction(List<Tile> tiles, int col) {
        this.tiles = tiles;
        this.col = col;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public int getCol() {
        return col;
    }
}
