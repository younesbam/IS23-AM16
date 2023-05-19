package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        player.setPoints(100);
        assertEquals(100, player.getPoints());
    }


    @Test
    void bookShelfTest() {
        List<Tile> tiles = new ArrayList<>();
        tiles = Collections.singletonList(Tile.PINK);
        bs.placeTiles(1, tiles);
        player.setBookShelf(bs);

        assertEquals(bs, player.getBookShelf());
    }

    @Test
    void chairTest(){
        player.setChair(chair);
        assertTrue(player.hasChair());
    }

    @Test
    void turnsTest(){
        player.setNumOfTurns(numTurns);
        assertEquals(numTurns, player.getNumOfTurns());
    }


}