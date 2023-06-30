package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EqualCornersTest class tests EqualCorners class in model.
 * @see EqualCorners
 */
class EqualCornersTest extends CardTest{

    CommonGoalCard common8 = new EqualCorners(8);

    /**
     * This method tests whether the algorithm recognizes properly the scheme.
     * Tested schemes are saved in testCommonCard.json
     * @param cardID card to test (8).
     */
    @ParameterizedTest
    @ValueSource(ints = {8})
    public void checkAlgorithm(int cardID) {
        JSONArray test = getBookshelfFromJSON(cardID);
        // Iterate all the objects in the array. Each element is an object hidden at the moment.
        for(int j=0; j< test.length(); j++){
            // New player.
            player = new Player("MarioRossi", 20);
            // Common goal card instantiated.
            commonCard = new EqualCorners(cardID);
            // Test with 2 players.
            commonCard.placePoints(2);

            // Get each object in a variable. Now we don't know what's inside
            JSONObject objInTest = test.getJSONObject(j);
            // Print which test is running.
            System.out.println(objInTest.getString("descr"));
            // Get coordinates.
            JSONArray coordinates = objInTest.getJSONArray("coordinates");
            // Iterate all the objects in the array. Each element is an object hidden at the moment.
            for(int k=0; k< coordinates.length(); k++){
                // Get each object in a variable. Now we don't know what's inside
                JSONObject objInCoordinates = coordinates.getJSONObject(k);

                int col = objInCoordinates.getInt("x");
                Tile tile = Tile.valueOf(objInCoordinates.getString("type").toUpperCase());
                list.clear();
                list.add(tile);

                player.getBookShelf().placeTiles(col, list);
            }
            boolean valid = objInTest.getBoolean("valid");
            if(valid)
                assertEquals(8, commonCard.checkScheme(player));        // Valid scheme.
            else
                assertEquals(0, commonCard.checkScheme(player));        // Invalid scheme.
        }
    }

    /**
     * This method tests method getCardNumber() in Card class.
     * @see Card#getCardNumber()
     */
    @Test
    void getCardNumberTest(){
        assertEquals(8, common8.getCardNumber());
    }

    /**
     * This method tests method checkScheme() in Card class.
     * @see Card#checkScheme(Player)
     */
    @Test
    void checkSchemeTest(){
        Player player = new Player("Pippo", 1);

        assertNotNull(common8.checkScheme(player));
    }

    /**
     * This method tests methods getScore() and placePoints() in CommonGoalCard class.
     * @see CommonGoalCard#placePoints(int)
     * @see CommonGoalCard#getScore()
     */
    @Test
    void pointsTest(){
        assertNull(common8.getScore());

        common8.placePoints(4);
        assertEquals(8, common8.getScore());
        assertEquals(6, common8.getScore());
        assertEquals(4, common8.getScore());
        assertEquals(2, common8.getScore());
    }

    /**
     * This method tests the method printCard() in EqualCorners class.
     * @see EqualCorners#printCard()
     */
    @Test
    void printCardTest(){
        common8.printCard();
    }
}