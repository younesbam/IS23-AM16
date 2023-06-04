package it.polimi.ingsw.model;

import it.polimi.ingsw.common.exceptions.PlayerNotFoundException;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Game class contains the main elements of a game match.
 * It keeps track of the players who take part in it,and also contains the game board
 * and the bag. The bag contains the decks from which to choose and distribute
 * the common and personal goal cards.
 */

public class Game implements Serializable {
    private Board board;
    private CreationFactory creationFactory;
    private final ArrayList<Player> activePlayers = new ArrayList<>();
    private final ArrayList<Player> inactivePlayers = new ArrayList<>();
    private Player currentPlayer;
    private int numOfPlayers;
    private final List<CommonGoalCard> commonGoalCards = new ArrayList<>();
    private final Bag bag;
    private Player firstPlayer;


    public Game() {
        // Only the bag instance, because the board must me created when we know the number of players.
        this.bag = new Bag();
    }

    /**
     * Method createPlayer creates a new player in the current match.
     * @param player
     */
    public void createPlayer(Player player){
        this.activePlayers.add(player);
    }

    /**
     * Method removePlayer removes a player from the current match.
     * @param player
     */
    public void removePlayer(Player player){
        this.activePlayers.remove(player);
    }

    public void setNumOfPlayers(int numOfPlayers){
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayers(){
        return this.numOfPlayers;
    }


    /**
     * First player setter.
     * @param player
     */
    public void setFirstPlayer(Player player){
        this.firstPlayer = player;
    }


    /**
     * First player getter.
     * @return
     */
    public Player getFirstPlayer(){
        return this.firstPlayer;
    }



    public List<CommonGoalCard> getCommonGoalCards() {return this.commonGoalCards;}


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


    /**
     * Method used to switch to the next player.
     */
    public void nextPlayer() {
        for(int i=0; i<activePlayers.size(); i++){
            if(currentPlayer.equals(activePlayers.get(i))){
                if(i == activePlayers.size()-1)
                    setCurrentPlayer(activePlayers.get(0));
                else
                    setCurrentPlayer(activePlayers.get(i+1));
                return;
            }
        }
    }


    /**
     * Board creation method.
     */
    public void createBoard(){
        this.creationFactory = new CreationFactory();
        this.board = creationFactory.createBoard(numOfPlayers);
    }


    /**
     * Move player from active to inactive list
     * @param ID of the player
     * @throws PlayerNotFoundException
     */
    public void moveActiveToInactive(int ID) throws PlayerNotFoundException {
        Player playerToMove = getPlayerByID(ID);
        if(playerToMove == null) throw new PlayerNotFoundException();
        for(Player p : activePlayers){
            if(playerToMove.equals(p)){
                activePlayers.remove(p);
                inactivePlayers.add(p);
                break;
            }
        }
    }

    /**
     * Move player from inactive to active list
     * @param username of the player
     * @throws PlayerNotFoundException
     */
    public void moveInactiveToActive(String username) throws PlayerNotFoundException {
        Player playerToMove = null;
        for(Player p : inactivePlayers){
            if(p.getUsername().equals(username)){
                playerToMove = p;
                break;
            }
        }
        if(playerToMove == null) throw new PlayerNotFoundException();

        inactivePlayers.remove(playerToMove);
        activePlayers.add(playerToMove);
    }
}
