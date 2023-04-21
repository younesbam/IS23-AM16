package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;

import java.beans.PropertyChangeSupport;

public class GameHandler {

    private static Game game;
    private final Server server;
    private final Controller controller;
    private int numOfPlayers;
    private final PropertyChangeSupport gameHandlerListener = new PropertyChangeSupport(this);
    private int playerID;
    private boolean alreadyStarted;


    /**
     * Method used to remove a player from the game.
     * @param playerID
     */
    public void removePlayer(int playerID){
        game.removePlayer(game.getPlayerByID(playerID));
    }


    /**
     * This method ends the current match for all the players, after a player disconnection.
     */
    public void endMatch(String playerLeaving){
        address
    }


    public void addressPlayer(int ID, Answer answer){

    }

    /**
     * isAlreadyStarted getter.
     */
    public void isAlreadyStarted(){
        return this.alreadyStarted;
    }

}
