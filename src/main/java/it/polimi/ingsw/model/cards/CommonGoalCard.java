package it.polimi.ingsw.model.cards;

import java.util.PriorityQueue;
import java.util.Queue;

public class CommonGoalCard extends Card {
    private Queue<Integer> scoreTiles;

    public CommonGoalCard(int playerNum){
        scoreTiles = new PriorityQueue<>();
        switch (playerNum) {
            case 2 -> {
                scoreTiles.add(8);
                scoreTiles.add(4);
            }
            case 3 -> {
                scoreTiles.add(8);
                scoreTiles.add(6);
                scoreTiles.add(4);
            }
            case 4 -> {
                scoreTiles.add(8);
                scoreTiles.add(6);
                scoreTiles.add(4);
                scoreTiles.add(2);
            }
        }
    }

    public Integer pickScoreTile(){
        return scoreTiles.poll();
    }
}
