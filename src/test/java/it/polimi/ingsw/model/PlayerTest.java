package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player player;
    BookShelf bs;
    final static String nickName = "MarioRossi";
    final static Boolean chair = true;
    int numTurns = 5;
    int points;
    //List<Integer> commonPoints = new ArrayList<>();
    //List<Integer> personalPoints = new ArrayList<>();
    static final int COMMON1 = 8;
    static final int COMMON2 = 6;
    static final int PERSONAL = 12;
    static final int ID = 20;


    @BeforeEach
    void init(){
        player = new Player(nickName, ID);
        bs = new BookShelf();
        //player.setNumOfTurns(numTurns);
        //commonPoints.add(COMMON1);
        //commonPoints.add(COMMON2);
        //personalPoints.add(PERSONAL);
        points = COMMON1 + COMMON2 + PERSONAL;
    }

    @Test
    void playerTest(){
        // Check for nickname.
        assertEquals(nickName, player.getUsername());

        // Check for chair.
        player.setChair(chair);
        assertTrue(player.hasChair());

        // Check for number of turns.
        assertEquals(numTurns, player.getNumOfTurns());

        // TODO: Check bookshelf

        // Check for ID.
        assertEquals(ID, player.getID());
    }

    @Test
    void turnsTest(){
        //player.updateNumOfTurns(numTurns);
        assertEquals(numTurns, player.getNumOfTurns());
    }


}