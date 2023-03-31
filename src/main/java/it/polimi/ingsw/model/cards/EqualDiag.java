package it.polimi.ingsw.model.cards;

/**
 * <p>
 *     Represent card n.11.
 * </p>
 * Equal tiles on the diagonal.
 * <p>
 *     <b>Note:</b>
 *     there are two diagonals to check. Based on requirements, no preferred diagonal to check is specified.
 * </p>
 *
 * @author Nicolo' Gandini
 */
public class EqualDiag extends CommonGoalCard {

    public EqualDiag(int playerNum, int cardNumber) {
        super(playerNum, cardNumber);
    }

    /**
     * Check if the player respect the rules to obtain the card's points
     * @param player actual player
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     * @author Nicolo' Gandini
     */
    public Integer checkScheme(Player player) {
        /*
        Controllo che le celle in diagonale siano uguali e non BLANK. Siccome ci sono due diagonali da controllare
        le controllo entrambe (non è specificato quale controllare).
        Cerco il minimo tra MAXROW e MAXCOL perchè se cambia la dimensione della matrice potrebbe esserci un lato più lungo dell'altro.
         */
        Cell[][] grid = player.getBookShelf().getGrid();

        for(int i=0; i<Math.min(MAXROW, MAXCOL); i++) {
            if(grid[0][0].getTile() == Tile.BLANK || grid[0][0].getTile() != grid[i][i].getTile())
                return 0;
            if(grid[0][0].getTile() == Tile.BLANK || grid[0][1].getTile() != grid[i][i+1].getTile())
                return 0;
        }
        return getScore();
    }
}
