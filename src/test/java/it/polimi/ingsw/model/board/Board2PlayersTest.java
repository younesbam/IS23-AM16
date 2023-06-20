package it.polimi.ingsw.model.board;

import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.common.exceptions.CellNotEmptyException;
import it.polimi.ingsw.common.exceptions.WrongCoordinateException;
import it.polimi.ingsw.common.exceptions.WrongTilesException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.Const.*;
import static org.junit.jupiter.api.Assertions.*;

class Board2PlayersTest {
    Board2Players b = new Board2Players();

    void boardTest(){
        // Filling the board.
        b.updateBoard();

        // Testing the pickability of some cells.
        assertTrue(b.isPickable(7,4));
        assertTrue(b.isPickable(4,1));
        assertTrue(b.isPickable(3,2));
        assertTrue(b.isPickable(1,3));
        assertTrue(b.isPickable(2,5));
        assertTrue(b.isPickable(3,7));

        assertFalse(b.isPickable(5,5));     // Cell hasn't free sides.
        assertFalse(b.isPickable(0,0));     // Unavailable cell.
        assertFalse(b.isPickable(8,4));     // Cell for 4 players.
        assertFalse(b.isPickable(0,3));     // Cell for 3 players.
        assertFalse(b.isPickable(8,8));     // Unavailable cell.
        assertFalse(b.isPickable(4,8));     // Cell for 4 players.
        assertFalse(b.isPickable(4,4));     // Cell hasn't free sides.
        assertFalse(b.isPickable(2,4));     // Cell hasn't free sides.
        assertFalse(b.isPickable(3,5));     // Cell hasn't free sides.
        assertFalse(b.isPickable(8,5));     // Cell for 3 players.

        Map<Tile, Coordinate> map = new HashMap<>();
        map.put(
                Tile.PINK,
                new Coordinate(5,5)
        );
        assertThrows(CellNotEmptyException.class, ()->b.restoreTiles(map));
        map.clear();

        // The board is filled randomly, so we just have to check that the method doesn't return blank or unavailable.
        assertNotEquals(Tile.BLANK, b.removeTile(4,5));
        assertEquals(Tile.BLANK, b.removeTile(4,5));
        assertNotEquals(Tile.BLANK, b.removeTile(7,4));
        assertEquals(Tile.BLANK, b.removeTile(7,4));
        assertNotEquals(Tile.BLANK, b.removeTile(3,7));
        assertEquals(Tile.BLANK, b.removeTile(3,7));

        map.put(
                Tile.UNAVAILABLE,
                new Coordinate(4,5)
        );
        assertThrows(WrongTilesException.class, ()->b.restoreTiles(map));
        map.put(
                Tile.BLANK,
                new Coordinate(4,5)
        );
        assertThrows(WrongTilesException.class, ()->b.restoreTiles(map));
        map.clear();

        map.put(
                Tile.PINK,
                new Coordinate(-1, 10)
        );
        map.put(
                Tile.GREEN,
                new Coordinate(0, 10)
        );
        map.put(
                Tile.BLUE,
                new Coordinate(10, 4)
        );
        assertThrows(WrongCoordinateException.class, ()->b.restoreTiles(map));

        // Check that cells contain the right expected tile.
        assertEquals(Tile.UNAVAILABLE, b.getTile(8,0));     // Unavailable cell.
        assertEquals(Tile.UNAVAILABLE, b.getTile(8,4));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(0,3));     // Cell for 3 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(8,8));     // Unavailable cell.
        assertEquals(Tile.UNAVAILABLE, b.getTile(4,8));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(8,5));     // Cell for 3 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(1,7));     // Unavailable cell.
        assertEquals(Tile.UNAVAILABLE, b.getTile(4,8));     // Cell for 4 players.
        assertEquals(Tile.UNAVAILABLE, b.getTile(5,0));     // Cell for 3 players.
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(3,4));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(5,4));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(5,5));
        assertNotEquals(Tile.UNAVAILABLE, b.getTile(5,1));
        assertNotEquals(Tile.BLANK, b.getTile(2,4));
        assertNotEquals(Tile.BLANK, b.getTile(1,3));
        assertNotEquals(Tile.BLANK, b.getTile(5,6));
        assertNotEquals(Tile.BLANK, b.getTile(6,4));
        assertTrue(b.isPickable(5,5));
//        assertFalse(b.refillNeeded());
        assertNotEquals(b.removeTile(4,5),Tile.UNAVAILABLE);
        assertEquals(b.removeTile(4,5), Tile.BLANK);
    }

}