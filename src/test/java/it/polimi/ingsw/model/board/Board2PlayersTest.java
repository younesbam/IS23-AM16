package it.polimi.ingsw.model.board;

import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.common.exceptions.CellNotEmptyException;
import it.polimi.ingsw.common.exceptions.WrongCoordinateException;
import it.polimi.ingsw.common.exceptions.WrongTilesException;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.notification.RunListener;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.Const.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Board2PlayerTest class tests the Board class when it's filled for 2 players.
 * @see Board
 * @see Board2PlayersTest
 */
class Board2PlayersTest extends BoardTest{
    Board2Players b = new Board2Players();
    Map<Tile, Coordinate> map = new HashMap<>();

    /**
     * This method instantiates the board to run tests.
     */
    @BeforeEach
    void init(){
        // The board is initially empty, so it must be filled.
        b.updateBoard();
    }

    /**
     * This method tests the method restoreTiles() in Board class when the map passed as parameter contains BLANK tile.
     * @see Board#restoreTiles(Map)
     */
    @Test
    void restoreTilesTestBlankTile(){
        // First removing some tiles to leave free spaces.
        b.removeTile(1,3);
        b.removeTile(1,4);

        map.put(Tile.PINK, new Coordinate(1,3));
        map.put(Tile.BLANK, new Coordinate(1,4));
        assertThrows(WrongTilesException.class, ()->b.restoreTiles(map));
    }

    /**
     * This method tests the method restoreTiles() in Board class when the map passed as parameter contains UNAVAILABLE tile.
     * @see Board#restoreTiles(Map)
     */
    @Test
    void restoreTilesTestUnavailableTile(){
        // First removing some tiles to leave free spaces.
        b.removeTile(1,3);
        b.removeTile(1,4);

        map.put(Tile.PINK, new Coordinate(1,3));
        map.put(Tile.UNAVAILABLE, new Coordinate(1,4));
        assertThrows(WrongTilesException.class, ()->b.restoreTiles(map));
    }

    /**
     * This method tests the method restoreTiles() in Board class when the coordinates are wrong.
     * @see Board#restoreTiles(Map)
     */
    @Test
    void restoreTilesTestWrongCoordinates(){
        // Row exceeds the dimension.
        map.put(Tile.PINK, new Coordinate(MAXBOARDDIM, 0));
        assertThrows(WrongCoordinateException.class, ()->b.restoreTiles(map));

        map.clear();

        // Row is negative.
        map.put(Tile.PINK, new Coordinate(-1, 0));
        assertThrows(WrongCoordinateException.class, ()->b.restoreTiles(map));

        map.clear();

        // Column exceeds the dimension.
        map.put(Tile.PINK, new Coordinate(0, MAXBOARDDIM));
        assertThrows(WrongCoordinateException.class, ()->b.restoreTiles(map));

        map.clear();

        // Column is negative.
        map.put(Tile.PINK, new Coordinate(0, -1));
        assertThrows(WrongCoordinateException.class, ()->b.restoreTiles(map));
    }

    /**
     * This method tests the method restoreTiles() in Board class when the coordinates identify a not empty cell.
     * @see Board#restoreTiles(Map)
     */
    @Test
    void restoreTilesTestNotEmptyCell(){
        map.put(Tile.PINK, new Coordinate(1,3));
        map.put(Tile.PINK, new Coordinate(1,4));

        assertThrows(CellNotEmptyException.class, ()->b.restoreTiles(map));
    }

    /**
     * This method tests the method restoreTiles() in Board class when all parameters are correct.
     * @see Board#restoreTiles(Map)
     */
    @Test
    void restoreTilesTest() throws WrongCoordinateException, WrongTilesException, CellNotEmptyException {
        map.put(b.removeTile(1,3), new Coordinate(1,3));
        map.put(b.removeTile(1,4), new Coordinate(1,4));

        b.restoreTiles(map);
    }

    /**
     * This method tests method isPickable() in Board class.
     * @see Board#isPickable(int, int)
     */
    @Test
    void isPickableTest(){
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

        // Check that the methods throws exception with wrong parameters.
        assertThrows(InvalidParameterException.class, ()->b.isPickable(MAXBOARDDIM, 0));
        assertThrows(InvalidParameterException.class, ()->b.isPickable(0, MAXBOARDDIM));
        assertThrows(InvalidParameterException.class, ()->b.isPickable(-1, 0));
        assertThrows(InvalidParameterException.class, ()->b.isPickable(0, -1));
    }

    /**
     * This method tests method removeTile() from the Board class.
     * @see Board#removeTile(int, int)
     */
    @Test
    void removeTileTest(){
        // The board is filled randomly, so we just have to check that the method doesn't return blank or unavailable.
        assertNotEquals(Tile.BLANK, b.removeTile(4,5));
        assertEquals(Tile.BLANK, b.removeTile(4,5));
        assertNotEquals(Tile.BLANK, b.removeTile(7,4));
        assertEquals(Tile.BLANK, b.removeTile(7,4));
        assertNotEquals(Tile.BLANK, b.removeTile(3,7));
        assertEquals(Tile.BLANK, b.removeTile(3,7));
    }

    /**
     * This method tests method getTile() in Board class.
     * @see Board#getTile(int, int)
     */
    @Test
    void getTileTest(){
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
    }

    /**
     * This method tests method getBoardForGUI() in Board class.
     * @see Board#getBoardforGUI()
     */
    @Test
    void getBoardForGUITest(){
        assertNotNull(b.getBoardforGUI());
    }
}