package it.polimi.ingsw.model;

import it.polimi.ingsw.common.exceptions.NoNextPlayerException;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private Player currentPlayer;
    private int numOfPlayers;
    private final List<CommonGoalCard> commonGoalCards = new ArrayList<>();
    private final Bag bag;
    private Player firstPlayer;

    /**
     * Constructor.
     */
    public Game() {
        // Only the bag instance, because the board must be created when we know the number of players.
        this.bag = new Bag();
    }

    /**
     * Creates a new player in the current match.
     * @param player player to be added.
     */
    public void createPlayer(Player player){
        this.players.add(player);
    }

    /**
     * Method removePlayer removes a player from the current match.
     * @param player to be removed
     */
    public void removePlayer(Player player){
        this.players.remove(player);
    }

    /**
     * Set number of players in the game.
     * @param numOfPlayers number of players
     */
    public void setNumOfPlayers(int numOfPlayers){
        this.numOfPlayers = numOfPlayers;
    }

    /**
     * Get the number of total players in the game.
     * @return number of total players in the game.
     */
    public int getNumOfPlayers(){
        return this.numOfPlayers;
    }


    /**
     * First player setter.
     * @param player first player.
     */
    public void setFirstPlayer(Player player){
        this.firstPlayer = player;
    }


    /**
     * First player getter.
     * @return first player.
     */
    public Player getFirstPlayer(){
        return this.firstPlayer;
    }

    /**
     * Method used to set the current player.
     * @param currentPlayer current player
     */
    public void setCurrentPlayer(Player currentPlayer){
        this.currentPlayer = currentPlayer;
    }

    /**
     * Get current player.
     * @return current player.
     */
    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }


    /**
     * Get common goal cards.
     * @return list of common goal card.
     */
    public List<CommonGoalCard> getCommonGoalCards() {return this.commonGoalCards;}

    /**
     * Get board.
     * @return board.
     */
    public Board getBoard(){
        return this.board;
    }

    /**
     * Get the bag.
     * @return bag.
     */
    public Bag getBag(){
        return this.bag;
    }

    /**
     * This method returns the ID corresponding player.
     * @param id unique ID of the player.
     * @return player associated with the passed ID.
     */
    public Player getPlayerByID(int id) {
        for (Player player : players) {
            if (player.getID() == id) {
                return player;
            }
        }
        return null;
    }

    /**
     * This method returns the list of players.
     * @return list of players in the game.
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }


    /**
     * Method used to switch to the next player.
     * @throws NoNextPlayerException not enough players to set the next player.
     */
    public void nextPlayer() throws NoNextPlayerException {
        // If the player is still active it means that he completed the turn successfully. Otherwise, he disconnects during his turn. Do not increment turn counter.
        if(this.currentPlayer.isActive())
            this.currentPlayer.updateNumOfTurns();

        // Check if there is at least one player more than the current player. If not, throw an exception
        int activePlayers = 0;
        for(Player p : players){
            if(p.isActive())
                activePlayers++;
        }

        if(activePlayers <= 1) throw new NoNextPlayerException();

        // Set the next player. Check if it is active. If not, jump to the next player.
        Player nextPlayer = null;
        // Iterate the list from the current player to the end
        for(int i=players.indexOf(currentPlayer)+1; i<players.size(); i++){
            if(players.get(i).isActive()){
                nextPlayer = players.get(i);
                break;
            }
        }
        // Iterate the list again (from 0 to currentPlayer-1) if the next player were not found.
        if(nextPlayer == null){
            for(int i=0; i<players.indexOf(currentPlayer); i++){
                if(players.get(i).isActive()){
                    nextPlayer = players.get(i);
                    break;
                }
            }
        }

        setCurrentPlayer(nextPlayer);
    }


    /**
     * Create a new full board based on the number of players
     */
    public void createBoard(){
        this.creationFactory = new CreationFactory();
        this.board = creationFactory.createBoard(numOfPlayers);
    }

    /**
     * Set player as active.
     * @param ID unique ID of the player.
     * @param active boolean value that represent if the player is active or not.
     */
    public void setActivePlayer(int ID, boolean active){
        for(Player p : players){
            if(p.getID() == ID)
                p.setActive(active);
        }
    }
}
