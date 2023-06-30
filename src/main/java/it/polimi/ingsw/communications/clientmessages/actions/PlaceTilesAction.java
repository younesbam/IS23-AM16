package it.polimi.ingsw.communications.clientmessages.actions;

import it.polimi.ingsw.model.Tile;

import java.util.List;

/**
 * Place tiles request from the player.
 */
public class PlaceTilesAction implements GameAction{
    /**
     * List of tiles to be placed in the bookshelf
     */
    private final List<Tile> tiles;

    /**
     * Bookshelf column where to place tiles.
     */
    private final int col;

    /**
     * Constructor.
     * @param tiles list of tiles to be placed in the bookshelf.
     * @param col bookshelf column where to place tiles.
     */
    public PlaceTilesAction(List<Tile> tiles, int col) {
        this.tiles = tiles;
        this.col = col;
    }

    /**
     * Get list of tiles.
     * @return list of tiles to be placed in the bookshelf.
     */
    public List<Tile> getTiles() {
        return tiles;
    }

    /**
     * Get column.
     * @return bookshelf column where to place tiles.
     */
    public int getCol() {
        return col;
    }
}
