package it.polimi.ingsw.server;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.communications.clientmessages.messages.ExitFromGame;
import it.polimi.ingsw.communications.clientmessages.messages.HowManyPlayersResponse;
import it.polimi.ingsw.communications.clientmessages.messages.Message;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorClassification;
import it.polimi.ingsw.communications.serveranswers.info.ConnectionOutcome;
import it.polimi.ingsw.communications.serveranswers.info.PlayerNumberChosen;
import it.polimi.ingsw.communications.serveranswers.requests.HowManyPlayersRequest;
import it.polimi.ingsw.exceptions.OutOfBoundException;
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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static it.polimi.ingsw.Const.*;

public class Server {
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
     * List of players waiting for game to start.
     */
    private final List<VirtualPlayer> playersWaitingList = new ArrayList<>();

    /**
     * List of connected players. Used to do some tasks, for instance ping all the connected clients.
     */
    private final List<VirtualPlayer> playersConnectedList = new ArrayList<>();

    /**
     * Represent that the first player doesn't answer to the number of players question.
     * During this mode, new players can't connect, until the server knows the maximum number of players.
     */
    private boolean setupMode;


    /**
     * Class constructor
     */
    public Server() {
        numOfPlayers = 0;

        /*
        Load parameters: socket and rmi port
         */
        jsonParser = new JSONParser("json/network.json");
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


        Thread thread = new Thread(this::exit);
        thread.start();

        /*
        Ping thread to check if clients are still alive
        WARNING: remember to shut down the thread with exec.shutdownNow();
         */
//        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
//        exec.scheduleAtFixedRate(() -> {
//            /*
//            Each client registered in the server is pinged. If the client doesn't respond, the ping() method proceed itself to disconnect the client.
//             */
//            for (VirtualPlayer p : playersConnectedList) {
//                p.getConnection().ping();
//            }
//        }, 1, Const.SERVER_PING_DELAY, TimeUnit.SECONDS);
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
                answer.setAnswer(new HowManyPlayersRequest("Wrong number of players. Please choose the number of players you want to play with.\n Type MAN if you dont' know the syntax!"));
                currentPlayer.send(answer.getAnswer());
            }
        }
        else if(message instanceof ExitFromGame){
            getGameHandlerByID(currentPlayer.getID()).sendToEveryoneExcept(new CustomAnswer(false, "Player " + getUsernameByID(currentPlayer.getID()) + " has disconnected from the game!"), currentPlayer.getID());
            getGameHandlerByID(currentPlayer.getID()).endMatch(getUsernameByID(currentPlayer.getID()));
            getVirtualPlayerByID(currentPlayer.getID()).getConnection().disconnect();
        }

    }

    /**
     * Handles the possible game action that can arrive from client's side.
     * @param action game action from client
     */
    private void actionHandler(VirtualPlayer currentPlayer, GameAction action){
        if(action instanceof PickTilesAction || action instanceof PlaceTilesAction){
            getGameHandlerByID(currentPlayer.getID()).dispatchActions(action);
        }
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
            if (connection.getID() == null) {
                connection.disconnect();
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
        if(setupMode){
            answer.setAnswer(new ErrorAnswer(ErrorClassification.LOBBY_NOT_READY));
            clientConnection.sendAnswerToClient(answer);
            return null;
        }

        //Checks about waiting list and available slot for the game
        if (numOfPlayers != 0 && (playersWaitingList.size() >= numOfPlayers || playersConnectedList.size() >= numOfPlayers)) {
            answer.setAnswer(new ErrorAnswer(ErrorClassification.MAX_PLAYERS_REACHED));
            clientConnection.sendAnswerToClient(answer);
            return null;
        }

        // Try to register a new client.
        Integer clientID = getIDByUsername(username);

        //checks about nickname
        if (clientID == null) {  // Username not used by another player
            if (playersConnectedList.isEmpty()) {
                gameHandler = new GameHandler(this);
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
                gameHandler.sendToEveryoneExcept(new CustomAnswer(false,"We have a new mate! Please call him: " + newPlayer.getUsername() + " :)"), clientID);
            }

        } else {  // Username already in use.
            answer.setAnswer(new ErrorAnswer("Username already in use. Try to connect again", ErrorClassification.TAKEN_USERNAME));
            clientConnection.sendAnswerToClient(answer);
            return null;
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

        answer.setAnswer(new CustomAnswer(false, BLUE_BOLD_COLOR + "\nType MAN to know all the valid commands\n" + RESET_COLOR));
        connection.sendAnswerToClient(answer);

        playersWaitingList.add(getVirtualPlayerByID(connection.getID()));
        if(playersWaitingList.size() == 1) { //if it's the first player
            setupMode = true;  // Start the setup mode.
            System.out.println(RED_COLOR + "Setup mode started. Clients are not welcome. Wait for the lobby host to choose the number of players" + RESET_COLOR);
            answer.setAnswer(new HowManyPlayersRequest("Hi " + getWaitingPlayerByID(connection.getID()).getUsername() + ", you are now the host of this lobby.\nPlease choose the number of players you want to play with:"));
            connection.sendAnswerToClient(answer);
        } else if(playersWaitingList.size() == numOfPlayers) {  // Game has reached the right number of players. Game is starting
            System.out.println(numOfPlayers + " players are now ready to play. Game is starting...");
            for(int i = 3; i > 0; i--) {
                gameHandler.sendToEveryone(new CustomAnswer(false, "Game will start in " + i));
                TimeUnit.MILLISECONDS.sleep(1000);
            }

            // Start the game.
            gameHandler.startGame();
            playersWaitingList.clear();

        } else {
            getWaitingPlayerByID(connection.getID()).send(new CustomAnswer(false, "You're now connected\n"));
            gameHandler.sendToEveryone(new CustomAnswer(false, "There are " + (numOfPlayers - playersWaitingList.size()) + " slots left!"));
        }
    }


    /**
     * Method used to verify if the number of players chosen by the player is in the possible range.
     * @param numOfPlayers
     * @throws OutOfBoundException
     */
    private void setNumOfPlayers(VirtualPlayer player, int numOfPlayers) throws OutOfBoundException{
        /*
        Check if the players are in the right range.
         */
        if(numOfPlayers > 4 || numOfPlayers < 2)
            throw new OutOfBoundException();
        /*
        Set number of players (also in GameHandler)
         */
        this.numOfPlayers = numOfPlayers;
        player.getGameHandler().setNumOfPlayers(numOfPlayers);
        player.send(new PlayerNumberChosen(numOfPlayers));
        setupMode = false;  // Stop the setup mode. Now the server can accept new players.
        System.out.println(GREEN_COLOR + "Setup mode ended. Clients are now welcome!" + RESET_COLOR);
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
     * This method closes all the connections to the server.
     * */
    public void exit() {
        //DA MODIFICARE E FARE IN MODO CHE CON "QUIT" DI CHIUDA ANCHE LA CONNESSIONE RMI (IF/ELSE)
        Scanner in = new Scanner(System.in);
        while (true) {
            if (in.next().equalsIgnoreCase("EXIT")) {
                serverSideSocket.setIsActive(false);
                for(VirtualPlayer p : playersConnectedList){
                    p.send(new PlayerDisconnected());
                }
                System.exit(0);
                break;
            }
        }
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
     * Main class of the server.
     * @param args
     */
    public static void main(String[] args) {
        Utils.printLogo();
        System.out.print("Welcome to the server of MyShelfie!\n");
        new Server();
    }
}
