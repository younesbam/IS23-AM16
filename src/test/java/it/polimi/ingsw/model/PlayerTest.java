package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class PlayerTest {
    Player player;
    BookShelf bs;
    @BeforeEach
    void init(){
        player = new Player("CiccioPanza");
        bs = new BookShelf();
    }
    @Test
    void setBookShelf() {
        List<Tile> tiles = new ArrayList<>();
        bs.placeTiles(1, tiles);
        player.setBookShelf(bs);
    }
}