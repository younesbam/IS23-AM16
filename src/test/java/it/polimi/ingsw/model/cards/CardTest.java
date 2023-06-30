package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * CardTest class instantiates the Card class to run tests on all Cards.
 */
class CardTest {
    List<Tile> list = new ArrayList<>();    // List of picked tiles.
    Player player;  // Player.
    CommonGoalCard commonCard;  // Common goal card.

    /*
    WARNING: cards in the json file must be inserted as they are inserted into the bookshelf:
    the highest index is the one in the bottom, while the lowest index is on the top.
    This lets us use method placeTiles() to insert the tiles contained in the json file.
     */

    /**
     * This method takes the schemes to check from file .json.
     * @param cardID number of the selected card.
     * @return array containing tests for the selected card.
     */
    JSONArray getBookshelfFromJSON(int cardID) {
        String jsonString = Utils.convertFileIntoString("/json/testCommonCard.json");
        // Get the json object from the string
        JSONObject obj = new JSONObject(jsonString);
        // Get cardTests array
        JSONArray cardTests = obj.getJSONArray("cardTests");
        // Iterate all the objects in the cardTests array. Each element is an object of boh, it's hidden now.
        for (int i = 0; i < cardTests.length(); i++) {
            // Get each object in a variable. Now we don't know what's inside
            JSONObject objInCardTests = cardTests.getJSONObject(i);
            int ID = objInCardTests.getInt("ID");
            // Check if the tests are related to the passed card ID.
            if (ID == cardID) {
                System.out.println(objInCardTests.getString("descr"));
                JSONArray test = objInCardTests.getJSONArray("test");
                return test;
            }
        }
        return null;
    }
}