package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import static it.polimi.ingsw.Utils.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Utils.MAXBOOKSHELFROW;

/**
 * <p>
 *     Represent card n.8.
 * </p>
 * Equal tiles in corners.
 * @author Nicolo' Gandini
 */
public class EqualCorners extends CommonGoalCard {

    public EqualCorners(int cardNumber) {
        super(cardNumber);
    }

    /**
     * Check if the player respect the rules to obtain the card's points
     * @param player actual player
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     */
    public Integer checkScheme(Player player) {
        Cell[][] grid = player.getBookShelf().getGrid();

        /*
        Controllo che le tessere siano uguali agli angoli della libreria e che siano diverse da blank.
         */
        if(grid[0][0].getTile() == grid[0][MAXBOOKSHELFCOL-1].getTile() &&
                grid[0][0].getTile() == grid[MAXBOOKSHELFROW-1][MAXBOOKSHELFCOL-1].getTile() &&
                grid[0][0].getTile() == grid[MAXBOOKSHELFROW-1][0].getTile() &&
                grid[0][0].getTile() != Tile.BLANK){
            return getScore();
        }
        return 0;
    }
}
