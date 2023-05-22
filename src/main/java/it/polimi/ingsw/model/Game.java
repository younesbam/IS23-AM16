package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Game class contains the main elements of a game match.
 * It keeps track of the players who take part in it,and also contains the game board
 * and the bag. The bag contains the decks from which to choose and distribute
 * the common and personal goal cards.
 */

public class Game implements Serializable {
    private Board board;
    private CreationFactory creationFactory;
    private final ArrayList<Player> players = new ArrayList<>();
    private final ArrayList<Player> activePlayers = new ArrayList<>();
    private Player currentPlayer;
    private int numOfPlayers;
    private final ArrayList<CommonGoalCard> commonGoalCards = new ArrayList<>();
    private final Bag bag;


    public Game() {

        this.bag = new Bag();

        /*
         * Factory method to create the board based on the number of players.
        */

         // TODO: da istanziare la board nel controller durante il setup iniziale
//        this.creationFactory = new CreationFactory();
//        this.board = creationFactory.createBoard(numOfPlayers);
    }

    /**
     * Method createPlayer creates a new player in the current match.
     * @param player
     */
    public void createPlayer(Player player){
        this.players.add(player);
        this.activePlayers.add(player);
    }

    /**
     * Method removePlayer removes a player from the current match.
     * @param player
     */
    public void removePlayer(Player player){
        this.players.remove(player);
    }

    public void setNumOfPlayers(int numOfPlayers){
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayers(){
        return this.numOfPlayers;
    }


    public ArrayList<Player> getPlayers() {return this.players;}

    public ArrayList<CommonGoalCard> getCommonGoalCards() {return this.commonGoalCards;}


    /**
     * Method used to set the current player.
     * @param currentPlayer
     */
    public void setCurrentPlayer(Player currentPlayer){
        this.currentPlayer = currentPlayer;
    }

    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }

    public Board getBoard(){
        return this.board;
    }

    public Bag getBag(){
        return this.bag;
    }

    /**
     * This method returns the ID corresponding player.
     * @param id
     * @return
     */
    public Player getPlayerByID(int id) {
        for (Player player : activePlayers) {
            if (player.getID() == id) {
                return player;
            }
        }
        return null;
    }

    /**
     * This method returns the list of active players.
     * @return
     */
    public ArrayList<Player> getActivePlayers() {
        return activePlayers;
    }


    public void createBoard(){
        this.creationFactory = new CreationFactory();
        this.board = creationFactory.createBoard(numOfPlayers);
    }
}
