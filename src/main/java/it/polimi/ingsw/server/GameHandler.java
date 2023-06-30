package it.polimi.ingsw.server;

import it.polimi.ingsw.common.exceptions.OutOfBoundException;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PrintCardsAction;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorClassification;
import it.polimi.ingsw.communications.serveranswers.info.EndOfYourTurn;
import it.polimi.ingsw.communications.serveranswers.info.PlayerNumberChosen;
import it.polimi.ingsw.communications.serveranswers.requests.HowManyPlayersRequest;
import it.polimi.ingsw.communications.serveranswers.start.ChairAssigned;
import it.polimi.ingsw.communications.serveranswers.start.CountDown;
import it.polimi.ingsw.communications.serveranswers.start.FirstPlayerSelected;
import it.polimi.ingsw.communications.serveranswers.start.GameReady;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.Phase;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.connection.CSConnection;
import java.util.Random;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static it.polimi.ingsw.Const.*;
import static it.polimi.ingsw.Const.RESET_COLOR;
import static it.polimi.ingsw.controller.Phase.SETUP;

/**
 * Handles the match, controller and game.
 * It's a middle man between server and controller.
 * It dispatches also the actions, received from the client, to the controller.
 */
public class GameHandler {
    /**
     * Game reference.
     */
    private Game game;

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
     * List of players waiting for game to start. List cleared when game start.
     */
    private final List<VirtualPlayer> playersWaitingList = new ArrayList<>();

    /**
     * List of connected players to this match.
     */
    private final List<VirtualPlayer> playersConnected = new ArrayList<>();


    /**
     * Name of this match
     */
    private String nameOfTheMatch;



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
    public void removePlayerFromGame(int playerID){
        game.removePlayer(game.getPlayerByID(playerID));
    }


    /**
     * Method used to remove a player from the players' waiting list.
     * @param playerID
     */
    public void removeWaitingPlayer(int playerID){
        playersWaitingList.remove(getWaitingPlayerByID(playerID));
    }

    /**
     * Method used to remove a player from the players' connected list.
     * This method remove the player from this match only.
     * @param playerID
     */
    public void removeConnectedPlayer(int playerID){
        playersConnected.remove(getConnectedPlayerByID(playerID));
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
     * Set this match's number of players.
     * @param numOfPlayers number of player for this match.
     */
    public void setNumOfPlayers(int numOfPlayers){
        this.numOfPlayers = numOfPlayers;
        game.setNumOfPlayers(numOfPlayers);
    }


    /**
     * Set this match's name.
     * @param name Name of this match.
     */
    public void setNameOfTheMatch(String name){
        this.nameOfTheMatch = name;
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
        for(VirtualPlayer p: playersConnected)
            sendToPlayer(answer, p.getID());
    }


    /**
     * Send an answer to every player except the selected player
     * @param answer answer to be sent to every player except a specific player.
     * @param notToHim ID of the excluded player.
     */
    public void sendToEveryoneExcept(Answer answer, int notToHim) {
        for(VirtualPlayer p : playersConnected) {
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
     * Method used to get the list of this game's connected players.
     * @return List of connected players.
     */
    public List<VirtualPlayer> getPlayersConnected(){
        return List.copyOf(this.playersConnected);
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

        Player firstPlayer = null;
        int firstPlayerID = 0;

        //genera numero casuale tra 0 e il n° di giocatori - 1.
        Random random = new Random();
        int randomNumber =  random.nextInt(numOfPlayers - 1);

        //do la sedia al primo giocatore scelto.
        for (int i = 0; i < numOfPlayers; i++) {
            if (i != randomNumber) {
                game.getPlayers().get(i).setChair(false);
            }
            else {
                game.getPlayers().get(i).setChair(true);
                game.setCurrentPlayer(game.getPlayers().get(i));
                firstPlayerID = game.getPlayers().get(i).getID();
                firstPlayer = game.getPlayers().get(i);
            }
        }

        game.setFirstPlayer(firstPlayer);
        sendToEveryoneExcept(new CustomAnswer("The first player is: " + server.getUsernameByID(firstPlayerID) + "!"), firstPlayerID);
        sendToEveryoneExcept(new FirstPlayerSelected(server.getUsernameByID(firstPlayerID)), firstPlayerID);
        sendToPlayer(new CustomAnswer("You are the first player! Here's your chair! \n " +
                "  __________.\n" +
                "  /_/-----/_/|   \n" +
                "  ( ( ' ' ( (| \n" +
                "  (_( ' ' (_(| \n" +
                "  / /=====/ /| \n" +
                " /_//____/_/ | \n" +
                "(o|:.....|o) | \n" +
                "|_|:_____|_|/' \n"), firstPlayerID);

        sendToPlayer(new ChairAssigned(), firstPlayerID);
        controller.setup();
        sendToEveryone(new GameReady());
    }


    /**
     * This is the lobby. Here the players wait for other players to connect, in order to reach the number chosen from the games's host.
     * @param connection Already created connection between client-server
     * @throws InterruptedException thrown if an error occurs during thread sleep.
     */
    public synchronized void lobby(CSConnection connection) throws InterruptedException {
        SerializedAnswer answer = new SerializedAnswer();

        answer.setAnswer(new CustomAnswer(BLUE_BOLD_COLOR + "\nType MAN to know all the valid commands\n" + RESET_COLOR));
        connection.sendAnswerToClient(answer);

        // If the game is already started and the player wants to reconnect, skip the lobby phase.
        if(isGameStarted())
            return;

        playersWaitingList.add(server.getVirtualPlayerByID(connection.getID()));
        playersConnected.add(server.getVirtualPlayerByID(connection.getID()));
        if(getController().getPhase() == SETUP && playersWaitingList.size() == 1) { //if it's the first player
            System.out.println(RED_COLOR + "Setup mode started for game " + nameOfTheMatch + ". Clients are not welcome. Wait for the lobby host to choose the number of players." + RESET_COLOR);
            answer.setAnswer(new HowManyPlayersRequest("Hi " + getWaitingPlayerByID(connection.getID()).getUsername() + ", you are now the host of this lobby.\nPlease choose the number of players you want to play with: (a number between 2 and 4 is required)"));
            connection.sendAnswerToClient(answer);
            getController().setCurrentPlayer(getController().getGame().getPlayers().get(0));
        } else if(playersWaitingList.size() == numOfPlayers) {  // Game has reached the right number of players. Game is starting.
            System.out.println(numOfPlayers + " players are now ready to play. Game " + nameOfTheMatch + " is starting...");
            for(int i = 3; i > 0; i--) {
                sendToEveryone(new CountDown(i));
                sendToEveryone(new CustomAnswer("Game will start in " + i));
                TimeUnit.MILLISECONDS.sleep(1000);
            }

            // Start the game.
            startGame();
            playersWaitingList.clear();

            }else {
            getWaitingPlayerByID(connection.getID()).send(new CustomAnswer("You're now connected\n"));
            sendToEveryone(new CustomAnswer("There are " + (numOfPlayers - playersWaitingList.size()) + " slots left!"));
            sendToPlayer(new EndOfYourTurn(), connection.getID());
        }
    }


//    /**
//     * This method terminates the game.
//     */
//    public void shutdownServer(){
//        server.exit();
//    }

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
            if (action instanceof PickTilesAction) {
                pcsController.firePropertyChange("PickTilesAction", null, action);
                return;
            }
            if (action instanceof PlaceTilesAction) {
                pcsController.firePropertyChange("PlaceTilesAction", null, action);
                return;
            }
            if (action instanceof PrintCardsAction) {
                pcsController.firePropertyChange("PrintCardsAction", null, action);
                return;
            }
    }

    /**
     * Suspend the selected client after a disconnection. It acts on controller. The player can reconnect and restore the game.
     * @param ID Unique ID of the player to be suspended.
     * @see GameHandler#restoreClient(int)
     */
    public void suspendClient(int ID){
        controller.suspendClient(ID);
    }

    /**
     * Restore the selected client after a disconnection and a reconnection. It acts on controller.
     * @param ID Unique ID of the player that was suspended
     * @see GameHandler#suspendClient(int)
     */
    public void restoreClient(int ID){
        controller.restoreClient(ID);
    }


    /**
     * Get a waiting player by ID.
     * @param ID Unique ID of the player.
     * @return virtual player related to the passed ID.
     */
    public synchronized VirtualPlayer getWaitingPlayerByID(int ID) {
        List<VirtualPlayer> list = List.copyOf(playersWaitingList);
        for(VirtualPlayer p : list){
            if(ID == p.getID()){
                return p;
            }
        }
        return null;
    }

    /**
     * Get a connected player by ID.
     * @param ID Unique ID of the player.
     * @return virtual player related to the passed ID.
     */
    public synchronized VirtualPlayer getConnectedPlayerByID(int ID) {
        List<VirtualPlayer> list = List.copyOf(playersConnected);
        for(VirtualPlayer p : list){
            if(ID == p.getID()){
                return p;
            }
        }
        return null;
    }


    /**
     * Verify if the number of players chosen by the player is in the possible range.
     * @param player player that send to the server the request to set the number of players.
     * @param numOfPlayers number of players in the game.
     * @throws OutOfBoundException thrown if the range of players is not correct.
     */
    public void setNumOfPlayers(VirtualPlayer player, int numOfPlayers) throws OutOfBoundException{
        /*
        Check if we are in the setup phase, which is true just for the first player. After the first player chooses the number of players, the phase is set to LOBBY,
        and if other players will try to use the PLAYERS command, they will receive an incorrect phase message.
         */
        if(getController().getPhase() != SETUP){
            player.send(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE));
            return;
        }

        /*
        Check if the players are in the right range.
         */
        if (numOfPlayers > MAXPLAYERS || numOfPlayers < MINPLAYERS)
            throw new OutOfBoundException();
        /*
        Set number of players (also in GameHandler)
         */
        this.numOfPlayers = numOfPlayers;
        player.getGameHandler().setNumOfPlayers(numOfPlayers);
        player.send(new PlayerNumberChosen(numOfPlayers));
        System.out.println(GREEN_COLOR + "Setup mode ended for game " + nameOfTheMatch + ". Clients are now welcome!" + RESET_COLOR);
        getController().setPhase(Phase.LOBBY);
    }


    /**
     * Name of current match getter.
     * @return Name of this match.
     */
    public String getNameOfTheMatch(){
        return this.nameOfTheMatch;
    }
}
