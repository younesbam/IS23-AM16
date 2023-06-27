package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DiffAlignedTest class tests class DiffAligned in model.
 * @see DiffAligned
 */
class DiffAlignedTest extends CardTest {

    CommonGoalCard common2 = new DiffAligned(2);
    CommonGoalCard common6 = new DiffAligned(6);

    /**
     * This method tests whether the algorithm recognizes properly the scheme.
     * Tested schemes are saved in testCommonCard.json
     * @param cardID card to test (2 or 6).
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 6})
    public void checkAlgorithm(int cardID) {
        JSONArray test = getBookshelfFromJSON(cardID);
        // Iterate all the objects in the array. Each element is an object of boh, it's hidden now.
        for(int j=0; j< test.length(); j++){
            // New player.
            player = new Player("MarioRossi", 20);
            // Common goal card instantiated.
            commonCard = new DiffAligned(cardID);
            // Test with 2 players.
            commonCard.placePoints(2);

            // Get each object in a variable. Now we don't know what's inside
            JSONObject objInTest = test.getJSONObject(j);
            // Print which test is running.
            System.out.println(objInTest.getString("descr"));
            // Get coordinates.
            JSONArray coordinates = objInTest.getJSONArray("coordinates");
            // Iterate all the objects in the array. Each element is an object of boh, it's hidden now.
            for(int k=0; k< coordinates.length(); k++){
                // Get each object in a variable. Now we don't know what's inside
                JSONObject objInCoordinates = coordinates.getJSONObject(k);

                int col = objInCoordinates.getInt("x");
                Tile tile = Tile.valueOf(objInCoordinates.getString("type").toUpperCase());
                list.clear();
                list.add(tile);

                player.getBookShelf().placeTiles(col, list);
            }
            /*
             * Se lo schema è rispettato:
             * assertEquals(commonCard.checkScheme(player), commonCard.getScore());
             *
             * Se lo schema non è rispettato:
             * assertEquals(commonCard.checkScheme(player), 0);
             */
            boolean valid = objInTest.getBoolean("valid");
            if(valid)
                assertEquals(8, commonCard.checkScheme(player));
            else
                assertEquals(0, commonCard.checkScheme(player));
        }
    }

    /**
     * This method tests method getCardNumber() in Card class.
     * @see Card#getCardNumber()
     */
    @Test
    void getCardNumberTest(){
        assertEquals(2, common2.getCardNumber());
        assertEquals(6, common6.getCardNumber());
    }

    /**
     * This method tests method checkScheme() in Card class.
     * @see Card#checkScheme(Player)
     */
    @Test
    void checkSchemeTest(){
        Player player = new Player("Pippo", 1);

        assertNotNull(common2.checkScheme(player));
        assertNotNull(common6.checkScheme(player));
    }

    /**
     * This method tests methods getScore() and placePoints() in CommonGoalCard class.
     * @see CommonGoalCard#placePoints(int)
     * @see CommonGoalCard#getScore()
     */
    @Test
    void pointsTest(){
        // Returns null because game hasn't been instantiated.
        assertNull(common2.getScore());
        assertNull(common6.getScore());

        common2.placePoints(2);
        assertEquals(8, common2.getScore());
        assertEquals(4, common2.getScore());

        common6.placePoints(3);
        assertEquals(8, common6.getScore());
        assertEquals(6, common6.getScore());
        assertEquals(4, common6.getScore());
    }

    /**
     * This method tests the method printCard() in DiffAligned class.
     * @see DiffAligned#printCard()
     */
    @Test
    void printCardTest(){
        common2.printCard();
        common6.printCard();
    }
}