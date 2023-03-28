package it.polimi.ingsw.model.cards;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Represent a generic common card.
 * @author Nicolo' Gandini
 */
public class CommonGoalCard extends Card {
    private Queue<Integer> score;

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
     * @param cardNumber number of the corresponding physical card
     */
    public CommonGoalCard(int playerNum, int cardNumber){
        this.cardNumber = cardNumber;
        score = new PriorityQueue<>();
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

    /**
     * Get score points based on the Queue order
     * @return score points
     */
    public Integer getScore(){
        return score.poll();
    }

    /*
    Method to override into the subclasses.
     */
    public Integer checkScheme(Player player){return -1};

}
