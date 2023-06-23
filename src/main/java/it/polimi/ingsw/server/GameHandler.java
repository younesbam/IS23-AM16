package it.polimi.ingsw.server;

import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PrintCardsAction;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles the match, controller and game.
 * It's a middle man between server and controller.
 * It dispatches also the actions, received from the client, to the controller.
 */
public class GameHandler {
    /**
     * Game reference.
     */
    private static Game game;

    /**
     * Server reference
     */
    private final Server server;

    /**
     * Controller reference.
     */
    private final Controller controller;

    /**
     * Number of players in the game.
     */
    private int numOfPlayers;

    /**
     * Controller's property change support.
     * @see Controller#propertyChange(PropertyChangeEvent)
     */
    private final PropertyChangeSupport pcsController = new PropertyChangeSupport(this);

    /**
     * Game started.
     */
    private boolean gameStarted;


    /**
     * GameHandler constructor.
     * @param server server reference.
     */
    public GameHandler(Server server){
        this.server = server;
        this.game = new Game();
        this.controller = new Controller( this, game);

        pcsController.addPropertyChangeListener(controller);
    }

    /**
     * Method used to remove a player from the game.
     * @param playerID ID of the player.
     */
    public void removePlayer(int playerID){
        game.removePlayer(game.getPlayerByID(playerID));
    }


    /**
     * Add a player to the game.
     * @param username username of the player.
     * @param clientID ID of the client.
     */
    public void createPlayer(String username, Integer clientID){
        game.createPlayer(new Player(username, clientID));
    }


    /**
     * Set this match' number of players.
     * @param numOfPlayers number of player for this match.
     */
    public void setNumOfPlayers(int numOfPlayers){
        this.numOfPlayers = numOfPlayers;
        game.setNumOfPlayers(numOfPlayers);
    }


    /**
     * Server getter.
     * @return server.
     */
    public Server getServer(){
        return this.server;
    }


    /**
     * Send a message to every player.
     * @param answer answer to be sent to every player.
     */
    public void sendToEveryone(Answer answer){
        for(VirtualPlayer p: server.getConnectedPlayers())
            sendToPlayer(answer, p.getID());
    }


    /**
     * Send an answer to every player except the selected player
     * @param answer answer to be sent to every player except a specific player.
     * @param notToHim ID of the excluded player.
     */
    public void sendToEveryoneExcept(Answer answer, int notToHim) {
        for(VirtualPlayer p : server.getConnectedPlayers()) {
            if(p.getID() != notToHim)
                sendToPlayer(answer, p.getID());
        }
    }


    /**
     * Sends the server's answer to the player.
     * @param answer answer to be sent.
     * @param playerID ID of the player whom you want to send the message.
     */
    public void sendToPlayer(Answer answer, int playerID){
        VirtualPlayer player = server.getVirtualPlayerByID(playerID);
        if(player != null)
            player.send(answer);
    }


    /**
     * Start the actual game.
     */
    public void startGame(){
        sendToEveryone(new CustomAnswer("The game is on!\n"));
        setGameStarted(true);

        initialSetup();
    }


    /**
     * Selects the first player and sets up the game parameters for a new match.
     */
    public void initialSetup(){
        sendToEveryone(new CustomAnswer("Now the first player to play is being randomly selected, be ready, it could be you!"));

        int firstPlayer = 0;
        int randomNum = ThreadLocalRandom.current().nextInt(0, numOfPlayers + 1);

        for (int i = 0; i < numOfPlayers; i++) {
            if (i != randomNum) {
                game.getPlayers().get(i).setChair(false);
            }
            game.getPlayers().get(i).setChair(true);
            game.setFirstPlayer(game.getPlayers().get(i));
            game.setCurrentPlayer(game.getPlayers().get(i));
            firstPlayer = game.getPlayers().get(i).getID();
        }

        sendToEveryoneExcept(new CustomAnswer("The first player is: " + server.getUsernameByID(firstPlayer) + "!"), firstPlayer);
        sendToEveryoneExcept(new FirstPlayerSelected(server.getUsernameByID(firstPlayer)), firstPlayer);
        sendToPlayer(new CustomAnswer("You are the first player! Here's your chair! \n " +
                "  __________.\n" +
                "  /_/-----/_/|   \n" +
                "  ( ( ' ' ( (| \n" +
                "  (_( ' ' (_(| \n" +
                "  / /=====/ /| \n" +
                " /_//____/_/ | \n" +
                "(o|:.....|o) | \n" +
                "|_|:_____|_|/' \n"), firstPlayer);

        controller.setup();
    }


    /**
     * This method terminates the game.
     */
    public void shutdownServer(){
        server.exit();
    }

    /**
     * Set match as stared.
     * @param gameStarted game started bit.
     */
    public void setGameStarted(boolean gameStarted){
        this.gameStarted = gameStarted;
    }


    /**
     * alreadyStarted getter.
     */
    public boolean isGameStarted(){
        return this.gameStarted;
    }


    /**
     * Controller getter.
     * @return controller.
     */
    public Controller getController(){
        return this.controller;
    }

    /**
     * Dispatch the action received from the client to the controller.
     * @param action action received from the client.
     */
    public void dispatchActions(GameAction action){
        if (action instanceof PickTilesAction){
            pcsController.firePropertyChange("PickTilesAction", null, action);
            return;
        }
        if(action instanceof PlaceTilesAction){
            pcsController.firePropertyChange("PlaceTilesAction", null, action);
            return;
        }
        if(action instanceof PrintCardsAction){
            pcsController.firePropertyChange("PrintCardsAction", null, action);
            return;
        }
    }

    /**
     * Suspend the selected client after a disconnection. It acts on controller.
     * @param ID Unique ID of the player to be suspended.
     */
    public void suspendClient(int ID){
        controller.suspendClient(ID);
    }

    /**
     * Restore the selected client after a disconnection and a reconnection. It acts on controller.
     * @param ID Unique ID of the player that was suspended
     */
    public void restoreClient(int ID){
        controller.restoreClient(ID);
    }
}
