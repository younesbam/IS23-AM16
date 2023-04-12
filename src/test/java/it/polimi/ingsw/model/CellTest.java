package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    Tile testTile;
    int testX;
    int testY;



    @BeforeEach
    void init() {
        testX = 3;
        testY = 4;
    }

    @Test
    void getTile() {
        assertEquals(getTile(); Tile testTile = Tile.PINK;);
    }

    @Test
    void setTile(){
        this.testTile = Tile.PINK; 
    }
}