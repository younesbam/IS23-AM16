package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EqualCrossTest class tests EqualCross class in model.
 * @see EqualCross
 */
class EqualCrossTest extends CardTest{

    CommonGoalCard common1 = new EqualCross(1);
    CommonGoalCard common10 = new EqualCross(10);

    /**
     * This method tests whether the algorithm recognizes properly the scheme.
     * Tested schemes are saved in testCommonCard.json
     * @param cardID card to test (1 or 10).
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    public void checkAlgorithm(int cardID) {
        JSONArray test = getBookshelfFromJSON(cardID);
        // Iterate all the objects in the array. Each element is an object hidden at the moment.
        for(int j=0; j<test.length(); j++){
            // New player.
            player = new Player("MarioRossi", 20);
            // Common goal card instantiated.
            commonCard = new EqualCross(cardID);
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
        assertEquals(1, common1.getCardNumber());
        assertEquals(10, common10.getCardNumber());
    }

    /**
     * This method tests method checkScheme() in Card class.
     * @see Card#checkScheme(Player)
     */
    @Test
    void checkSchemeTest(){
        Player player = new Player("Pippo", 1);

        assertNotNull(common10.checkScheme(player));
        assertNotNull(common1.checkScheme(player));
    }

    /**
     * This method tests methods getScore() and placePoints() in CommonGoalCard class.
     * @see CommonGoalCard#placePoints(int)
     * @see CommonGoalCard#getScore()
     */
    @Test
    void pointsTest(){
        assertNull(common1.getScore());
        assertNull(common10.getScore());

        common1.placePoints(4);
        assertEquals(8, common1.getScore());
        assertEquals(6, common1.getScore());
        assertEquals(4, common1.getScore());
        assertEquals(2, common1.getScore());

        common10.placePoints(2);
        assertEquals(8, common10.getScore());
        assertEquals(4, common10.getScore());
    }

    /**
     * This method tests the method printCard() in EqualCross class.
     * @see EqualCross#printCard()
     */
    @Test
    void printCardTest(){
        common1.printCard();
        common10.printCard();
    }
}