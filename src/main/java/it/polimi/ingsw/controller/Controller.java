package it.polimi.ingsw.controller;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.GameHandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Controller implements PropertyChangeListener {
    private Game game;
    private final GameHandler gameHandler;
    private Player currentPlayer;
    private ArrayList<Tile> currentTiles = new ArrayList<>();


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

        for (i = 0; i < coordinates.length; i++) {
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
        //ArrayList<Tile> tiles = new ArrayList<>();

        for (i = 0;  i < coordinates.length; i++) {
            //tiles.add(game.getBoard().getTile(coordinates[i][0], coordinates[i][1]));
            currentTiles.add(game.getBoard().getTile(coordinates[i][0], coordinates[i][1]));
            gameHandler.sendToPlayer(new PersonalizedAnswer(false, "\nYou picked the following tile:" + game.getBoard().getTile(coordinates[i][0], coordinates[i][1]).name()), currentPlayer.getID());
            game.getBoard().removeTile(coordinates[i][0], coordinates[i][1]);
        }

        askToPlaceTiles();
    }


    public void askToPlaceTiles(){
        gameHandler.sendToPlayer(new RequestWhereToPlaceTiles(), getCurrentPlayer().getID());
    }

    /**
     * Method used to place the selected tiles in the current player's Bookshelf. It also checks if the selected column has enough free spaces.
     *
     * @param coordinates it contains the coordinates of the bookshelf on where to place the tiles.
     */
    public void placeTiles(String[] coordinates) {
        try{

            ArrayList<Tile> rightOrderTiles = new ArrayList<>();
            int column = 0;

            switch (coordinates.length) {
                case 2 -> {
                    if (!currentTiles.get(0).name().equals(coordinates[0])) {
                        gameHandler.sendToPlayer(new PersonalizedAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
                        askToPlaceTiles();
                    }
                    else{
                        rightOrderTiles.add(Tile.valueOf(coordinates[0]));
                        column = Integer.parseInt(coordinates[1]);
                    }
                }
                case 3 -> {
                    if (!(currentTiles.get(0).name().equals(coordinates[0]) || (currentTiles.get(0).name().equals(coordinates[0])) && ((currentTiles.get(1).name().equals(coordinates[1]) || (currentTiles.get(1).name().equals(coordinates[1])))))) {
                        gameHandler.sendToPlayer(new PersonalizedAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
                        askToPlaceTiles();
                    }
                    else {
                        rightOrderTiles.add(Tile.valueOf(coordinates[0]));
                        rightOrderTiles.add(Tile.valueOf(coordinates[1]));
                        column = Integer.parseInt(coordinates[2]);
                    }
                }
                case 4 -> {
                    if (!(currentTiles.get(0).name().equals(coordinates[0]) || (currentTiles.get(0).name().equals(coordinates[0])) && ((currentTiles.get(1).name().equals(coordinates[1]) || (currentTiles.get(1).name().equals(coordinates[1])))) && ((currentTiles.get(2).name().equals(coordinates[2]) || (currentTiles.get(2).name().equals(coordinates[2])))))) {
                        gameHandler.sendToPlayer(new PersonalizedAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
                        askToPlaceTiles();
                    }
                    else {
                        rightOrderTiles.add(Tile.valueOf(coordinates[0]));
                        rightOrderTiles.add(Tile.valueOf(coordinates[1]));
                        rightOrderTiles.add(Tile.valueOf(coordinates[2]));
                        column = Integer.parseInt(coordinates[3]);
                    }
                }
            }

            game.getCurrentPlayer().getBookShelf().checkColumn(column - 1, coordinates.length - 1);
            game.getCurrentPlayer().getBookShelf().placeTiles(column - 1, rightOrderTiles);

            gameHandler.sendToEveryone(new GameReplica(game));
            gameHandler.sendToPlayer(new BookShelfFilledWithTiles(), currentPlayer.getID());

        }
        catch (InvalidParameterException e){
            System.out.println("Invalid parameters!");
        }
        catch (NotEmptyColumnException e){
            gameHandler.sendToPlayer(new PersonalizedAnswer(false, "Not enough space in this column! Please select another one!"), currentPlayer.getID());
            askToPlaceTiles();
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
            case "TILESPLACED" -> placeTiles((String[]) evt.getNewValue());
        }

    }
}