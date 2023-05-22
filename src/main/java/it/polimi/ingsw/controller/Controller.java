package it.polimi.ingsw.controller;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.GameHandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.InvalidParameterException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class Controller implements PropertyChangeListener {
    private Game game;
    private final GameHandler gameHandler;
    private Player currentPlayer;


    /**
     * Class constructor.
     */
    public Controller(GameHandler gameHandler, Game game) {
        this.gameHandler = gameHandler;
        this.game = game;
    }


    /**
     * Game instance getter.
     * @return
     */
    public Game getGame() {
        return game;
    }


    /**
     * Method used to set game parameters for the initial phase of the match.
     */
    public void setup() {

        //select 2 common goal cards
        game.getCommonGoalCards().add(game.getBag().pickCommonGoalCard(game.getNumOfPlayers()));
        game.getCommonGoalCards().add(game.getBag().pickCommonGoalCard(game.getNumOfPlayers()));

        game.getCommonGoalCards().get(0).placePoints(game.getNumOfPlayers());
        game.getCommonGoalCards().get(1).placePoints(game.getNumOfPlayers());


        int i;
        //select a personal goal card for each player
        for (i = 0; i < game.getNumOfPlayers(); i++) {
            game.getPlayers().get(i).setPersonalGoalCard(game.getBag().pickPersonalGoalCard());
            game.getPlayers().get(i).setBookShelf(new BookShelf());
        }

        game.createBoard();
        game.getBoard().updateBoard();
        setCurrentPlayer(game.getCurrentPlayer());

        gameHandler.sendToEveryone(new GameReplica(game));

        askWhatToDo();
        //the game board should be already filled with tiles
    }


    /**
     * Current player getter.
     * @return
     */
    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }


    /**
     * Method used to ask the player what he wants to do at the start of his turn.
     */
    public void askWhatToDo(){
        gameHandler.sendToPlayer(new RequestWhatToDo(), currentPlayer.getID());
    }


    /**
     * Method that asks the current player which tiles he wants to pick from the board.
     */
    public void askTiles(){
        gameHandler.sendToPlayer(new GameReplica(getGame()), getCurrentPlayer().getID());
        gameHandler.sendToPlayer(new RequestTiles(), getCurrentPlayer().getID());
    }


    /**
     * Method to check if the tiles selected by the player are actually pickable. If so, they are removed from the board.
     *
     * @param coordinates
     */
    public void canPickTiles(int[][] coordinates){

        //check if selected tiles can be picked up from the board
        int i;
        boolean canPick = true;

        // La condizione precedente era: coordinates[i][0] != null. Che significa? < 2 non vuol dire nulla, era solo per farlo andare
        for (i = 0; i < coordinates.length; i++) {
            System.out.println(coordinates[i][0] + " " + coordinates[i][1]);
            if (!game.getBoard().isPickable(coordinates[i][0], coordinates[i][1])) {
                canPick = false;
            }
        }

        if (canPick) {
            removeTilesFromBoard(coordinates);
        } else{
            System.out.println("You can't pick the tiles here! Please select other tiles!");
            askTiles();
        }

    }


    /**
     * Method used to remove tiles from the player board.
     * @param coordinates
     */
    public void removeTilesFromBoard(int[][] coordinates){
        int i;
        ArrayList<Tile> tiles = new ArrayList<>();

        for (i = 0;  i < coordinates.length; i++) {  // La condizione precedente era: coordinates[i][0] != null. Che significa? < 2 non vuol dire nulla, era solo per farlo andare
            tiles.add(game.getBoard().getTile(coordinates[i][0], coordinates[i][1]));
            gameHandler.sendToPlayer(new PersonalizedAnswer(false, "\nYou picked the following tile:" + game.getBoard().getTile(coordinates[i][0], coordinates[i][1]).name()), currentPlayer.getID());
            game.getBoard().removeTile(coordinates[i][0], coordinates[i][1]);
        }

        askToPlaceTiles(tiles);
    }


    public void askToPlaceTiles(ArrayList<Tile> tiles){
        gameHandler.sendToPlayer(new RequestWhereToPlaceTiles(tiles), getCurrentPlayer().getID());
    }

    /**
     * Method used to place the selected tiles in the current player's Bookshelf. It also checks if the selected column has enough free spaces.
     *
     * @param column
     * @param numOfTiles
     * @param list
     */
    public void placeTiles(int column, int numOfTiles, List<Tile> list) {
        try{
            game.getCurrentPlayer().getBookShelf().checkColumn(column, numOfTiles);
            game.getCurrentPlayer().getBookShelf().placeTiles(column, list);
        }catch (InvalidParameterException | NotEmptyColumnException e){
            //else MESSAGGIO CHE LA COLONNA NON HA ABBASTANZA SPAZIO!
        }
    }


    /**
     * Method called after every turn, it checks if the current player has reached any common goal in this turn.
     */
    public void checkCommonGoal() {

        int i;
        for (i = 0; i < 2; i++) {
            if (game.getCurrentPlayer().getCommonGoalReached()[i] < 0) {

                game.getCommonGoalCards().get(i).checkScheme(game.getCurrentPlayer());

                if (game.getCurrentPlayer().getCommonGoalReached()[i] > 0) {
                    updatePoints(game.getCurrentPlayer().getCommonGoalReached()[i]);
                }
            }
        }
    }


    /**
     * Method called at the end of the game to calculate the points gained by the personal goal.
     */
    public void checkPersonalGoal(Player player) {

        int numOfPlayers = game.getNumOfPlayers();
        int i;

        for(i = 0; i < numOfPlayers; i++){
            updatePoints(game.getPlayers().get(i).getPersonalGoalCard().checkScheme(player));
        }

    }


    /**
     * Method that after every player's turn checks if he has completely filled its Bookshelf, and so the game has reached an end.
     * @return
     */
    public boolean checkEndGame() {
        if (game.getCurrentPlayer().getBookShelf().checkEndGame()) {
            this.endGame();
        }
        return true;
    }


    /**
     * Method that updates current player's points.
     * @param points
     */
    public void updatePoints(int points) {
        game.getCurrentPlayer().setPoints(points);
    }


    /**
     * Method used to set the current player.
     * @param player
     */
    public void setCurrentPlayer(Player player) {
        game.setCurrentPlayer(player);
        this.currentPlayer = player;
    }


//    /**
//     * Method that switches the current player to the next one.
//     */
//    public void nextPlayer() {
//        game.nextPlayer();
//    }

    public void endGame() {

    }


    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName().toUpperCase()){
            case "TILESPICKED" -> canPickTiles((int[][]) evt.getNewValue());
        }

    }
}