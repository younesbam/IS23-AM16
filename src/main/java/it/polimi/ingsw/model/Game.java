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


    public Game() {
        // Only the bag instance, because the board must be created when we know the number of players.
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
        for (Player player : players) {
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
    public ArrayList<Player> getPlayers() {
        return players;
    }


    /**
     * Method used to switch to the next player.
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

//        for(int i = 0; i< players.size(); i++){
//            if(currentPlayer.equals(players.get(i))){
//                if(i == players.size()-1)
//                    setCurrentPlayer(players.get(0));
//                else
//                    setCurrentPlayer(players.get(i+1));
//                return;
//            }
//        }
    }


    /**
     * Board creation method.
     */
    public void createBoard(){
        this.creationFactory = new CreationFactory();
        this.board = creationFactory.createBoard(numOfPlayers);
    }


    public void setActivePlayer(int ID, boolean active){
        for(Player p : players){
            if(p.getID() == ID)
                p.setActive(active);
        }
    }

//    /**
//     * Move player from active to inactive list
//     * @param ID of the player
//     * @throws PlayerNotFoundException
//     */
//    public void moveActiveToInactive(int ID) throws PlayerNotFoundException {
//        Player playerToMove = getPlayerByID(ID);
//        if(playerToMove == null) throw new PlayerNotFoundException();
//        for(Player p : players){
//            if(playerToMove.equals(p)){
//                players.remove(p);
//                inactivePlayers.add(p);
//                break;
//            }
//        }
//    }

//    /**
//     * Move player from inactive to active list
//     * @param username of the player
//     * @throws PlayerNotFoundException
//     */
//    public void moveInactiveToActive(String username) throws PlayerNotFoundException {
//        Player playerToMove = null;
//        for(Player p : inactivePlayers){
//            if(p.getUsername().equals(username)){
//                playerToMove = p;
//                break;
//            }
//        }
//        if(playerToMove == null) throw new PlayerNotFoundException();
//
//        inactivePlayers.remove(playerToMove);
//        players.add(playerToMove);
//    }
}
