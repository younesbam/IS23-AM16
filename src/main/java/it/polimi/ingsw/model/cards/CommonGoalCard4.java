package it.polimi.ingsw.model.cards;

public class CommonGoalCard4 extends CommonGoalCard {

    public CommonGoalCard4(int playerNum) {
        super(playerNum);
    }

    public Integer checkScheme(Player player, int maxNotEq, int group, Direction dir) {
        return pickScoreTile();
    }
}
