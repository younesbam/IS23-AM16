package it.polimi.ingsw.controller;
import it.polimi.ingsw.model.*;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private Game game;
    private final GameHandler gameHandler;


    /**
     * Class constructor.
     */
    public Controller() {
        this.game = new Game();
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
    private void setup() {

        //setting first player
        int numOfPlayers = game.getNumOfPlayers();
        int min = 0;
        int randomNum = min + (int) (Math.random() * ((numOfPlayers –min)));
        int i;

        for (i = 0; i < numOfPlayers; i++) {
            if (i != randomNum) {
                game.getPlayers().get(i).setChair(false);
            }
            game.getPlayers().get(i).setChair(true);
        }

        //select 2 common goal cards
        game.getCommonGoalCards().add(game.getBag().pickCommonGoalCard());
        game.getCommonGoalCards().add(game.getBag().pickCommonGoalCard());

        //select a personal goal card for each player
        for (i = 0; i < numOfPlayers; i++) {
            game.getPlayers().get(i).setPersonalGoalCard(game.getBag().pickPersonalGoalCard());
        }

        //update the game board filling it with tiles
        game.getBoard().updateBoard();
    }


    /**
     * Method to check if the tiles selected by the player are actually pickable. If so, they are removed from the board.
     *
     * @param numOfTiles
     * @param coordinates
     */
    public void pickTiles(int numOfTiles, int coordinates[3][2]) {

        //check if selected tiles can be picked up from the board
        int i;
        boolean canPick = true;

        for (i = 0; i < numOfTiles; i++) {
            if (!game.getBoard().isPickable(coordinates[i][0], coordinates[i][1])) {
                canPick = false;
            }
        }

        if (canPick) {
            for (i = 0; i < numOfTiles; i++) {
                game.getBoard().removeTile(coordinates[i][0], coordinates[i][1]);
            }
        } else
        //MESSAGGIO CHE NON PUOI PRENDERE QUESTE TILES!
    }


    /**
     * Method used to place the selected tiles in the current player's Bookshelf. It also checks if the selected column has enough free spaces.
     *
     * @param column
     * @param numOfTiles
     * @param list
     */
    public void placeTiles(int column, int numOfTiles, List<Tile> list) {

        if (game.getCurrentPlayer().getBookShelf().checkColumn(column, numOfTiles)) {
            game.getCurrentPlayer().getBookShelf().placeTiles(column, list);
        } else
        //MESSAGGIO CHE LA COLONNA NON HA ABBASTANZA SPAZIO!
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
    public void checkPersonalGoal() {

        int numOfPlayers = game.getNumOfPlayers();
        int i;

        for(i = 0; i < numOfPlayers; i++){
            updatePoints(game.getPlayers().get(i).getPersonalGoalCard().checkScheme());
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
    }


    /**
     * Method that switches the current player to the next one.
     */
    public void nextPlayer() {
        game.nextPlayer();
    }

    public void endGame() {

    }


    public void propertyChange(PropertyChangeEvent evt) {

    }
}