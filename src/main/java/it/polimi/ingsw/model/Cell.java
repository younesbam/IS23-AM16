package it.polimi.ingsw.model;

/**
 * This class represents the cell.
 * The cell is used from both the board and the bookshelf.
 * @author Francesca Rosa Diz
 */
public class Cell {

    // This attribute represents the object tile contained in the cell.
    private Tile tile;

    // This variable specifies the x coordinate of the cell.
    private int x;

    // This variable specifies the y coordinate of the cell.
    private int y;

    public Cell(){
        x=0;
        y=0;
        tile=Tile.BLANK;
    }

    /**
     * This method returns the tile contained in the cell.
     * @return
     */
    public Tile getTile(){
        return tile;
    }

    /**
     * This method sets the tile in the cell.
     * @param tile
     */
    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
