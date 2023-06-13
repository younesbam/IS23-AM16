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
    void bsTest(){
        // This cycle inserts the yellow tile in row 5 for each column and then checks whether the tile is present.
        for(int col : validColumns){
            bookShelf.placeTiles(col, tilesList1);
            grid = bookShelf.getGrid();
            assertEquals(tilesList1.get(0), grid[MAXBOOKSHELFROW-1][col].getTile());
        }

        // Testing method checkEndGame() with a bookshelf filled with just one row.
        assertFalse(bookShelf.checkEndGame());

        // Filling the bookshelf from row 4 to row 2.
        for(int col : validColumns)
            bookShelf.placeTiles(col, tilesList3);

        // Testing method checkEndGame() with a bookshelf with 2 empty rows.
        assertFalse(bookShelf.checkEndGame());

        // Filling the last two rows of the bookshelf.
        for(int col : validColumns)
            bookShelf.placeTiles(col, tilesList2);

        // The bookshelf is now filled, so the game must end.
        assertTrue(bookShelf.checkEndGame());
    }

    @Test
    void bsExceptionTest(){
        // Check if invalid columns throw the right exception.
        assertThrows(InvalidParameterException.class, ()->bookShelf.checkColumn(invalidColumns.get(2), 2));
        assertThrows(InvalidParameterException.class, ()->bookShelf.checkColumn(invalidColumns.get(3), 2));
        assertThrows(InvalidParameterException.class, ()->bookShelf.checkColumn(validColumns.get(0), 4));
        assertThrows(InvalidParameterException.class, ()->bookShelf.checkColumn(validColumns.get(1), -1));
        assertThrows(InvalidParameterException.class, ()->bookShelf.checkColumn(validColumns.get(2), 0));

        // Filling one column to check whether NotEmptyColumnException is thrown.
        bookShelf.placeTiles(validColumns.get(0), tilesList3);
        bookShelf.placeTiles(validColumns.get(0), tilesList3);
        assertThrows(NotEmptyColumnException.class, ()->bookShelf.checkColumn(validColumns.get(0), 2));
    }
}