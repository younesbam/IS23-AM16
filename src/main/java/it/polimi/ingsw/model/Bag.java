package it.polimi.ingsw.model;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.common.exceptions.InvalidDirectoryException;
import it.polimi.ingsw.model.cards.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static it.polimi.ingsw.model.BookShelf.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.model.BookShelf.MAXBOOKSHELFROW;

/**
 * Represent the black bag with all the shuffled cards and tiles inside.
 * @author Nicolo' Gandini
 */
public class Bag {
    public static final int MAXCARDS = 12;

    private Set<CommonGoalCard> initCommSet;
    private Set<PersonalGoalCard> initPersSet;
    private Queue<CommonGoalCard> commCards;
    private Queue<PersonalGoalCard> persCards;

    /**
     * Initialize all the tiles inside the bag into a Set.
     */
    public Bag() {
        // Create Sets to insert random cards.
        initCommSet = new HashSet<>();
        initPersSet = new HashSet<>();

        /*
        Common card initialization
         */
        // Add common cards in the Set
        initCommSet.add(new EqualCross(1));
        initCommSet.add(new EqualCross(10));
        initCommSet.add(new DiffAligned(2));
        initCommSet.add(new DiffAligned(6));
        initCommSet.add(new EqualInCol(3));
        initCommSet.add(new EqualInCol(4));
        initCommSet.add(new MaxDiffGroup(5));
        initCommSet.add(new MaxDiffGroup(7));
        initCommSet.add(new EqualCorners(8));
        initCommSet.add(new EqualRand(9));
        initCommSet.add(new EqualDiag(11));
        initCommSet.add(new SubMatrix(12));

        /*
        Personal card read from json
         */
        Hashtable<Integer, Integer> persCardPoints = new Hashtable<>();
        Cell[][] personalCardGrid = Utils.createBlankGrid(MAXBOOKSHELFROW, MAXBOOKSHELFCOL);

        String jsonString = Utils.convertFileIntoString("json/initSetup.json");
        // Get the json object from the string
        JSONObject obj = new JSONObject(jsonString);
        // Get personal cards array
        JSONArray personalCardsArray = obj.getJSONArray("personalCards");
        // Iterate all the objects in the cards array. Each element is an object of boh, it's hidden now.
        for(int i=0; i<personalCardsArray.length(); i++){
            // Get each object in a variable. Now we don't know what's inside
            JSONObject objInPersonalCardsArray = personalCardsArray.getJSONObject(i);
            // Get the descr key that must be inside the mystery object
            int descr = objInPersonalCardsArray.getInt("descr");
            // Get the coordinates key array and pointsTable that must be inside the mystery object
            JSONArray coordinateArrayInObj = objInPersonalCardsArray.getJSONArray("coordinates");
            JSONArray pointsTableArrayInObj = objInPersonalCardsArray.getJSONArray("pointsTable");
            // Iterate the pointsTable array. Again, each element is an object of boh, it's hidden now
            for(int j=0; j<pointsTableArrayInObj.length(); j++){
                // Get each object in a variable. Now we don't know what's inside
                JSONObject objInPointsTable = pointsTableArrayInObj.getJSONObject(j);
                // Get all the useful keys inside the mystery object
                int matches = objInPointsTable.getInt("matches");
                int points = objInPointsTable.getInt("points");
                persCardPoints.put(matches, points);
            }
            // Iterate the coordinate array. Again, each element is an object of boh, it's hidden now
            for(int j=0; j<coordinateArrayInObj.length(); j++){
                // Get each object in a variable. Now we don't know what's inside
                JSONObject objInCoordinate = coordinateArrayInObj.getJSONObject(j);
                // Get all the useful keys inside the mystery object
                int x = objInCoordinate.getInt("x");
                int y = objInCoordinate.getInt("y");
                Tile tile = Tile.valueOf(objInCoordinate.getString("type").toUpperCase());

                personalCardGrid[y][x].setTile(tile);
            }
            // Save all the retrieved information into the card
            initPersSet.add(new PersonalGoalCard(descr, persCardPoints, personalCardGrid));
            // Reset all the previous grid and points.
            persCardPoints.clear();
            personalCardGrid = Utils.createBlankGrid(MAXBOOKSHELFROW, MAXBOOKSHELFCOL);
        }

        /*
        Final operations
         */
        // Transform the Set into priority queue. Useful to poll the first element.
        commCards = new PriorityQueue<>(initCommSet);
        persCards = new PriorityQueue<>(initPersSet);
    }

    public CommonGoalCard pickCommonGoalCard(int playerNum) throws NullPointerException {
        CommonGoalCard comCard;
        comCard = commCards.poll();
        if(comCard == null) throw new NullPointerException("Common card's deck is empty");
        comCard.placePoints(playerNum);
        return comCard;
    }

    public PersonalGoalCard pickPersonalGoalCard(){
        return persCards.poll();
    }
}
