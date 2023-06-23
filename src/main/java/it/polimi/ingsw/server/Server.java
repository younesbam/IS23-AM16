package it.polimi.ingsw.server;

import it.polimi.ingsw.Const;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.messages.HowManyPlayersResponse;
import it.polimi.ingsw.communications.clientmessages.messages.Message;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorClassification;
import it.polimi.ingsw.communications.serveranswers.info.ConnectionOutcome;
import it.polimi.ingsw.communications.serveranswers.info.PlayerNumberChosen;
import it.polimi.ingsw.communications.serveranswers.requests.DisconnectPlayer;
import it.polimi.ingsw.communications.serveranswers.requests.HowManyPlayersRequest;
import it.polimi.ingsw.controller.Phase;
import it.polimi.ingsw.common.exceptions.OutOfBoundException;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.rmi.RMIServerHandler;
import it.polimi.ingsw.server.socket.ServerSideSocket;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.Const.*;
import static it.polimi.ingsw.controller.Phase.SETUP;

public class Server {
    public final Object clientLock = new Object();
    /**
     * Logger of the server.
     */
    public static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private ServerSideSocket serverSideSocket;

    /**
     * Rmi port, read from json file
     */
    private final int rmiPort;

    /**
     * Socket port, read from json file
     */
    private final int socketPort;

    /**
     * Easily get what's inside the json file
     */
    private final JSONParser jsonParser;

    /**
     * Game handler.
     */
    private GameHandler gameHandler;

    /**
     * Number of players for this match, chosen by the host in the initial phase of the game.
     */
    private int numOfPlayers;

    /**
     * Current player ID, progressive order.
     */
    private int currentPlayerID;

    /**
     * List of players waiting for game to start. List cleared when game start.
     */
    private final List<VirtualPlayer> playersWaitingList = new ArrayList<>();

    /**
     * List of connected players. Use this to ping all connected clients
     */
    private final List<VirtualPlayer> playersConnectedList = new ArrayList<>();

    /**
     * Scheduler service
     */
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    /**
     * Class constructor
     */
    public Server() {
        numOfPlayers = 0;

        /*
        Load parameters: socket and rmi port
         */
        jsonParser = new JSONParser("network.json");
        this.rmiPort = jsonParser.getServerRmiPort();
        this.socketPort = jsonParser.getServerSocketPort();
        LOGGER.log(Level.INFO, "RMI port: " + rmiPort);
        LOGGER.log(Level.INFO, "Socket port: " + socketPort);

        try {
            socketInit(this);
        } catch (Exception e){
            LOGGER.log(Level.SEVERE, "Socket setup failed", e);
            System.exit(-1);
        }
        LOGGER.log(Level.INFO, "Socket setup complete");

        try {
            RMIInit();
        } catch (RemoteException | AlreadyBoundException e){
            LOGGER.log(Level.SEVERE, "RMI setup failed", e);
            System.exit(-1);
        }
        LOGGER.log(Level.INFO, "RMI setup complete");

        System.out.println("Type EXIT to end connection.");


        Thread exitThread = new Thread(this::exitListener);
        exitThread.start();

        /*
        Ping thread to check if clients are still alive
        WARNING: remember to shut down the thread with exec.shutdownNow();
         */
        scheduler.scheduleAtFixedRate(() -> {
            /*
            Each client registered in the server is pinged. If the client doesn't respond, the ping() method proceed itself to disconnect the client.
             */
            try {
                pingClients();
            }catch (Exception e){
                LOGGER.log(Level.WARNING, "Exception thrown",e);
            }

        }, 1, Const.SERVER_PING_DELAY, TimeUnit.SECONDS);
    }


    /**
     * Ping every connected client
     */
    private synchronized void pingClients(){
        for(VirtualPlayer p : playersConnectedList)
            p.getConnection().ping();
    }


    /**
     * Initialize RMI communication.
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    private void RMIInit() throws RemoteException, AlreadyBoundException {
        Registry registry = LocateRegistry.createRegistry(this.rmiPort);
        registry.bind(jsonParser.getServerName(), new RMIServerHandler(this));
    }


    /**
     * Initialize Socket communication.
     * @throws Exception
     */
    private void socketInit(Server server) throws Exception {
        serverSideSocket = new ServerSideSocket(this, this.socketPort);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(server.serverSideSocket);
    }


    /**
     * Dispatch the message to the right action handler, based on the type of the serialized message.
     * @param serializedMessage
     */
    public void onClientMessage(SerializedMessage serializedMessage) {
        VirtualPlayer currentPlayer = getVirtualPlayerByID(serializedMessage.playerID);
        if (serializedMessage.message != null) {
            actionHandler(currentPlayer, serializedMessage.message);
        } else if (serializedMessage.gameAction != null) {
            actionHandler(currentPlayer, serializedMessage.gameAction);
        }
    }

    /**
     * Handles the possible messages that can arrive from client's side.
     * @param message message from client
     */
    private void actionHandler(VirtualPlayer currentPlayer, Message message){
        if(message instanceof HowManyPlayersResponse){
            try{
                setNumOfPlayers(currentPlayer, ((HowManyPlayersResponse) message).getNumChoice());
            } catch (OutOfBoundException e) {
                Server.LOGGER.log(Level.INFO, "Wrong number of players received from client");
                SerializedAnswer answer = new SerializedAnswer();
                answer.setAnswer(new HowManyPlayersRequest("Wrong number of players. Please choose the number of players you want to play with. Type MAN if you dont' know the syntax!"));
                currentPlayer.send(answer.getAnswer());
            }
            return;
        }
    }

    /**
     * Handles the possible game action that can arrive from client's side.
     * @param action game action from client
     */
    private void actionHandler(VirtualPlayer currentPlayer, GameAction action){
        getGameHandlerByID(currentPlayer.getID()).dispatchActions(action);
    }


    /**
     * Try to connect the client to the server and add it to the lobby
     * <p></p>
     * Method used by RMI and Socket when a client is trying to connect for the first time
     * @param usernameChoice Chosen username
     * @param connection Already created connection between client-server
     */
    public void tryToConnect(String usernameChoice, CSConnection connection){
        try {
            connection.setID(newClientRegistration(usernameChoice, connection));
            // If null, simply return. New clients will be added to the active list if the ID is not null.
            if (connection.getID() == null) {
                return;
            }
            lobby(connection);
        } catch (IOException | InterruptedException e) {
            Server.LOGGER.log(Level.SEVERE, "Failed to register the new client", e);
        }
    }


    /**
     * This method is the one that registers the new client to the match. It also checks if the username chosen by the player is not already taken.
     */
    private synchronized Integer newClientRegistration(String username, CSConnection clientConnection) throws IOException{
        SerializedAnswer answer = new SerializedAnswer();
        // Check if the server is in a setup mode. If true it returns null and disconnect the client immediately.
        if(gameHandler != null){  // Game handler null means no one is connected yet
            if(gameHandler.getController().getPhase() == SETUP){
                answer.setAnswer(new ErrorAnswer("Lobby not ready to receive new players. The first player must choose the number of players.", ErrorClassification.LOBBY_NOT_READY));
                clientConnection.sendAnswerToClient(answer);
                return null;
            }
        }

        // Try to register a new client.
        Integer clientID = getIDByUsername(username);

        //checks about nickname
        if (clientID == null) {  // Username not used by another player
            //Checks about waiting list and available slot for the game
            if (numOfPlayers != 0 && (playersWaitingList.size() >= numOfPlayers || playersConnectedList.size() >= numOfPlayers)) {
                answer.setAnswer(new ErrorAnswer("The game already started and the maximum number of players has been reached. Try again when te actual game ends.", ErrorClassification.MAX_PLAYERS_REACHED));
                clientConnection.sendAnswerToClient(answer);
                return null;
            }

            if (playersConnectedList.isEmpty()) {
                gameHandler = new GameHandler(this);  // This set also the SETUP phase
            }

            // Create a new player.
            clientID = newClientID();
            gameHandler.createPlayer(username, clientID);
            clientConnection.setID(clientID);
            VirtualPlayer newPlayer = new VirtualPlayer(username, clientConnection, gameHandler);
            playersConnectedList.add(newPlayer);

            System.out.println(newPlayer.getUsername() + " is now connected, his ID is " + newPlayer.getID());
            answer.setAnswer(new ConnectionOutcome(true, newPlayer.getID(), "Welcome! You have been associated with the following ID " + newPlayer.getID()));
            clientConnection.sendAnswerToClient(answer);

            if (playersConnectedList.size() > 1) {
                gameHandler.sendToEveryoneExcept(new CustomAnswer("We have a new mate! Please call him: " + newPlayer.getUsername() + " :)"), clientID);
            }

        } else {  // Username already in use.
            // Check if a disconnected player want to reconnect with the same username
            VirtualPlayer playerToRestore = getVirtualPlayerByID(clientID);
            if(!playerToRestore.getConnection().isAlive()){
                clientConnection.setID(clientID);
                playerToRestore.restorePlayer(clientConnection);  // Assign the actual client-server connection
                answer.setAnswer(new ConnectionOutcome(true, playerToRestore.getID(), "Welcome back!"));
                clientConnection.sendAnswerToClient(answer);

                restoreClient(clientConnection);

                answer.setAnswer(new RestorePlayer());
                clientConnection.sendAnswerToClient(answer);
            } else {
                answer.setAnswer(new ErrorAnswer("Username already in use. Try to connect again", ErrorClassification.TAKEN_USERNAME));
                clientConnection.sendAnswerToClient(answer);
                return null;
            }
        }
        return clientID;
    }


    /**
     * This is the lobby. Here the players wait for other players to connect, in order to reach the number chosen from the games's host.
     * @param connection
     * @throws InterruptedException
     */
    private synchronized void lobby(CSConnection connection) throws InterruptedException {
        SerializedAnswer answer = new SerializedAnswer();

        answer.setAnswer(new CustomAnswer(BLUE_BOLD_COLOR + "\nType MAN to know all the valid commands\n" + RESET_COLOR));
        connection.sendAnswerToClient(answer);

        // If the game is already started and the player wants to reconnect, skip the lobby phase
        if(gameHandler.isGameStarted())
            return;

        playersWaitingList.add(getVirtualPlayerByID(connection.getID()));
        if(gameHandler.getController().getPhase() == SETUP) { //if it's the first player
            System.out.println(RED_COLOR + "Setup mode started. Clients are not welcome. Wait for the lobby host to choose the number of players" + RESET_COLOR);
            answer.setAnswer(new HowManyPlayersRequest("Hi " + getWaitingPlayerByID(connection.getID()).getUsername() + ", you are now the host of this lobby.\nPlease choose the number of players you want to play with:"));
            connection.sendAnswerToClient(answer);
            gameHandler.getController().setCurrentPlayer(gameHandler.getController().getGame().getPlayers().get(0));
        } else if(playersWaitingList.size() == numOfPlayers) {  // Game has reached the right number of players. Game is starting
            System.out.println(numOfPlayers + " players are now ready to play. Game is starting...");
            for(int i = 3; i > 0; i--) {
                gameHandler.sendToEveryone(new CountDown(i));
                gameHandler.sendToEveryone(new CustomAnswer("Game will start in " + i));
                TimeUnit.MILLISECONDS.sleep(1000);
            }

            // Start the game.
            gameHandler.startGame();
            playersWaitingList.clear();

        } else {
            getWaitingPlayerByID(connection.getID()).send(new CustomAnswer("You're now connected\n"));
            gameHandler.sendToEveryone(new CustomAnswer("There are " + (numOfPlayers - playersWaitingList.size()) + " slots left!"));
        }
    }


    /**
     * Method used to verify if the number of players chosen by the player is in the possible range.
     * @param numOfPlayers
     * @throws OutOfBoundException
     */
    private void setNumOfPlayers(VirtualPlayer player, int numOfPlayers) throws OutOfBoundException{
        /*
        Check if we are in the setup phase, which is true just for the first player. After the first player chooses the number of players, the phase is set to LOBBY,
        and if other players will try to use the PLAYERS command, they will receive an incorrect phase message.
         */
        if(gameHandler.getController().getPhase() != SETUP){
            player.send(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE));
            return;
        }

        /*
        Check if the players are in the right range.
         */
        if (numOfPlayers > 4 || numOfPlayers < 2)
            throw new OutOfBoundException();
        /*
        Set number of players (also in GameHandler)
         */
        this.numOfPlayers = numOfPlayers;
        player.getGameHandler().setNumOfPlayers(numOfPlayers);
        player.send(new PlayerNumberChosen(numOfPlayers));
        System.out.println(GREEN_COLOR + "Setup mode ended. Clients are now welcome!" + RESET_COLOR);
        gameHandler.getController().setPhase(Phase.LOBBY);
    }


    /**
     * This method generates a new client ID.
     * @return
     */
    public synchronized int newClientID() {
        int newID = currentPlayerID;
        currentPlayerID++;
        return newID;
    }


    /**
     * Listen for exit command. In that case, shutdown the server.
     */
    private void exitListener() {
        Scanner in = new Scanner(System.in);
        while (true) {
            if (in.next().equalsIgnoreCase("EXIT")) {
                exit();
            }
        }
    }

    /**
     * Exit command to close all the connection and shutdown the server properly.
     */
    public void exit(){
        // Disconnect players
        synchronized (playersConnectedList){
            for(VirtualPlayer p : playersConnectedList){
                p.send(new DisconnectPlayer());
            }
        }
        // Close threads
        serverSideSocket.shutdown();
        scheduler.shutdownNow();
        // Shutdown the server
        System.out.println("Thanks for playing MyShelfie! Shutting down...");
        System.exit(0);
    }


    /**
     * This method returns the gameHandler belonging to the clientID parameter passed.
     *
     * @param ID
     * @return
     * */
    public synchronized GameHandler getGameHandlerByID(int ID) {
        List<VirtualPlayer> list = List.copyOf(playersConnectedList);
        for(VirtualPlayer p : list){
            if(ID == p.getID()){
                return p.getGameHandler();
            }
        }
        return null;
    }

    /**
     * This method returns the VirtualPlayer instance corresponding to the passed ID.
     * @param ID
     * @return
     */
    public synchronized VirtualPlayer getVirtualPlayerByID(int ID) {
        List<VirtualPlayer> list = List.copyOf(playersConnectedList);
        for(VirtualPlayer p : list){
            if(ID == p.getID()){
                return p;
            }
        }
        return null;
    }

    /**
     * This method returns the corresponding ID of the player with that username.
     * @param username
     * @return
     */
    public synchronized Integer getIDByUsername(String username) {
        List<VirtualPlayer> list = List.copyOf(playersConnectedList);
        for(VirtualPlayer p : list){
            if(username.equals(p.getUsername())){
                return p.getID();
            }
        }
        return null;
    }

    /**
     * This method returns the corresponding username of the player with that ID.
     * @param ID
     * @return
     */
    public synchronized String getUsernameByID(int ID) {
        List<VirtualPlayer> list = List.copyOf(playersConnectedList);
        for(VirtualPlayer p : list){
            if(ID == p.getID()){
                return p.getUsername();
            }
        }
        return null;
    }

    /**
     * Get a waiting player by ID
     * @param ID
     * @return
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
     * Get connected virtual player list
     * @return
     */
    public List<VirtualPlayer> getConnectedPlayers(){
        return playersConnectedList;
    }


    /**
     * This method removes a player from the current server.
     * @param ID
     */
    public synchronized void removePlayer(int ID) {
        System.out.println("Removing player " + getUsernameByID(ID));
        getGameHandlerByID(ID).removePlayer(ID);
        try{
            playersConnectedList.remove(getVirtualPlayerByID(ID));
        }catch (NullPointerException e){
            //
        }

        try{
            playersWaitingList.remove(getWaitingPlayerByID(ID));
        }catch (NullPointerException e){
            //
        }
    }


    /**
     * Suspend a client after failed ping {@link CSConnection#ping() ping} request
     * @param connection
     */
    public void suspendClient(CSConnection connection){
        VirtualPlayer suspendedClient = getVirtualPlayerByID(connection.getID());
        if(suspendedClient == null){
            System.out.println("Player doesn't exist. Impossible to suspend it");
            return;
        }
        System.out.println("Suspending player " + suspendedClient.getUsername() + " ...");
        gameHandler.suspendClient(suspendedClient.getID());
        System.out.println("Player suspended successfully");
    }


    /**
     * Restore client after a {@link Server#suspendClient(CSConnection) suspension} from the server
     * <p></p>
     * Note: in order to properly restore the client, username must be the same of the disconnected client
     * @param connection
     */
    public void restoreClient(CSConnection connection){
        VirtualPlayer restoredClient = getVirtualPlayerByID(connection.getID());
        System.out.println("Restoring player " + restoredClient.getUsername() + " ...");
        gameHandler.restoreClient(restoredClient.getID());
        System.out.println("Player restored successfully");
    }


    /**
     * Main class of the server.
     * @param args
     */
    public static void main(String[] args) {
        System.out.print("Welcome to the server of MyShelfie!\n");
        new Server();
    }
}
