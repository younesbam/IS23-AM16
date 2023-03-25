package it.polimi.ingsw.model.cards;

public class CommonGoalCard1 extends CommonGoalCard {

    public CommonGoalCard1(int playerNum) {
        super(playerNum);
    }

    public Integer checkScheme(Player player, int repetition) {
        int matches = 0;
        for(int j=0; j<6-1; j++){
            for(int i=0; i<5-1; i++){
                Cell[][] grid = player.getBookShelf().getGrid();
                Type tile = grid[i][j].getObjectTile().getType();
                Type tileRight = grid[i+1][j].getObjectTile().getType();
                Type tileSouth = grid[i][j+1].getObjectTile().getType();
                Type tileDiag = grid[i+1][j+1].getObjectTile().getType();
                if(tile == tileRight && tile == tileSouth && tile == tileDiag)
                    matches+=1;
                if(matches >= repetition)
                    return pickScoreTile();
            }
        }
        return 0;
    }
}
