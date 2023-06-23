package it.polimi.ingsw.model;

import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.model.cards.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.Const.*;

/**
 * This class represent the PLAYER model.
 * @author Younes Bamhaoud
 */
public class Player implements Serializable {
    private String username;
    private final int ID;
    private int totalPoints;
    private BookShelf bookShelf;
    private PersonalGoalCard personalGoalCard;

    /**
     * Specify if the player is the FIRST of the round in all the game
     */
    private boolean chair;

    /**
     * Map that contains the points earned during the game from the related common card.
     */
    private final List<Integer> commonCardPoints;

    /**
     * Points earned during the game from the related personal card.
     */
    private int personalGoalCardPoints = 0;

    private int adjacentTilesPoints = 0;

    /**
     * Saves number of turns played. Useful to save the state of the game.
     */
    private int numOfTurns;

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Player constructor.
     * @param username
     * @param ID
     */
    public Player(String username, Integer ID){
        this.bookShelf = new BookShelf();
        this.username = username;
        this.ID = ID;
        this.commonCardPoints = new ArrayList<>();
        // Add 2 common goal card points.
        this.commonCardPoints.add(0);
        this.commonCardPoints.add(0);
        this.active = true;
        this.numOfTurns = 0;
    }

    /**
     * Player's username getter.
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Player's username setter.
     * @param nickname
     */
    public void setUsername(String nickname) {
        this.username = nickname;
    }

    /**
     * This method returns the boolean value representing if a player has the first player chair or not.
     * @return
     */
    public boolean hasChair() {
        return this.chair;
    }

    /**
     * CHair setter.
     * @param chair
     */
    public void setChair(boolean chair){
        this.chair = chair;
    }

    /**
     * BookShelf getter.
     * @return
     */
    public BookShelf getBookShelf() {
        return bookShelf;
    }

    /**
     * Number of turns getter.
     * @return
     */
    public int getNumOfTurns() {
        return numOfTurns;
    }

    /**
     * Method used to set the number of turns played.
     */
    public void updateNumOfTurns() {
        this.numOfTurns++;
    }

    /**
     * Player points getter.
     * @return
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * Updates total points based on points earned from personal and common cards.
     */
    public void updateTotalPoints() {
        int total=0;
        for(int i = 0; i< commonCardPoints.size(); i++){
            total += commonCardPoints.get(i);
        }
        total += personalGoalCardPoints;
        total += adjacentTilesPoints;
        this.totalPoints = total;
    }


    /**
     * Get points earned form the common goal card.
     * @param i index of the card.  This is strictly related to the index of the common goal card in Game class.
     * @return points earned from that common goal card.
     */
    public int getCommonCardPoints(int i) {
        return this.commonCardPoints.get(i);
    }


    /**
     * Set points earned form the common goal card.
     * @param i index of the card.  This is strictly related to the index of the common goal card in Game class.
     * @param value points earned from that common goal card.
     */
    public void setCommonCardPoints(int i, int value) {
        this.commonCardPoints.set(i, value);
    }


    /**
     * Check personal goal card scheme and automatically update points.
     */
    public void checkPersonalGoalCardScheme(){
        personalGoalCardPoints = personalGoalCard.checkScheme(this);
    }

    /**
     * Check adjacent tiles in order to assign extra points.
     */
    public void checkAdjacentTiles(){
        Map<Integer, Integer> assignablePoints = new HashMap<>();
        assignablePoints.put(3, 2);  // group of 3 adjacent tiles, 2 points.
        assignablePoints.put(4, 3);  // group of 4 adjacent tiles, 3 points.
        assignablePoints.put(5, 5);  // group of 5 adjacent tiles, 5 points.
        assignablePoints.put(6, 8);  // group of 3 adjacent tiles, 8 points.
        for(int i=7; i<MAXTILES; i++){  // group of 6+ tiles, 8 points.
            assignablePoints.put(i, 8);  // group of i adjacent tiles, 8 points.
        }

        Boolean[][] visited = new Boolean[MAXBOOKSHELFROW][MAXBOOKSHELFCOL];
        Map<Tile, List<Coordinate>> tiles = new HashMap<>();
        for(Tile tile : Tile.values()){
            if(tile != Tile.UNAVAILABLE && tile != Tile.BLANK)
                tiles.put(tile, new ArrayList<>());
        }

        for (int i = 0; i< MAXBOOKSHELFROW; i++) {
            for(int j = 0; j< MAXBOOKSHELFCOL; j++) {
                //
            }
        }
    }


    /**
     * Personal goal card setter.
     * @param card Personal goal card, picked from the bag
     * @return
     */
    public void setPersonalGoalCard(PersonalGoalCard card) {
        this.personalGoalCard = card;
    }


    /**
     * Personal goal card getter.
     * @return
     */
    public PersonalGoalCard getPersonalGoalCard() {
        return this.personalGoalCard;
    }


    /**
     * ID getter.
     * @return
     */
    public int getID(){
        return this.ID;
    }

}
