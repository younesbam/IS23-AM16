package it.polimi.ingsw.model.cards;

public class CommonGoalCard6 extends CommonGoalCard {

    public CommonGoalCard6(int playerNum) {
        super(playerNum);
    }

    public Integer checkScheme(Player player) {
        return pickScoreTile();
    }
}
