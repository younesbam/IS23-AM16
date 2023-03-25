package it.polimi.ingsw.model.cards;

public class CommonGoalCard2 extends CommonGoalCard {

    public CommonGoalCard2(int playerNum) {
        super(playerNum);
    }

    public Integer checkScheme(Player player, int notEq, int repetition, Direction dir) {
        return pickScoreTile();
    }
}
