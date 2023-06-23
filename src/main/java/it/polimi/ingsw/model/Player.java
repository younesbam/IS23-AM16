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
 */
public class Player implements Serializable {
    /**
     * Player's username.
     */
    private String username;
    /**
     * Player's ID.
     */
    private final int ID;
    /**
     * Total points earned by the player.
     */
    private int totalPoints;
    /**
     * Player's bookshelf.
     */
    private BookShelf bookShelf;
    /**
     * Player's personal goal card.
     */
    private PersonalGoalCard personalGoalCard;

    /**
     * The chair is used to specify whether the player is the first to play.
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

    /**
     * Specifies if the player is still active during a game.
     */
    private boolean active;

    /**
     * Activity getter.
     * @return the status of the player.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the current status of the player.
     * @param active true if the player's active, false otherwise.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Player constructor.
     * @param username username chosen by the player
     * @param ID player's ID
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
     * Username setter.
     * @return username chosen by the player.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Player's username setter.
     * @param nickname chosen by the player.
     */
    public void setUsername(String nickname) {
        this.username = nickname;
    }

    /**
     * Chair getter.
     * @return boolean value representing the chair.
     */
    public boolean hasChair() {
        return this.chair;
    }

    /**
     * Chair setter.
     * @param chair true if the player has the  chair, false otherwise.
     */
    public void setChair(boolean chair){
        this.chair = chair;
    }

    /**
     * Bookshelf getter.
     * @return player's bookshelf.
     */
    public BookShelf getBookShelf() {
        return bookShelf;
    }

    /**
     * Number of turns getter.
     * @return number of played turns.
     */
    public int getNumOfTurns() {
        return numOfTurns;
    }

    /**
     * Method used to update the number of turns played.
     */
    public void updateNumOfTurns() {
        this.numOfTurns++;
    }

    /**
     * Player points getter.
     * @return total points earned by the player.
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
     * Getter of points earned from the common goal card.
     * @param i index of the card.  This is strictly related to the index of the common goal card in Game class.
     * @return points earned from that common goal card.
     */
    public int getCommonCardPoints(int i) {
        return this.commonCardPoints.get(i);
    }


    /**
     * Setter of points earned from the common goal card.
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
     */
    public void setPersonalGoalCard(PersonalGoalCard card) {
        this.personalGoalCard = card;
    }


    /**
     * Personal goal card getter.
     * @return player's personal goal card.
     */
    public PersonalGoalCard getPersonalGoalCard() {
        return this.personalGoalCard;
    }


    /**
     * ID getter.
     * @return player's ID.
     */
    public int getID(){
        return this.ID;
    }

}
