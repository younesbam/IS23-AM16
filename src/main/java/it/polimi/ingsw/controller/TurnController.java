package it.polimi.ingsw.controller;
import it.polimi.ingsw.model.*;

public class TurnController {
    private Game game;
    private Controller controller;
    private View view;
    public TurnController(Game game, Controller controller, View view){
        this.game = game;
        this.controller = controller;
        this.view = view;
    }

    public Game getGame() {
        return game;
    }

    public Controller getController() {
        return controller;
    }

    public void pickTiles(int numOfTiles, int ){
        int numOfTilesToPick = view.getNumOfTilesToPick();
        int[][] coordinatesOfTilesToPick = new int[numOfTilesToPick][2];
        Tile[] tiles = null;
        for(int i = 0; i<numOfTilesToPick; i++){
            x = view.getXSelected();
            y = view.getYSelected();
            if(game.getBoard().isPickable(x,y)){
                coordinatesOfTilesToPick[i][0] = x;
                coordinatesOfTilesToPick[i][1] = y;
                pickable++;
            }
            else {
                view.displayError("Tiles selceted cannot be picked");
                return;
            }
        }
        tiles = new Tile[numOfTilesToPick];
        for(int i = 0; i<numOfTilesToPick; i++){
            tiles[i] = game.getBoard().removeTile(coordinatesOfTilesToPick[i][0], coordinatesOfTilesToPick[i][1]);
        }
        view.displayTilesToPlace(tiles); //OPPURE CHIAMARE placeTiles() ???

    }

    public void placeTiles(){
        int numOfTilesToPlace = view.getNumOfTilesToPick();
        int columnSelected = view.getColumnSelected();
        if(game.getCurrentPlayer().getBookShelf().checkColumn(columnSelected, numOfTilesToPlace)){
            if(numOfTilesToPlace == 1){
                Tile tile = view.getNextTileSelected();
                game.getCurrentPlayer().getBookShelf().placeTiles(columnSelected, tile);
            }
            else if(numOfTilesToPlace == 2){
                Tile tile1 = view.getNextTileSelected();
                Tile tile2 = view.getNextTileSelected();
                game.getCurrentPlayer().getBookShelf().placeTiles(columnSelected, tile1, tile2);
            }
            else if(numOfTilesToPlace == 3){
                Tile tile1 = view.getNextTileSelected();
                Tile tile2 = view.getNextTileSelected();
                Tile tile3 = view.getNextTileSelected();
                game.getCurrentPlayer().getBookShelf().placeTiles(columnSelected, tile1, tile2, tile3);
            }
        }
    }

    public void checkCommonGoal(){

    }

    public void checkPersonalGoal(){

    }

    public boolean checkEndGame(){
        return false;
    }

    public void updatePoints(){
        view.displayPoints(game.getCurrentPlayer().getPoints());
    }

    public void setCurrentPlayer(Player player){
        game.setCurrentPlayer(player);
    }

    public void nextPlayer(){

    }

    public void endGame(){

    }
}
