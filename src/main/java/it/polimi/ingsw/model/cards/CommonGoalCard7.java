package it.polimi.ingsw.model.cards;

/**
 * <p>
 *     Represent card n.11.
 * </p>
 * All the tiles in the diagonal must me the same.
 * <p>
 *     <b>Note:</b>
 *     there are two diagonals to check. Based on requirements, no preferred diagonal to check is specified.
 * </p>
 *
 * @author Nicolo' Gandini
 */
public class CommonGoalCard7 extends CommonGoalCard {

    public CommonGoalCard7(int playerNum) {
        super(playerNum);
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
            if(grid[0][0].getObjectTile().getType() == Type.BLANK || grid[0][0].getObjectTile().getType() != grid[i][i].getObjectTile().getType())
                return 0;
            if(grid[0][0].getObjectTile().getType() == Type.BLANK || grid[0][1].getObjectTile().getType() != grid[i][i+1].getObjectTile().getType())
                return 0;
        }
        return pickScoreTile();
    }
}
