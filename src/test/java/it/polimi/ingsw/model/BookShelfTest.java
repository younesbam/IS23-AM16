package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static it.polimi.ingsw.model.BookShelf.MAXBOOKSHELFROW;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.model.BookShelf.MAXBOOKSHELFCOL;

class BookShelfTest {
    BookShelf bookShelf;
    List<Tile> tilesList1;  //One element
    List<Tile> tilesList2;  //Two elements
    List<Tile> tilesList3;  //Three elements
    List<Integer> columnsGentili;
    List<Integer> columnsStronze;
    @BeforeEach
    void init(){
        bookShelf = new BookShelf();
        tilesList1 = new ArrayList<>();
        tilesList2 = new ArrayList<>();
        tilesList3 = new ArrayList<>();
        columnsGentili = new ArrayList<>();
        columnsStronze = new ArrayList<>();

        // Random column's number
        for(int i=0; i<MAXBOOKSHELFCOL; i++){
            columnsGentili.add(i);
        }
        columnsStronze.add(-2);
        columnsStronze.add(100);
        columnsStronze.add(-MAXBOOKSHELFCOL);

        tilesList1.add(Tile.YELLOW);
        tilesList2.add(Tile.BLUE);
        tilesList2.add(Tile.WHITE);
        tilesList3.add(Tile.GREEN);
        tilesList3.add(Tile.PINK);
        tilesList3.add(Tile.LIGHTBLUE);
    }

    @Test
    @DisplayName("Place tiles in a valid column")
    void placeColTest(){
        int k=0;
        for(int col : columnsGentili){
            bookShelf.placeTiles(col, tilesList1);
            assertEquals(bookShelf.getGrid()[MAXBOOKSHELFROW-k][col].getTile(), tilesList1.get(k));
            k++;
        }
    }
}