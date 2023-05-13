package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Tile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EqualInColTest extends CardTest{

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    public void checkAlgorithm(int cardID) {
        // Common goal card instantiated.
        commonCard = new EqualInCol(cardID);
        // Test with 2 players.
        commonCard.placePoints(2);

        JSONArray test = getBookshelfFromJSON(cardID);
        // Iterate all the objects in the array. Each element is an object of boh, it's hidden now.
        for(int j=0; j< test.length(); j++){
            // Get each object in a variable. Now we don't know what's inside
            JSONObject objInTest = test.getJSONObject(j);
            JSONArray coordinates = objInTest.getJSONArray("bookshelf");
            // Iterate all the objects in the array. Each element is an object of boh, it's hidden now.
            for(int k=0; k< coordinates.length(); k++){
                // Get each object in a variable. Now we don't know what's inside
                JSONObject objInCoordinates = test.getJSONObject(k);

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
                assertEquals(commonCard.checkScheme(player), commonCard.getScore());
            else
                assertEquals(commonCard.checkScheme(player), 0);
        }
    }
}