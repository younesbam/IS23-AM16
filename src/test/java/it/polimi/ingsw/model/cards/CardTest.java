package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Tile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class CardTest {
    /*
    List of picked tiles.
     */
    List<Tile> list = new ArrayList<>();
    /*
    Player.
     */
    Player player = new Player("MarioRossi", 20);
    /*
    Common goal card.
     */
    CommonGoalCard commonCard;

    /*
    ATTENTION: le carte all'interno del file json devono essere inserite in ordine di caduta.
    Ovvero, si parte dall'indice più alto che sta in basso a quello più basso che sta in alto.
    Questo permette di usare il metodo placeTiles per inserire le tessere lette da json.
     */
    JSONArray getBookshelfFromJSON(int cardID) {
        String jsonString = Utils.convertFileIntoString("json/testCommonCard.json");
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