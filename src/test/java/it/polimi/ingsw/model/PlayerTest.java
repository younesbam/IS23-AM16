package it.polimi.ingsw.model;

import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.model.cards.PersonalGoalCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    void init(){
        player = new Player(nickName, ID);
        player.setUsername(nickName);
        bs = player.getBookShelf();
        player.setActive(true);
        player.setChair(chair);

        // Personal goal card creation. This player has card 1.
        JSONParser jsonParser = new JSONParser("initSetup.json");
        persCards = jsonParser.getPersonalGoalCards();
        player.setPersonalGoalCard(persCards.get(0));
        persCard = persCards.get(0);
    }

    @Test
    void activityTest(){
        assertTrue(player.isActive());
        player.setActive(false);
        assertFalse(player.isActive());
    }

    @Test
    void usernameTest(){
        assertEquals(nickName, player.getUsername());
        player.setUsername("Pippo");
        assertEquals("Pippo", player.getUsername());
    }

    @Test
    void chairTest(){
        assertTrue(player.hasChair());

        player.setChair(false);
        assertFalse(player.hasChair());
    }

    @Test
    void turnsTest(){
        assertEquals(numTurns, player.getNumOfTurns());

        player.updateNumOfTurns();
        numTurns++;
        assertEquals(numTurns, player.getNumOfTurns());
    }

    @Test
    void persCardTest(){
        assertEquals(persCard, player.getPersonalGoalCard());

        player.setPersonalGoalCard(persCards.get(3));
        assertEquals(persCards.get(3), player.getPersonalGoalCard());
    }
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

    @Test
    void checkPersonalTest(){
        ArrayList<Tile> yellow = new ArrayList<>();
        ArrayList<Tile> pink = new ArrayList<>();
        ArrayList<Tile> white = new ArrayList<>();
        ArrayList<Tile> lightblue = new ArrayList<>();
        ArrayList<Tile> blue = new ArrayList<>();
        ArrayList<Tile> green = new ArrayList<>();

        yellow.add(Tile.YELLOW);
        yellow.add(Tile.YELLOW);
        yellow.add(Tile.YELLOW);

        pink.add(Tile.PINK);
        pink.add(Tile.PINK);
        pink.add(Tile.PINK);

        white.add(Tile.WHITE);
        white.add(Tile.WHITE);
        white.add(Tile.WHITE);

        lightblue.add(Tile.LIGHTBLUE);
        lightblue.add(Tile.LIGHTBLUE);
        lightblue.add(Tile.LIGHTBLUE);

        blue.add(Tile.BLUE);
        blue.add(Tile.BLUE);
        blue.add(Tile.BLUE);

        green.add(Tile.GREEN);
        green.add(Tile.GREEN);
        green.add(Tile.GREEN);

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

    @Test
    void idTest(){
        assertEquals(ID, player.getID());
    }
}