package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Game g = new Game();
    Player p1 = new Player("Pippo", 20);
    Player p2 = new Player("Pluto", 02);
    Player p3 = new Player("Paperino", 02);
    ArrayList<Player> ps = new ArrayList<>();

    @BeforeEach
    void init(){
        int numP = 3;
        g.createPlayer(p1);
        g.createPlayer(p2);
        g.createPlayer(p3);
    }

    @Test
    void testPlayer(){
        g.removePlayer(p2);

        assertEquals(ps.get(2), null);
        assertEquals(ps.get(1), p3);
        assertEquals(ps.get(0), p1);
    }


}