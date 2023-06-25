package it.polimi.ingsw.model;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;
import static org.junit.jupiter.api.Assertions.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;


class BookShelfTest {
    BookShelf bookShelf = new BookShelf();
    Cell[][] grid = new Cell[MAXBOOKSHELFROW][MAXBOOKSHELFCOL];
    List<Tile> tilesList1 = new ArrayList<>();  //One element
    List<Tile> tilesList2 = new ArrayList<>();  //Two elements
    List<Tile> tilesList3 = new ArrayList<>();  //Three elements

    List<Integer> validColumns = new ArrayList<>(); // Contains valid values for columns in the bookshelf.
    List<Integer> invalidColumns = new ArrayList<>(); // Contains invalid values for columns in the bookshelf.

    @BeforeEach
    void init(){
        // This cycle fills list validColumns with the columns' values in the bookshelf.
        for(int i=0; i<MAXBOOKSHELFCOL; i++)
            validColumns.add(i);

        // Initialization of the grid.
        grid = Utils.createBlankGrid(MAXBOOKSHELFROW, MAXBOOKSHELFCOL);

        // Fill list invalidColumns with some invalid values for columns.
        invalidColumns.add(MAXBOOKSHELFCOL);
        invalidColumns.add(-1);
        invalidColumns.add(100);
        invalidColumns.add(-MAXBOOKSHELFCOL);

        tilesList1.add(Tile.YELLOW);
        tilesList2.add(Tile.BLUE);
        tilesList2.add(Tile.WHITE);
        tilesList3.add(Tile.GREEN);
        tilesList3.add(Tile.PINK);
        tilesList3.add(Tile.LIGHTBLUE);
    }

    @Test
    void checkColumnTest() throws NotEmptyColumnException {
        // The bookshelf is empty, so no exception should be thrown.
        for(int col : validColumns)
            assertDoesNotThrow(()->bookShelf.checkColumn(col, tilesList3.size()));

        // Checking that exception are thrown for invalid columns.
        for(int col1 : invalidColumns)
            assertThrows(InvalidParameterException.class, ()->bookShelf.checkColumn(col1, tilesList3.size()));

        // Filling some columns with tiles to check that other tiles won't be added.
        bookShelf.placeTiles(validColumns.get(0), tilesList3);
        bookShelf.placeTiles(validColumns.get(0), tilesList2);
        assertThrows(NotEmptyColumnException.class, ()->bookShelf.checkColumn(validColumns.get(0), tilesList2.size()));
    }

    @Test
    void placeTilesTest(){
        // Filling some columns and then checking that cells contain the right value.
        for(int col : validColumns)
            bookShelf.placeTiles(col, tilesList1);

        for(int i=0; i<MAXBOOKSHELFCOL; i++){
            grid = bookShelf.getGrid();
            assertEquals(tilesList1.get(0), grid[MAXBOOKSHELFROW-1][i].getTile());
        }
    }

    @Test
    void getGridTest(){
        assertNotNull(bookShelf.getGrid());
    }

    @Test
    void checkEndGameTest(){
        // Filling all the bookshelf except the highest row.
        for(int col : validColumns){
            bookShelf.placeTiles(col, tilesList3);
            bookShelf.placeTiles(col, tilesList2);
        }
        assertFalse(bookShelf.checkEndGame());

        // Filling all the bookshelf so the game ends.
        for (int col : validColumns)
            bookShelf.placeTiles(col, tilesList1);
        assertTrue(bookShelf.checkEndGame());
    }
}