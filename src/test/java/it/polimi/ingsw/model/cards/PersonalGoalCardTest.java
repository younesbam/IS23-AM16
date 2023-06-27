package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.model.BookShelf;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PersonalGoalCardTest class tests PersonalGoalCard class in model.
 * @see PersonalGoalCard
 */
class PersonalGoalCardTest {

    Player player;
    PersonalGoalCard personalGoalCard;
    BookShelf bs;

    List<Tile> pinkTiles = new ArrayList<>();
    List<Tile> yellowTiles = new ArrayList<>();
    List<Tile> greenTiles = new ArrayList<>();
    List<Tile> whiteTiles = new ArrayList<>();
    List<Tile> blueTiles = new ArrayList<>();
    List<Tile> lightblueTiles = new ArrayList<>();

    /**
     * This method instantiates both the player and the card.
     * We test Personal Goal Card number 1.
     */
    @BeforeEach
    void init(){
        player = new Player("Pippo", 1);

        personalGoalCard = new PersonalGoalCard(1, new Hashtable<>(), new Cell[MAXBOOKSHELFROW][MAXBOOKSHELFCOL]);

        LinkedList<PersonalGoalCard> persCards = new LinkedList<>();
        JSONParser jsonParser = new JSONParser("initSetup.json");
        persCards = jsonParser.getPersonalGoalCards();
        personalGoalCard = persCards.get(0);

        // Creation of tiles' lists.
        pinkTiles.add(Tile.PINK);
        pinkTiles.add(Tile.PINK);
        pinkTiles.add(Tile.PINK);

        yellowTiles.add(Tile.YELLOW);
        yellowTiles.add(Tile.YELLOW);
        yellowTiles.add(Tile.YELLOW);

        greenTiles.add(Tile.GREEN);
        greenTiles.add(Tile.GREEN);
        greenTiles.add(Tile.GREEN);

        whiteTiles.add(Tile.WHITE);
        whiteTiles.add(Tile.WHITE);
        whiteTiles.add(Tile.WHITE);

        blueTiles.add(Tile.BLUE);
        blueTiles.add(Tile.BLUE);
        blueTiles.add(Tile.BLUE);

        lightblueTiles.add(Tile.LIGHTBLUE);
        lightblueTiles.add(Tile.LIGHTBLUE);
        lightblueTiles.add(Tile.LIGHTBLUE);
    }

    /**
     * This method tests method getTile() in PersonalGoalCard class.
     * @see PersonalGoalCard#getTile(int, int)
     */
    @Test
    void getTileTest(){
        for(int i=0; i<MAXBOOKSHELFROW; i++)
            for(int j=0; j<MAXBOOKSHELFCOL; j++){
                assertNotNull(personalGoalCard.getTile(i, j));
                assertNotEquals(Tile.UNAVAILABLE, personalGoalCard.getTile(i, j));
            }
    }

    /**
     * This method tests method checkScheme() in PersonalGoalCardClass when 1 cell is respected.
     * @see PersonalGoalCard#checkScheme(Player)
     */
    @Test
    void checkSchemeTest1Cell() {
        int oneCell = 1;
        player.getBookShelf().placeTiles(0, yellowTiles);
        player.getBookShelf().placeTiles(0, pinkTiles);

        assertEquals(oneCell, personalGoalCard.checkScheme(player));
    }

    /**
     * This method tests method checkScheme() in PersonalGoalCardClass when 2 cells are respected.
     * @see PersonalGoalCard#checkScheme(Player)
     */
    @Test
    void checkSchemeTest2Cells(){
        int twoCells = 2 ;
        // First column.
        player.getBookShelf().placeTiles(0, yellowTiles);
        player.getBookShelf().placeTiles(0, pinkTiles);

        // Second column.
        player.getBookShelf().placeTiles(1, yellowTiles);
        player.getBookShelf().placeTiles(1, pinkTiles);

        assertEquals(twoCells, personalGoalCard.checkScheme(player));
    }

    /**
     * This method tests method checkScheme() in PersonalGoalCardClass when 3 cells are respected.
     * @see PersonalGoalCard#checkScheme(Player)
     */
    @Test
    void checkSchemeTest3Cells(){
        int threeCells = 4;
        // First column.
        player.getBookShelf().placeTiles(0, yellowTiles);
        player.getBookShelf().placeTiles(0, pinkTiles);

        // Second column.
        player.getBookShelf().placeTiles(1, yellowTiles);
        player.getBookShelf().placeTiles(1, pinkTiles);

        // Third column.
        player.getBookShelf().placeTiles(2, lightblueTiles);

        assertEquals(threeCells, personalGoalCard.checkScheme(player));
    }

    /**
     * This method tests method checkScheme() in PersonalGoalCardClass when 4 cells are respected.
     * @see PersonalGoalCard#checkScheme(Player)
     */
    @Test
    void checkSchemeTest4Cells(){
        int fourCells = 6;
        // First column.
        player.getBookShelf().placeTiles(0, yellowTiles);
        player.getBookShelf().placeTiles(0, pinkTiles);

        // Second column.
        player.getBookShelf().placeTiles(1, yellowTiles);
        player.getBookShelf().placeTiles(1, pinkTiles);

        // Third column.
        player.getBookShelf().placeTiles(2, lightblueTiles);
        player.getBookShelf().placeTiles(2, blueTiles);

        assertEquals(fourCells, personalGoalCard.checkScheme(player));
    }

    /**
     * This method tests method checkScheme() in PersonalGoalCardClass when 5 cells are respected.
     * @see PersonalGoalCard#checkScheme(Player)
     */
    @Test
    void checkSchemeTest5Cells(){
        int fiveCells = 9;
        // First column.
        player.getBookShelf().placeTiles(0, yellowTiles);
        player.getBookShelf().placeTiles(0, pinkTiles);

        // Second column.
        player.getBookShelf().placeTiles(1, yellowTiles);
        player.getBookShelf().placeTiles(1, pinkTiles);

        // Third column.
        player.getBookShelf().placeTiles(2, lightblueTiles);
        player.getBookShelf().placeTiles(2, blueTiles);

        // Fourth column.
        player.getBookShelf().placeTiles(3, whiteTiles);
        player.getBookShelf().placeTiles(3,  whiteTiles);

        assertEquals(fiveCells, personalGoalCard.checkScheme(player));
    }

    /**
     * This method tests method checkScheme() in PersonalGoalCardClass when 6 cells are respected.
     * @see PersonalGoalCard#checkScheme(Player)
     */
    @Test
    void checkSchemeTest6Cells(){
        int sixCells = 12;
        // First column.
        player.getBookShelf().placeTiles(0, yellowTiles);
        player.getBookShelf().placeTiles(0, pinkTiles);

        // Second column.
        player.getBookShelf().placeTiles(1, yellowTiles);
        player.getBookShelf().placeTiles(1, pinkTiles);

        // Third column.
        player.getBookShelf().placeTiles(2, lightblueTiles);
        player.getBookShelf().placeTiles(2, blueTiles);

        // Fourth column.
        player.getBookShelf().placeTiles(3, whiteTiles);
        player.getBookShelf().placeTiles(3,  whiteTiles);

        // Fifth column.
        player.getBookShelf().placeTiles(4, greenTiles);
        player.getBookShelf().placeTiles(4, greenTiles);

        assertEquals(sixCells, personalGoalCard.checkScheme(player));
    }

    /**
     * This method tests method printCard() in PersonalGoalCard class.
     * @see PersonalGoalCard#printCard()
     */
    @Test
    void printCardTest(){
        personalGoalCard.printCard();
    }
}