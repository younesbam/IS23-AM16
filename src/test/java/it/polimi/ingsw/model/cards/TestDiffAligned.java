package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.BookShelf;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

public class TestDiffAligned {
    @Test
    public void testCheckScheme(){
        /*
         * With 1 player. Card number 1.
         */
        BookShelf bookshelf = new BookShelf();
        Player player = new Player("CiccioPanza", 20);
        CommonGoalCard commonCard = new DiffAligned(1);


    }
}
