package it.polimi.ingsw.model.cards;

public class CommonGoalCard7 extends CommonGoalCard {

    public CommonGoalCard7(int playerNum) {
        super(playerNum);
    }

    public Integer checkScheme(Player player) {
        return pickScoreTile();
    }
}
