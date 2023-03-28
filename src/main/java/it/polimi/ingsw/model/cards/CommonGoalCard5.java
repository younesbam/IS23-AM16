package it.polimi.ingsw.model.cards;

/**
 * <p>
 *     Represent card n.8.
 * </p>
 * Tiles in top edges must me the same
 * @author Nicolo' Gandini
 */
public class CommonGoalCard5 extends CommonGoalCard {

    public CommonGoalCard5(int playerNum, int cardNumber) {
        super(playerNum, cardNumber);
    }

    /**
     * Check if the player respect the rules to obtain the card's points
     * @param player actual player
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     * @author Nicolo' Gandini
     */
    public Integer checkScheme(Player player) {
        Cell[][] grid = player.getBookShelf().getGrid();

        /*
        Controllo che le tessere siano uguali agli angoli della libreria e che siano diverse da blank.
         */
        if(grid[0][0].getTile() == grid[0][MAXCOL-1].getTile() &&
                grid[0][0].getTile() == grid[MAXROW-1][MAXCOL-1].getTile() &&
                grid[0][0].getTile() == grid[MAXROW-1][0].getTile() &&
                grid[0][0].getTile() != Tile.BLANK){
            return getScore();
        }
        return 0;
    }
}
