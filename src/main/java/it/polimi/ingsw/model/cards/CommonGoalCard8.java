package it.polimi.ingsw.model.cards;

public class CommonGoalCard8 extends CommonGoalCard {

    public CommonGoalCard8(int playerNum) {
        super(playerNum);
    }

    public Integer checkScheme(Player player) {
        return pickScoreTile();
    }
}
