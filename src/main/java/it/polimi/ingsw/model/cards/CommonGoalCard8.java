package it.polimi.ingsw.model.cards;

/**
 * <p>
 *     Represent card n.12.
 * </p>
 * All the cells below the main diagonal must be full and not BLANK.
 * @author Nicolo' Gandini
 */
public class CommonGoalCard8 extends CommonGoalCard {

    public CommonGoalCard8(int playerNum, int cardNumber) {
        super(playerNum, cardNumber);
    }

    /**
     * Check if the player respect the rules to achieve the card's points
     * @param player actual player
     * @return Integer which represent the points that the player can obtain. 0 can be returned
     * @author Nicolo' Gandini
     */
    public Integer checkScheme(Player player) {
        /*
        Creo una variabile k che incrementa ad ogni riga che scorro(al contrario, dal basso verso l'alto).
        In tal modo riesco a sottrarre al numero delle colonne da controllare, fino ad arrivare a 1 cella da controllare
        in alto. In questo caso poi esco dal ciclo.
         */
        Cell[][] grid = player.getBookShelf().getGrid();
        int k = 0;
        for(int j=MAXROW-1; j>=1; j--) {
            for(int i=MAXCOL-1-k; i>=0; i--){
                if(grid[i][j].getTile() == Tile.BLANK)
                    return 0;
            }
            k++;
            if(k>=MAXCOL-1)
                return 0;
        }
        return getScore();
    }
}
