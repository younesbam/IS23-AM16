package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represent the PLAYER model.
 * @author Younes Bamhaoud
 */
public class Player implements Serializable {
    private String username;
    private int ID;
    private int points;
    private BookShelf bookShelf;
    private PersonalGoalCard personalGoalCard;
    private boolean chair; /* specify if the player is the FIRST of the round in all the game */
    private int[] commonGoalReached = new int[2]; /* array that contains the points for reaching zero, one or two common goals */
    private int numOfTurns; /* saves number of turns played. Useful to save the state of the game.*/



    /**
     * Player constructor.
     * @param username
     * @param ID
     */
    public Player(String username, Integer ID){
        this.username = username;
        this.ID = ID;
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
     * Player points getter.
     * @return
     */
    public int getPoints() {
        return points;
    }

    /**
     * Method used to set player's points
     * @param points
     */
    public void setPoints(int points) {
        this.points = points;
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
     * This method returns the array containing the boolean values that represent if the player has reached the common goals.
     * @return
     */
    public int[] getCommonGoalReached() {
        return this.commonGoalReached;
    }

    /**
     * Personal goal card getter.
     * @return
     */
    public PersonalGoalCard getPersonalGoalCard() {
        return this.personalGoalCard;
    }

    /**
     * Personal goal card setter.
     * @param personalGoalCard
     */
    public void setPersonalGoalCard(PersonalGoalCard personalGoalCard) {
        this.personalGoalCard = personalGoalCard;
    }

    /**
     * ID getter.
     * @return
     */
    public int getID(){
        return this.ID;
    }

}
