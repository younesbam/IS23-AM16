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
    void playerTest() {
        // Check for nickname.
        assertEquals(nickName, player.getUsername());

        // Check for activity.
        assertTrue(player.isActive());

        // Check for chair.
        player.setChair(chair);
        assertTrue(player.hasChair());

        // Test for turns.
        player.updateNumOfTurns();
        numTurns++;
    }

    @Test
    void turnsTest(){
        //player.updateNumOfTurns(numTurns);
        assertEquals(numTurns, player.getNumOfTurns());
        player.updateNumOfTurns();
        numTurns++;
        assertEquals(numTurns, player.getNumOfTurns());

        // Check for points.
        player.updateTotalPoints();
        assertEquals(points, player.getTotalPoints());
        assertEquals(0, player.getCommonCardPointsEarned(0));
        assertEquals(0, player.getCommonCardPointsEarned(1));
        player.setCommonCardPointsEarned(0, common1points);
        assertEquals(common1points, player.getCommonCardPointsEarned(0));
        player.setCommonCardPointsEarned(1, common2points);
        assertEquals(common2points, player.getCommonCardPointsEarned(1));
        points = common1points + common2points;
        player.updateTotalPoints();
        assertEquals(points, player.getTotalPoints());

        // Check for ID.
        assertEquals(ID, player.getID());
    }
}