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
    @BeforeEach
    void init(){
        player = new Player(nickName, 20);
        bs = new BookShelf();
        int points;
    }

    @Test
    void nicknameTest() {
        assertEquals(nickName, player.getUsername());
    }


    @Test
    void pointsTest() {
//        player.updateTotalPoints(100);
//        assertEquals(100, player.getTotalPoints());
    }


    @Test
    void bookShelfTest() {
//        List<Tile> tiles = new ArrayList<>();
//        tiles = Collections.singletonList(Tile.PINK);
//        bs.placeTiles(1, tiles);
//        player.setBookShelf(bs);
//
//        assertEquals(bs, player.getBookShelf());
    }

    @Test
    void chairTest(){
        player.setChair(chair);
        assertTrue(player.hasChair());
    }

    @Test
    void turnsTest(){
        player.updateNumOfTurns(numTurns);
        assertEquals(numTurns, player.getNumOfTurns());
    }


}