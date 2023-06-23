package it.polimi.ingsw.common;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.cards.PersonalGoalCard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.Const.MAXBOOKSHELFROW;
import static it.polimi.ingsw.Const.MAXBOARDDIM;

/**
 * Easily get what's inside the json file
 */
public class JSONParser {
    private final String fileName;


    /**
     * Constructor
     * @param fileName name of the file. If there are subdirectories, specifies them in the string. In case of subdirectories, don't add the initial "/". All the files must be in "resources" directory
     */
    public JSONParser(String fileName) {
        this.fileName = "/json/" + fileName;
    }


    /**
     * Get all the personal goal card stored in the json file.
     * @return Set of PersonalGoalCard
     */
    public LinkedList<PersonalGoalCard> getPersonalGoalCards(){
        LinkedList<PersonalGoalCard> list = new LinkedList<>();
        Hashtable<Integer, Integer> persCardPoints = new Hashtable<>();
        String jsonString = Utils.convertFileIntoString(fileName);
        // Get the json object from the string
        JSONObject obj = new JSONObject(jsonString);
        // Get personal cards array
        JSONArray personalCardsArray = obj.getJSONArray("personalCards");
        // Iterate all the objects in the cards array. Each element is an object of boh, it's hidden now.
        for (int i = 0; i < personalCardsArray.length(); i++) {
            // Reset all the previous grid and points.
            persCardPoints.clear();
            // Create the blank grid
            Cell[][] personalCardGrid = Utils.createBlankGrid(MAXBOOKSHELFROW, MAXBOOKSHELFCOL);
            // Get each object in a variable. Now we don't know what's inside
            JSONObject objInPersonalCardsArray = personalCardsArray.getJSONObject(i);
            // Get the descr key that must be inside the mystery object
            int descr = objInPersonalCardsArray.getInt("descr");
            // Get the coordinates key array and pointsTable that must be inside the mystery object
            JSONArray coordinateArrayInObj = objInPersonalCardsArray.getJSONArray("coordinates");
            JSONArray pointsTableArrayInObj = objInPersonalCardsArray.getJSONArray("pointsTable");
            // Iterate the pointsTable array. Again, each element is an object of boh, it's hidden now
            for (int j = 0; j < pointsTableArrayInObj.length(); j++) {
                // Get each object in a variable. Now we don't know what's inside
                JSONObject objInPointsTable = pointsTableArrayInObj.getJSONObject(j);
                // Get all the useful keys inside the mystery object
                int matches = objInPointsTable.getInt("matches");
                int points = objInPointsTable.getInt("points");
                persCardPoints.put(matches, points);
            }
            // Iterate the coordinate array. Again, each element is an object of boh, it's hidden now
            for (int j = 0; j < coordinateArrayInObj.length(); j++) {
                // Get each object in a variable. Now we don't know what's inside
                JSONObject objInCoordinate = coordinateArrayInObj.getJSONObject(j);
                // Get all the useful keys inside the mystery object
                int x = objInCoordinate.getInt("x");
                int y = objInCoordinate.getInt("y");
                Tile tile = Tile.valueOf(objInCoordinate.getString("type").toUpperCase());

                personalCardGrid[y][x].setTile(tile);
            }
            // Save all the retrieved information into the card
            list.add(new PersonalGoalCard(descr, persCardPoints, personalCardGrid));
        }
        return list;
    }


    /**
     * Get the board based on the number of players
     * @param numOfPlayers number of players
     * @return board
     */
    public Cell[][] getBoard(int numOfPlayers) {
        // Create the blank grid
        Cell[][] board = Utils.createBlankGrid(MAXBOARDDIM, MAXBOARDDIM);
        JSONObject objInBoardArray = null;

        String jsonString = Utils.convertFileIntoString(fileName);
        // Get the json object from the string
        JSONObject obj = new JSONObject(jsonString);
        // Get board array
        JSONArray boardArray = obj.getJSONArray("boards");
        // Iterate all the objects in the board array. Each element is an object of boh, it's hidden now.
        for (int i = 0; i < boardArray.length(); i++) {
            // Get each object in a variable. Now we don't know what's inside
            objInBoardArray = boardArray.getJSONObject(i);
            // Get the descr key that must be inside the mystery object
            int players = objInBoardArray.getInt("players");
            if(players == numOfPlayers){
                break;
            }
        }
        // Get the coordinates key array that must be inside the mystery object
        JSONArray coordinateArrayInObj = objInBoardArray.getJSONArray("coordinates");
        // Iterate the coordinate array. Again, each element is an object of boh, it's hidden now
        for (int j = 0; j < coordinateArrayInObj.length(); j++) {
            // Get each object in a variable. Now we don't know what's inside
            JSONObject objInCoordinate = coordinateArrayInObj.getJSONObject(j);
            // Get all the useful keys inside the mystery object
            int x = objInCoordinate.getInt("x");
            int y = objInCoordinate.getInt("y");
            Tile tile = Tile.valueOf(objInCoordinate.getString("type").toUpperCase());

            board[y][x].setTile(tile);
        }
        return board;
    }


    /**
     * Get port of the server with RMI.
     * @return server port
     */
    public int getServerRmiPort(){ return getJSONObject().getInt("RmiPort"); }


    /**
     * Get port of the server with socket.
     * @return server port
     */
    public int getServerSocketPort(){ return getJSONObject().getInt("SocketPort"); }


    /**
     * Get server name.
     * @return server name
     */
    public String getServerName(){ return getJSONObject().getString("ServerName"); }


    /**
     * Get the initial JSONObject, that start with the first {}
     * @return initial json object
     */
    private JSONObject getJSONObject() {
        String jsonString = Utils.convertFileIntoString(fileName);
        // Get the json object from the string
        return new JSONObject(jsonString);
    }
}
