package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CellTest class tests Cell class in model.
 * @see Cell
 */
class CellTest {

    Cell cell = new Cell();

    /**
     * This method instantiates the cell to run tests.
     */
    @BeforeEach
    void init() {
        cell.setTile(Tile.PINK);
    }

    /**
     * This method tests method getTile() in Cell class.
     * @see Cell#getTile()
     */
    @Test
    void tileTest(){
        assertEquals(Tile.PINK, cell.getTile());
    }
}