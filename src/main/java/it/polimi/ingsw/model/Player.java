package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    private final Map<CommonGoalCard, Integer> commonCardPointsEarned;

    /**
     * Points earned during the game from the related personal card.
     */
    private int personalGoalCardPointsEarned = 0;

    /**
     * Saves number of turns played. Useful to save the state of the game.
     */
    private int numOfTurns;



    /**
     * Player constructor.
     * @param username
     * @param ID
     */
    public Player(String username, Integer ID){
        this.username = username;
        this.ID = ID;
        this.commonCardPointsEarned = new HashMap<>();
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
        return bookShelf; }

    /**
     * Bookshelf setter.
     * @param bookShelf
     */
    public void setBookShelf(BookShelf bookShelf){
        this.bookShelf = bookShelf;
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
     * @param numOfTurns
     */
    public void setNumOfTurns(int numOfTurns) {
        this.numOfTurns = numOfTurns;
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
        for(CommonGoalCard card : commonCardPointsEarned.keySet()){
            total += commonCardPointsEarned.get(card);
        }
        total += personalGoalCardPointsEarned;
        this.totalPoints = total;
    }


    /**
     * Get points earned form the common goal card.
     * @return points earned from that common goal card.
     */
    public int getCommonCardPointsEarned(CommonGoalCard card) {
        return this.commonCardPointsEarned.get(card);
    }


    /**
     * Set points earned form the common goal card.
     * @param card you scored from
     * @param value points earned from that common goal card.
     */
    public void setCommonCardPointsEarned(CommonGoalCard card, int value) {
        if(this.commonCardPointsEarned.get(card) != null)
            this.commonCardPointsEarned.replace(card, value);
    }


    /**
     * Add the common goal card to the map. Easily get earned point based on the common goal card picked.
     * <p></p>
     * Note: use this method only when the game set the common goal card.
     * @param card
     */
    public void addCommonGoalCard(CommonGoalCard card){
        this.commonCardPointsEarned.put(card, 0);
    }


    /**
     * Check personal goal card scheme and automatically update points.
     */
    public void checkPersonalGoalCardScheme(){
        if(personalGoalCardPointsEarned <= 0)
            personalGoalCardPointsEarned = personalGoalCard.checkScheme(this);
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
