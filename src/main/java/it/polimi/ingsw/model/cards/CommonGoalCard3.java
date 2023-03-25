package it.polimi.ingsw.model.cards;

public class CommonGoalCard3 extends CommonGoalCard {

    public CommonGoalCard3(int playerNum) {
        super(playerNum);
    }

    public Integer checkScheme(Player player, int eq, int repetition, Direction dir) {
        return pickScoreTile();
    }
}
