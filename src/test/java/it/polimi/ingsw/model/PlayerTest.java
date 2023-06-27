package it.polimi.ingsw.model;

import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.model.cards.PersonalGoalCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PlayerTest class tests Player class in model.
 * @see Player
 */
class PlayerTest {
    Player player;
    BookShelf bs = new BookShelf();
    final static String nickName = "MarioRossi";
    final static Boolean chair = true;
    int numTurns = 0;
    int points = 0;
    int common1points = 8;
    int common2points = 4;
    static final int ID = 20;
    PersonalGoalCard persCard;
    LinkedList<PersonalGoalCard> persCards = new LinkedList<>();

    /**
     * This method instantiates the player to run tests.
     */
    @BeforeEach
    void init(){
        player = new Player(nickName, ID);
        bs = player.getBookShelf();
        player.setActive(true);
        player.setChair(chair);

        // Personal goal card creation. This player has card 1.
        JSONParser jsonParser = new JSONParser("initSetup.json");
        persCards = jsonParser.getPersonalGoalCards();
        player.setPersonalGoalCard(persCards.get(0));
        persCard = persCards.get(0);
    }

    /**
     * This method tests methods setActive() and isActive() in Player class.
     * @see Player#setActive(boolean)
     * @see Player#isActive()
     */
    @Test
    void activityTest(){
        assertTrue(player.isActive());

        player.setActive(false);
        assertFalse(player.isActive());
    }

    /**
     * This method tests methods setUsername() and getUsername() in Player class.
     * @see Player#setUsername(String)
     * @see Player#getUsername()
     */
    @Test
    void usernameTest(){
        assertEquals(nickName, player.getUsername());

        player.setUsername("Pippo");
        assertEquals("Pippo", player.getUsername());
    }

    /**
     * This method tests methods setChair() and hasChair() in Player class.
     * @see Player#hasChair()
     * @see Player#setChair(boolean)
     */
    @Test
    void chairTest(){
        assertTrue(player.hasChair());

        player.setChair(false);
        assertFalse(player.hasChair());
    }

    /**
     * This method tests method getBookShelf() in Player class.
     * @see Player#getBookShelf()
     */
    @Test
    void getBookShelfTest(){
        assertNotNull(player.getBookShelf());
    }

    /**
     * This method tests methods getNumOfTurns() and updateNumOfTurns() in Player class.
     * @see Player#updateNumOfTurns()
     * @see Player#getNumOfTurns()
     */
    @Test
    void turnsTest(){
        assertEquals(numTurns, player.getNumOfTurns());

        player.updateNumOfTurns();
        numTurns++;
        assertEquals(numTurns, player.getNumOfTurns());
    }

    /**
     * This method tests methods setPersonalGoalCard() and getPersonalGoalCard() in Player class
     * @see Player#setPersonalGoalCard(PersonalGoalCard)
     * @see Player#getPersonalGoalCard()
     */
    @Test
    void personalGoalCardTests(){
        assertEquals(persCard, player.getPersonalGoalCard());

        player.setPersonalGoalCard(persCards.get(3));
        assertEquals(persCards.get(3), player.getPersonalGoalCard());
    }

    /**
     * This method tests methods that assign points for common and personal goal cards, method that updates total points,
     * method that checks whether the bookshelf is full.
     * @see Player#updateTotalPoints()
     * @see Player#getTotalPoints()
     * @see Player#getCommonCardPoints(int)
     * @see Player#setCommonCardPoints(int, int)
     * @see Player#checkFullBookshelf()
     */
    @Test
    void pointsTest(){
        // Total points.
        player.updateTotalPoints();
        assertEquals(points, player.getTotalPoints());

        // Common cards points.
        assertEquals(points, player.getCommonCardPoints(0));
        assertEquals(points, player.getCommonCardPoints(1));
        player.setCommonCardPoints(0, common1points);
        player.setCommonCardPoints(1, common2points);
        player.updateTotalPoints();
        player.checkFullBookshelf();

        // The bookshelf isn't full so the game doesn't end.
        int firstPoint = 0;
        points = common1points + common2points + firstPoint;
        assertEquals(points, player.getTotalPoints());
        assertEquals(common1points, player.getCommonCardPoints(0));
        assertEquals(common2points, player.getCommonCardPoints(1));
    }

    /**
     * This method tests that at the end of the game points are calculated correclty.
     * @see Player#getTotalPoints()
     * @see Player#setCommonCardPoints(int, int)
     * @see Player#getCommonCardPoints(int)
     * @see Player#checkPersonalGoalCardScheme()
     * @see Player#getTotalPoints()
     * @see Player#checkFullBookshelf()
     * @see Player#checkAdjacentTiles()
     * @see Player#updateTotalPoints()
     */
    @Test
    void checkPersonalTest(){
        // Initialization of lists of tiles to fill the bookshelf.
        ArrayList<Tile> yellow = new ArrayList<>();
        ArrayList<Tile> pink = new ArrayList<>();
        ArrayList<Tile> white = new ArrayList<>();
        ArrayList<Tile> lightblue = new ArrayList<>();
        ArrayList<Tile> blue = new ArrayList<>();
        ArrayList<Tile> green = new ArrayList<>();

        // List of 3 yellow tiles.
        yellow.add(Tile.YELLOW);
        yellow.add(Tile.YELLOW);
        yellow.add(Tile.YELLOW);

        // List of 3 pink tiles.
        pink.add(Tile.PINK);
        pink.add(Tile.PINK);
        pink.add(Tile.PINK);

        // List of 3 white tiles.
        white.add(Tile.WHITE);
        white.add(Tile.WHITE);
        white.add(Tile.WHITE);

        // List of 3 lightblue tiles.
        lightblue.add(Tile.LIGHTBLUE);
        lightblue.add(Tile.LIGHTBLUE);
        lightblue.add(Tile.LIGHTBLUE);

        // List of 3 blue tiles.
        blue.add(Tile.BLUE);
        blue.add(Tile.BLUE);
        blue.add(Tile.BLUE);

        // List of 3 green tiles.
        green.add(Tile.GREEN);
        green.add(Tile.GREEN);
        green.add(Tile.GREEN);

        // Filling all the bookshelf to respect all the personal goal card scheme.
        player.getBookShelf().placeTiles(0, pink);
        player.getBookShelf().placeTiles(0, pink);
        player.getBookShelf().placeTiles(1, yellow);
        player.getBookShelf().placeTiles(1, yellow);
        player.getBookShelf().placeTiles(2, lightblue);
        player.getBookShelf().placeTiles(2, blue);
        player.getBookShelf().placeTiles(3, lightblue);
        player.getBookShelf().placeTiles(3, white);
        player.getBookShelf().placeTiles(4, white);
        player.getBookShelf().placeTiles(4, green);

        player.checkPersonalGoalCardScheme();

        assertEquals(points, player.getTotalPoints());

        // The personal goal card's scheme is respected, so the player should earn 12 points.
        int persPoints = 12;
        // The player has also earned 3 points for adjacent tiles.
        int adPoints = 30;
        // Assuming that the player is the first who finishes.
        int firstPoint = 1;
        // Setting also common points.
        player.setCommonCardPoints(0, common1points);
        player.setCommonCardPoints(1, common2points);
        points = persPoints + adPoints + common2points + common1points + firstPoint;
        player.checkPersonalGoalCardScheme();
        player.checkFullBookshelf();
        player.checkAdjacentTiles();
        player.updateTotalPoints();
        assertEquals(points, player.getTotalPoints());
    }

    /**
     * This method tests method getID() in Player class.
     * @see Player#getID()
     */
    @Test
    void getIDTest(){
        assertEquals(ID, player.getID());
    }
}