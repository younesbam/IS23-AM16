package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player player;
    BookShelf bs;
    final static String nickName = "MarioRossi";
    final static Boolean chair = true;
    int numTurns = 0;
    int points = 0;
    int common1points = 8;
    int common2points = 4;
    static final int ID = 20;


    @BeforeEach
    void init(){
        player = new Player(nickName, ID);
        player.setUsername(nickName);
        bs = new BookShelf();
        player.setActive(true);
    }

    @Test
    void playerTest(){
        // Check for nickname.
        assertEquals(nickName, player.getUsername());

        // Check for activity.
        assertTrue(player.isActive());

        // Check for chair.
        player.setChair(chair);
        assertTrue(player.hasChair());

        // Check for number of turns.
        assertEquals(numTurns, player.getNumOfTurns());
        player.updateNumOfTurns();
        numTurns++;
        assertEquals(numTurns, player.getNumOfTurns());

        // Check for points.
        player.updateTotalPoints();
        assertEquals(points, player.getTotalPoints());
        assertEquals(0, player.getCommonCardPoints(0));
        assertEquals(0, player.getCommonCardPoints(1));
        player.setCommonCardPoints(0, common1points);
        assertEquals(common1points, player.getCommonCardPoints(0));
        player.setCommonCardPoints(1, common2points);
        assertEquals(common2points, player.getCommonCardPoints(1));
        points = common1points + common2points;
        player.updateTotalPoints();
        assertEquals(points, player.getTotalPoints());

        // Check for ID.
        assertEquals(ID, player.getID());
    }
}