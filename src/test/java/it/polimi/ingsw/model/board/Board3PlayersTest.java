package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Board3PlayersTest extends BoardTest{
    Board3Players b = new Board3Players();

    @Test
    void board3Test(){
        // The board has just been created, so it's empty.
        assertTrue(b.refillNeeded());

        // Filling the board.
        b.updateBoard();

        // Testing the pickability of some cells.
        assertTrue(b.isPickable(0,3));
        assertTrue(b.isPickable(1,3));
        assertTrue(b.isPickable(2,2));
        assertTrue(b.isPickable(5,0));
        assertTrue(b.isPickable(6,2));
        assertTrue(b.isPickable(8,5));
        assertTrue(b.isPickable(3,8));
        assertTrue(b.isPickable(2,5));
        assertTrue(b.isPickable(6,6));
        assertTrue(b.isPickable(2,6));

        assertFalse(b.isPickable(0,4));     // Cell for 4 players.
        assertFalse(b.isPickable(3,1));     // Cell for 4 players.
        assertFalse(b.isPickable(8,0));     // Unavailable cell.
        assertFalse(b.isPickable(7,1));     // Unavailable cell.
        assertFalse(b.isPickable(7,7));     // Unavailable cell.
        assertFalse(b.isPickable(4,8));     // Cell for 4 players.
        assertFalse(b.isPickable(2,7));     // Unavailable cell.
        assertFalse(b.isPickable(8,4));     // Cell for 4 players.
        assertFalse(b.isPickable(2,1));     // Unavailable cell.
        assertFalse(b.isPickable(4,4));     // Hasn't free sides.
        assertFalse(b.isPickable(5,2));     // Hasn't free sides.
        assertFalse(b.isPickable(3,6));     // Hasn't free sides.
        assertFalse(b.isPickable(2,4));     // Hasn't free sides.

        // The board is filled randomly, so we just have to check that the method doesn't return blank or unavailable.
        assertNotEquals(Tile.BLANK, b.removeTile(0,3));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(0,3));
        assertNotEquals(Tile.BLANK, b.removeTile(1,3));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(1,3));
        assertNotEquals(Tile.BLANK, b.removeTile(2,2));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(2,2));
        assertNotEquals(Tile.BLANK, b.removeTile(5,0));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(5,0));
        assertNotEquals(Tile.BLANK, b.removeTile(6,2));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(6,2));
        assertNotEquals(Tile.BLANK, b.removeTile(8,5));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(8,5));
        assertNotEquals(Tile.BLANK, b.removeTile(3,8));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(3,8));
        assertNotEquals(Tile.BLANK, b.removeTile(2,5));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(2,5));
        assertNotEquals(Tile.BLANK, b.removeTile(6,6));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(6,6));
        assertNotEquals(Tile.BLANK, b.removeTile(2,6));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(2,6));

        // Check that the cell contains the expected value.
        assertEquals(Tile.UNAVAILABLE, b.getTile(0,4));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(1,5));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(4,8));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(5,7));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(8,4));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(7,3));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(4,0));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(3,1));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(7,1));
        assertEquals(Tile.UNAVAILABLE, b.getTile(7,7));
        assertEquals(Tile.UNAVAILABLE, b.getTile(5,8));
        assertEquals(Tile.UNAVAILABLE, b.getTile(2,7));
        assertEquals(Tile.UNAVAILABLE, b.getTile(2,1));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(4,4));
        assertNotEquals(Tile.BLANK, b.getTile(3,2));
        assertNotEquals(Tile.BLANK, b.getTile(5,1));
        assertNotEquals(Tile.BLANK, b.getTile(5,5));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(3,3));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(7,4));
    }
}