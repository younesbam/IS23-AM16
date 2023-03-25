package it.polimi.ingsw.model.cards;

public class CommonGoalCard5 extends CommonGoalCard {

    public CommonGoalCard5(int playerNum) {
        super(playerNum);
    }

    public Integer checkScheme(Player player) {
        return pickScoreTile();
    }
}
