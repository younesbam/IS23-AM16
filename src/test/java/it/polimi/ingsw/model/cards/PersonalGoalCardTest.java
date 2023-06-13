package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Test;

import java.util.Hashtable;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;
import static org.junit.jupiter.api.Assertions.*;

class PersonalGoalCardTest {

        Player player = new Player("MarioRossi", 01);
        PersonalGoalCard personalGoalCard = new PersonalGoalCard(1,new Hashtable<>(), new Cell[MAXBOOKSHELFROW][MAXBOOKSHELFCOL]);
    @Test
    void checkScheme() {
        /*
        Se ha rispettato 1 cella:
        assertEquals(checkScheme(player), 1);

        Se ha rispettato 2 celle:
        assertEquals(checkScheme(player), 2)

        Se ha rispettato 3 celle:
        assertEquals(checkScheme(player), 4)

        Se ha rispettato 4 celle:
        assertEquals(checkScheme(player), 6)

        Se ha rispettato 5 celle:
        assertEquals(checkScheme(player), 9)

        Se ha rispettato 6 celle:
        assertEquals(checkScheme(player), 12)
         */

    }
}