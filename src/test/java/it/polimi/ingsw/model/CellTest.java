package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    Cell cell = new Cell();
    Tile testTile;
    final int testX = 3;
    final int testY = 4;



    @BeforeEach
    void init() {
        cell.setTile(Tile.PINK);
    }

    @Test
    void tileTest(){
        assertEquals(Tile.PINK, cell.getTile());
    }
}