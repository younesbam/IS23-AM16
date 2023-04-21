package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;

/**
 * This class represent the PLAYER model.
 * @author Younes Bamhaoud
 */
public class Player {
    private String nickname;
    private int ID;
    private int points;
    private BookShelf bookShelf;
    private PersonalGoalCard personalGoalCard;
    private boolean chair; /* specify if the player is the FIRST of the round in all the game */
    private int[] commonGoalReached = new int[2]; /* array that contains the points for reaching zero, one or two common goals */
    private int numOfTurns; /* saves number of turns played. Useful to save the state of the game.*/

    /**
     * Player constructor.
     * @param nickname
     */
    public Player(String nickname){ this.nickname = nickname;}

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean hasChair() {
        return chair;
    }

    public void setChair(boolean chair){
        this.chair=chair;
    }

    public BookShelf getBookShelf() {
        return bookShelf; }

    public void setBookShelf(BookShelf bookShelf){
        this.bookShelf = bookShelf;
    }

    public int getNumOfTurns() {
        return numOfTurns;
    }
    public void setNumOfTurns(int numOfTurns) {
        this.numOfTurns = numOfTurns;
    }

    public int[] getCommonGoalReached() {
        return commonGoalReached;
    }

    public PersonalGoalCard getPersonalGoalCard() {
        return personalGoalCard;
    }
    public void setPersonalGoalCard(PersonalGoalCard personalGoalCard) {
        this.personalGoalCard = personalGoalCard;
    }

    public int getID(){
        return this.ID;
    }

}
