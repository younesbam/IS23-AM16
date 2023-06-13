package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Board4PlayersTest extends BoardTest{
    Board4Players b = new Board4Players();

    @Test
    void board4Test(){
        // The board has just been created, so there it's empty.
        assertTrue(b.refillNeeded());

        // Filling the board.
        b.updateBoard();

        // Testing the pickability of some cells.
        assertTrue(b.isPickable(0,4));
        assertTrue(b.isPickable(1,5));
        assertTrue(b.isPickable(4,8));
        assertTrue(b.isPickable(5,7));
        assertTrue(b.isPickable(8,4));
        assertTrue(b.isPickable(7,3));
        assertTrue(b.isPickable(4,0));
        assertTrue(b.isPickable(3,1));

        assertFalse(b.isPickable(1,4));     // Hasn't free sides.
        assertFalse(b.isPickable(4,7));     // Hasn't free sides.
        assertFalse(b.isPickable(7,4));     // Hasn't free sides.
        assertFalse(b.isPickable(4,1));     // Hasn't free sides.
        assertFalse(b.isPickable(6,3));     // Hasn't free sides.
        assertFalse(b.isPickable(2,5));     // Hasn't free sides.
        assertFalse(b.isPickable(7,2));     // Unavailable.
        assertFalse(b.isPickable(7,7));     // Unavailable.
        assertFalse(b.isPickable(2,8));     // Unavailable.
        assertFalse(b.isPickable(2,0));     // Unavailable.
        assertFalse(b.isPickable(2,7));     // Unavailable.
        assertFalse(b.isPickable(1,2));     // Unavailable.

        // The board is filled randomly, so we just have to check that the method doesn't return blank or unavailable.
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(0,4));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(1,5));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(4,8));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(5,7));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(8,4));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(7,3));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(4,0));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(3,1));
        assertEquals(Tile.BLANK, b.removeTile(0,4));
        assertEquals(Tile.BLANK, b.removeTile(1,5));
        assertEquals(Tile.BLANK, b.removeTile(4,8));
        assertEquals(Tile.BLANK, b.removeTile(5,7));
        assertEquals(Tile.BLANK, b.removeTile(8,4));
        assertEquals(Tile.BLANK, b.removeTile(7,3));
        assertEquals(Tile.BLANK, b.removeTile(4,0));
        assertEquals(Tile.BLANK, b.removeTile(3,1));
        assertNotEquals(Tile.BLANK, b.removeTile(3,8));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(2,2));
        assertNotEquals(Tile.BLANK, b.removeTile(2,6));
        assertNotEquals(Tile.UNAVAILABLE, b.removeTile(6,6));

        // Check that the cell contains the expected value.
        assertEquals(Tile.UNAVAILABLE, b.getTile(6,0));
        assertEquals(Tile.UNAVAILABLE, b.getTile(3,0));
        assertEquals(Tile.UNAVAILABLE, b.getTile(6,1));
        assertEquals(Tile.UNAVAILABLE, b.getTile(8,8));
        assertEquals(Tile.UNAVAILABLE, b.getTile(0,0));
        assertEquals(Tile.UNAVAILABLE, b.getTile(0,8));
        assertEquals(Tile.UNAVAILABLE, b.getTile(1,8));
        assertEquals(Tile.UNAVAILABLE, b.getTile(2,8));
        assertEquals(Tile.UNAVAILABLE, b.getTile(7,6));

        assertNotEquals(Tile.UNAVAILABLE, b.getTile(4,2));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(5,5));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(2,6));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(7,5));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(5,2));
        assertNotEquals(Tile.BLANK, b.getTile(6,4));
        assertNotEquals(Tile.BLANK, b.getTile(3,7));
        assertNotEquals(Tile.BLANK, b.getTile(3,2));
        assertNotEquals(Tile.BLANK, b.getTile(4,4));
        assertNotEquals(Tile.BLANK, b.getTile(3,6));
    }
}