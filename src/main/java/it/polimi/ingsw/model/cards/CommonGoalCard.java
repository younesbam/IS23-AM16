package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.*;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Represent a generic common card.
 */
public abstract class CommonGoalCard extends Card {
    /**
     * Score queue. This is based on the number of players in the game
     */
    private LinkedList<Integer> score;

    public CommonGoalCard(int cardNumber){
        this.cardNumber = cardNumber;
        score = new LinkedList<>();
    }

    /**
     * Get score points based on the number of players.
     * @return score points
     */
    protected Integer getScore(){
        return score.poll();
    }


    /**
     * Add score based on the number of players.
     * <p>
     *      2 Players: 8, 4 score points.
     * </p>
     * <p>
     *     3 Players: 8, 6, 4 score points.
     * </p>
     * <p>
     *     4 Players: 8, 6, 4, 2 score points.
     * </p>
     * @param playerNum number of total players in the game
     */
    public void placePoints(int playerNum){
        switch (playerNum) {
            case 2 -> {
                score.add(8);
                score.add(4);
            }
            case 3 -> {
                score.add(8);
                score.add(6);
                score.add(4);
            }
            case 4 -> {
                score.add(8);
                score.add(6);
                score.add(4);
                score.add(2);
            }
        }
    }
}
