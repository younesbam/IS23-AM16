package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.BookShelf;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiffAlignedTest {

    @Test
    public void checkScheme() {
        /*
         * With 1 player. Card number 1.
         */
        BookShelf bookshelf = new BookShelf();
        Player player = new Player("CiccioPanza");
        CommonGoalCard commonCard = new DiffAligned(1);

    }
}