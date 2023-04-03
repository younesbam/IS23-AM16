package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PlayerTest1 {
    // setNickname(?)
    @Test
    public void nickname(){
        Player player = new Player("CiccioPanza");
        assertEquals("CiccioPanza", player.getNickname());
    }

    // hasChair(?)
    @Test
    public void chair(){
        Player player = new Player("CiccioPanza");
        player.setChair(true);
        assertTrue(player.hasChair());
    }

    // setBookShelf(?)
    @Test
    public void bookshelf(){
        Player player = new Player("CiccioPanza");
        BookShelf bs = new BookShelf();
        List<Tile> tiles = new ArrayList<>();
        bs.placeTiles(1, tiles);
        player.setBookShelf(bs);

    }
}
