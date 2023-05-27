package it.polimi.ingsw.server;

import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.CustomAnswer;
import it.polimi.ingsw.communications.serveranswers.PlayerDisconnected;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;

import java.beans.PropertyChangeSupport;

/**
 * Handles the match, controller and game
 */
public class GameHandler {

    private static Game game;
    private final Server server;
    private final Controller controller;
    private int numOfPlayers;
    private final PropertyChangeSupport pcsController = new PropertyChangeSupport(this);
    private int playerID;
    private boolean alreadyStarted;


    /**
     * GameHandler constructor.
     * @param server
     */
    public GameHandler(Server server){
        this.server = server;
        this.game = new Game();
        this.controller = new Controller( this, game);

        pcsController.addPropertyChangeListener(controller);
    }

    /**
     * Method used to remove a player from the game.
     * @param playerID
     */
    public void removePlayer(int playerID){
        game.removePlayer(game.getPlayerByID(playerID));
    }


    /**
     * Method used to add a player to the game.
     * @param username
     * @param clientID
     */
    public void createPlayer(String username, Integer clientID){
        game.createPlayer(new Player(username, clientID));
    }


    /**
     * Method used to set this match' number of players.
     * @param numOfPlayers
     */
    public void setNumOfPlayers(int numOfPlayers){
        this.numOfPlayers = numOfPlayers;
        game.setNumOfPlayers(numOfPlayers);
    }


    /**
     * This method is used to send a message to every player.
     * @param answer
     */
    public void sendToEveryone(Answer answer){
        for(Player p: game.getActivePlayers()){
            sendToPlayer(answer, server.getIDByUsername(p.getUsername()));
        }

    }


    /**
     * This method is used to send an answer from the server to everyone execpt the
     * @param answer
     * @param notToHim
     */
    public void sendToEveryoneExcept(Answer answer, int notToHim) {
        for(Player activePlayers : controller.getGame().getActivePlayers()) {
            if(server.getIDByUsername(activePlayers.getUsername()) != notToHim) {
                sendToPlayer(answer, activePlayers.getID());
            }
        }
    }


    /**
     * This method sends the server's answer to the player.
     * @param answer
     * @param playerID
     */
    public void sendToPlayer(Answer answer, int playerID){
        server.getVirtualPlayerByID(playerID).send(answer);
    }


    /**
     * Method used to start the actual game.
     */
    public void startGame(){
        sendToEveryone(new CustomAnswer(false, "The game is on!\n"));
        setIsStarted(true);

        initialSetup();
    }


    /**
     * This method selects the first player and sets up the game parameters for a new match.
     */
    public void initialSetup(){
        sendToEveryone(new CustomAnswer(false, "Now the first player to play is being randomly selected, be ready, it could be you!"));

        int firstPlayer = 0;
        int min = 0;
        int randomNum = (int) Math.floor(Math.random() *(numOfPlayers - min + 1) + min);
        int i;

        for (i = 0; i < numOfPlayers; i++) {
            if (i != randomNum) {
                game.getPlayers().get(i).setChair(false);
            }
            game.getPlayers().get(i).setChair(true);
            game.setCurrentPlayer(game.getPlayers().get(i));
            firstPlayer = game.getPlayers().get(i).getID();
        }

        sendToEveryoneExcept(new CustomAnswer(false, "The first player is: " + server.getUsernameByID(firstPlayer) + "!"), firstPlayer);
        sendToPlayer(new CustomAnswer(false, "You are the first player! Here's your chair!"), firstPlayer);

        controller.setup();
    }


    /**
     * This method ends the current match for all the players, after a player disconnection.
     */
    public void endMatch(String playerDisconnected) {
        sendToEveryone(new CustomAnswer(false, "Player " + playerDisconnected + " has disconnected :( Game will finish without a winner! Thanks to have played MyShelfie! Hope to see you soon ;)"));
        sendToEveryone(new PlayerDisconnected());
        for(Player p  : game.getActivePlayers()) {
            server.getVirtualPlayerByID(p.getID()).getConnection().disconnect();
        }
    }


    /**
     * alreadyStarted setter.
     * @param bool
     */
    public void setIsStarted(boolean bool){
        this.alreadyStarted = bool;
    }


    /**
     * alreadyStarted getter.
     */
    public boolean isAlreadyStarted(){
        return this.alreadyStarted;
    }


    /**
     * Controller getter.
     * @return
     */
    public Controller getController(){
        return this.controller;
    }


    public void dispatchActions(GameAction action){
        if (action instanceof PickTilesAction){
            pcsController.firePropertyChange("PickTilesAction", null, ((PickTilesAction) action).getTiles());
            return;
        }
        if(action instanceof PlaceTilesAction){
            pcsController.firePropertyChange("PlaceTilesAction", null, ((PlaceTilesAction) action).getCoordinates());
            return;
        }
    }
}
