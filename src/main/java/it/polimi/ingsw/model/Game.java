package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * The Game class contains the main elements of a game match.
 * It keeps track of the players who take part in it,and also contains the game board
 * and the bag. The bag contains the decks from which to choose and distribute
 * the common and personal goal cards.
 */

public class Game {


    private static final int MAXPLAYERS = 4;
    private static final int COMGOALCARDS = 2;
    private final Board board;
    private final ArrayList<Player> players = new ArrayList<>();
    private Player currentPlayer;
    private final int numOfPlayers;
    private final ArrayList<CommonGoalCard> commonGoalCards = new ArrayList<>();

    private final Bag bag;


    public Game{
        this.board = new Board();
        this.bag = new Bag();
    }

    /**
     * Method createPlayer creates a new player in the current match.
     * @param player
     */
    public void createPlayer(Player player){
        this.players.add(player);

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


}
