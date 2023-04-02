package it.polimi.ingsw.model;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.BookShelf;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PlayerTest {
    @Test
    public void nickname(){
        Player player = new Player("CiccioPanza");
        assertEquals("CiccioPanza", player.getNickname());
    }

    @Test
    public void chair(){
        Player player = new Player("CiccioPanza");
        player.setChair(true);
        assertTrue(player.hasChair());
    }

    @Test
    public void bookshelf(){
        Player player = new Player("CiccioPanza");
        BookShelf bs = new BookShelf();
        List<Tile> tiles = new ArrayList<>();
        bs.placeTiles(1, tiles);
        player.setBookShelf(bs);

    }
}
