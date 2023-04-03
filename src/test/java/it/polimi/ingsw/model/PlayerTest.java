package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void getNickname() {
    }

    @Test
    void setNickname() {
        Player player = new Player("CiccioPanza");
        assertEquals("CiccioPanza", player.getNickname());
    }

    @Test
    void getPoints() {
    }

    @Test
    void setPoints() {
    }

    @Test
    void hasChair() {
        Player player = new Player("CiccioPanza");
        player.setChair(true);
        assertTrue(player.hasChair());
    }

    @Test
    void setChair() {
    }

    @Test
    void getBookShelf() {
    }

    @Test
    void setBookShelf() {
        Player player = new Player("CiccioPanza");
        BookShelf bs = new BookShelf();
        List<Tile> tiles = new ArrayList<>();
        bs.placeTiles(1, tiles);
        player.setBookShelf(bs);
    }

    @Test
    void getNumOfTurns() {
    }

    @Test
    void setNumOfTurns() {
    }

    @Test
    void getCommonGoalReached() {
    }

    @Test
    void getPersonalGoalCard() {
    }

    @Test
    void setPersonalGoalCard() {
    }
}