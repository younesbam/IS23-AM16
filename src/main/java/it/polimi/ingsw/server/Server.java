package it.polimi.ingsw.server;

import it.polimi.ingsw.Const;
import it.polimi.ingsw.MyShelfie;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.messages.CreateGameMessage;
import it.polimi.ingsw.communications.clientmessages.messages.HowManyPlayersResponse;
import it.polimi.ingsw.communications.clientmessages.messages.Message;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorClassification;
import it.polimi.ingsw.communications.serveranswers.network.ConnectionOutcome;
import it.polimi.ingsw.communications.serveranswers.info.PlayerNumberChosen;
import it.polimi.ingsw.communications.serveranswers.network.RestorePlayer;
import it.polimi.ingsw.communications.serveranswers.requests.HowManyPlayersRequest;
import it.polimi.ingsw.communications.serveranswers.start.CountDown;
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

    /**
     * Logger of the server.
     */
    public static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    /**
     * Socket representation class.
     */
    private ServerSideSocket serverSideSocket;

    /**
     * Server IP address.
     */
    private final String serverIP;

    /**
     * Rmi port, read from json file.
     */
    private final int rmiPort;

    /**
     * Socket port, read from json file.
     */
    private final int socketPort;

    /**
     * Current player ID, progressive order.
     */
    private int currentPlayerID;

    /**
     * List of all connected players regardless of the lobby. Use this to ping all connected clients
     */
    private final List<VirtualPlayer> playersConnectedList = new ArrayList<>();

    /**
     * Scheduler service. It executes tasks every n seconds, specified below.
     */
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * HashMap used to associate each Match to its name.
     */
    private final Map<String, GameHandler> matchMap = new HashMap<>();



    /**
     * Class constructor
     */
    public Server(String serverIP, int rmiPort, int socketPort) {
        System.out.print("Welcome to the server of MyShelfie!\n");

        this.serverIP = serverIP;
        this.rmiPort = rmiPort;
        this.socketPort = socketPort;

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

        // Run a thread to listen for the "exit" command.
        Thread exitThread = new Thread(this::exitListener);
        exitThread.start();

        /*
        Ping thread to check if clients are still alive
        WARNING: remember to shut down the thread with exec.shutdownNow();
         */

        scheduler.scheduleAtFixedRate(() -> {
            /*
            Each client registered in the server is pinged. If the client doesn't respond, the ping() method proceed itself to suspend the client
             */
            try {
                pingClients();
            }catch (Exception e){
                LOGGER.log(Level.SEVERE, "Error while pinging clients");
                //e.printStackTrace();
            }

        }, 1, Const.SERVER_PING_DELAY, TimeUnit.SECONDS);
    }


    /**
     * Ping every connected client.
     */
    private synchronized void pingClients(){
        List<VirtualPlayer> list = List.copyOf(playersConnectedList);
        for(VirtualPlayer p : list)
            if(playersConnectedList.contains(p))
                p.getConnection().ping();
    }


    /**
     * Initialize RMI communication.
     * @throws RemoteException thrown if the registry cannot be exported.
     * @throws AlreadyBoundException thrown if the name is already bound.
     */
    private void RMIInit() throws RemoteException, AlreadyBoundException {
        System.setProperty("java.rmi.server.hostname", this.serverIP);
        Registry registry = LocateRegistry.createRegistry(this.rmiPort);
        registry.bind("MyShelfieServer", new RMIServerHandler(this));
    }


    /**
     * Initialize Socket communication.
     * @throws Exception thrown if socket cannot be initialized.
     */
    private void socketInit(Server server) throws Exception {
        serverSideSocket = new ServerSideSocket(this, this.socketPort);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(server.serverSideSocket);
    }


    /**
     * Dispatch the message to the right action handler, based on the type of the serialized message.
     * @param serializedMessage message received from client.
     */
    public void onClientMessage(SerializedMessage serializedMessage) {
        VirtualPlayer currentPlayer = getVirtualPlayerByID(serializedMessage.playerID);
        if (serializedMessage.message != null) {
            actionHandler(currentPlayer, serializedMessage.message);
        } else if (serializedMessage.gameAction != null && currentPlayer.getGameHandler() != null) {
            actionHandler(currentPlayer, serializedMessage.gameAction);
        } else{
            SerializedAnswer answer = new SerializedAnswer();
            answer.setAnswer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE));
            currentPlayer.send(answer.getAnswer());
        }
    }

    /**
     * Handles the possible messages that can arrive from client's side.
     * @param message message from client
     */
    private void actionHandler(VirtualPlayer currentPlayer, Message message) {
        if (message instanceof HowManyPlayersResponse) {
            try {
                getGameHandlerByID(currentPlayer.getID()).setNumOfPlayers(currentPlayer, ((HowManyPlayersResponse) message).getNumChoice());
            } catch (OutOfBoundException e) {
                Server.LOGGER.log(Level.INFO, "Wrong number of players received from client");
                SerializedAnswer answer = new SerializedAnswer();
                answer.setAnswer(new HowManyPlayersRequest("Wrong number of players. Please choose the number of players you want to play with. Type MAN if you dont' know the syntax!"));
                currentPlayer.send(answer.getAnswer());
            } catch(Exception e){
                SerializedAnswer answer = new SerializedAnswer();
                answer.setAnswer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE));
                currentPlayer.send(answer.getAnswer());
            }
            return;
        } else {
            Answer answer = new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE);
            currentPlayer.send(answer);
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
        } catch (IOException e){
            Server.LOGGER.log(Level.SEVERE, "Failed to register the new client");
        }
    }


    /**
     * Register the new client to the match. It also checks if the username chosen by the player is not already taken.
     * @param username username of the client that want to register.
     * @param clientConnection Already created connection between client-server.
     * @throws IOException thrown if there are communication problem.
     */
    private synchronized Integer newClientRegistration(String username, CSConnection clientConnection) throws IOException{
        SerializedAnswer answer = new SerializedAnswer();

        // Try to register a new client.
        Integer clientID = getIDByUsername(username);

        //checks about nickname
        if (clientID == null) {  // Username not used by another player

            // Create a new player.
            clientID = newClientID();
            clientConnection.setID(clientID);
            clientConnection.setUsername(username);
            VirtualPlayer newPlayer = new VirtualPlayer(username, clientConnection);
            playersConnectedList.add(newPlayer);

            System.out.println(username + " is now connected, his ID is " + clientID);
            answer.setAnswer(new ConnectionOutcome(true, clientID, "Welcome! You have been associated with the following ID: " + clientID));
            clientConnection.sendAnswerToClient(answer);

            answer.setAnswer(new CustomAnswer("\nType CREATE <Name_Of_The_Game> if you want to create a new game, or type JOIN <Name_Of_The_Game> if you want to join an existing one.\n>"));
            clientConnection.sendAnswerToClient(answer);

        } else {  // Username already in use.
            // Check if a disconnected player wants to reconnect with the same username
            VirtualPlayer playerToRestore = getVirtualPlayerByID(clientID);
            if(!playerToRestore.getConnection().isAlive()){
                // Restore player parameters
                clientConnection.setID(clientID);
                playerToRestore.restorePlayer(clientConnection);  // Assign the actual client-server connection
                answer.setAnswer(new ConnectionOutcome(true, playerToRestore.getID(), "Welcome back!"));
                clientConnection.sendAnswerToClient(answer);

                // Restore the player in the game
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
     * Method used in order to create a new match.
     * @param connection Already created connection between client-server.
     * @param matchName Name of the match chosen by the player.
     * @throws InterruptedException
     */
    public synchronized void createNewMatch(CSConnection connection, String matchName) throws InterruptedException {
        boolean newPlayer = false;

        //check if the player hasn't already joined a match.
        for(VirtualPlayer p : playersConnectedList){
            if(p.getUsername().equals(connection.getUsername()) && p.getGameHandler() == null){
                newPlayer = true;
            }
        }


        //if he hasn't joined a match yet, he can create a new one.
        if(newPlayer && !matchMap.containsKey(matchName)) {

            GameHandler gameHandler = new GameHandler(this);
            gameHandler.setNameOfTheMatch(matchName);
            gameHandler.createPlayer(connection.getUsername(), connection.getID());

            //set the player's game handler
            for (int i = 0; i < playersConnectedList.size(); i++) {
                if (playersConnectedList.get(i).getID() == connection.getID()) {
                    playersConnectedList.get(i).setGameHandler(gameHandler);
                }
            }
            matchMap.put(matchName, gameHandler);

            gameHandler.lobby(connection);

        }else if(newPlayer && matchMap.containsKey(matchName)){
            SerializedAnswer answer = new SerializedAnswer();
            answer.setAnswer(new ErrorAnswer("A match with that name already exists! Please choose another name for your match!", ErrorClassification.INVALID_MATCH_NAME));
            connection.sendAnswerToClient(answer);
        }
        else{
            SerializedAnswer answer = new SerializedAnswer();
            answer.setAnswer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE));
            connection.sendAnswerToClient(answer);
        }
    }


    /**
     * Method used to join an already existing match.
     * @param matchName Name of the match the player wants to join.
     * @param connection Already created connection between client-server.
     * @throws InterruptedException if the lobby timer is interrupted while sleeping.
     */
    public synchronized void joinMatch(String matchName, CSConnection connection) throws InterruptedException {
        GameHandler gameHandler = matchMap.get(matchName);
        boolean newPlayer = false;

        //check if the player hasn't already joined a match.
        for(VirtualPlayer p : playersConnectedList){
            if(p.getUsername().equals(connection.getUsername()) && p.getGameHandler() == null){
                newPlayer = true;
            }
        }

        //if he hasn't joined a match yet, he can join one.
        if(newPlayer) {
            if (gameHandler != null) {
                if (gameHandler.getController().getPhase() == SETUP) {
                    SerializedAnswer answer = new SerializedAnswer();
                    answer.setAnswer(new ErrorAnswer("Lobby not ready to receive new players. The first player must choose the number of players.", ErrorClassification.LOBBY_NOT_READY));
                    connection.sendAnswerToClient(answer);
                } else if(gameHandler.isGameStarted()){
                    SerializedAnswer answer = new SerializedAnswer();
                    answer.setAnswer(new ErrorAnswer("The game already started and the maximum number of players has been reached. You can create a new game or join another existing one.", ErrorClassification.MAX_PLAYERS_REACHED));
                    connection.sendAnswerToClient(answer);
                }else {
                    gameHandler.createPlayer(connection.getUsername(), connection.getID());

                    //set player's game handler.
                    for (int i = 0; i < playersConnectedList.size(); i++) {
                        if (playersConnectedList.get(i).getID() == connection.getID()) {
                            playersConnectedList.get(i).setGameHandler(gameHandler);
                        }
                    }

                    gameHandler.sendToEveryoneExcept(new CustomAnswer("We have a new mate! Please call him: " + connection.getUsername() + " :)"), connection.getID());
                    gameHandler.lobby(connection);
                }
            } else {
                SerializedAnswer answer = new SerializedAnswer();
                answer.setAnswer(new ErrorAnswer("This match doesn't exist yet! You can create a new match or wait for others to create one.", ErrorClassification.MATCH_NOT_FOUND));
                connection.sendAnswerToClient(answer);
                answer.setAnswer(new ErrorAnswer("This match doesn't exist yet! You can create a new match or wait for others to create one.", ErrorClassification.MATCH_NOT_FOUND));
                connection.sendAnswerToClient(answer);
            }
        }else{
            SerializedAnswer answer = new SerializedAnswer();
            answer.setAnswer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE));
            connection.sendAnswerToClient(answer);
        }
    }


    /**
     * This method generates a new client ID.
     * @return generated client ID.
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
        // Close threads
        serverSideSocket.shutdown();
        scheduler.shutdownNow();
        // Shutdown the server
        System.out.println("Thanks for playing MyShelfie! Shutting down...");
        System.exit(0);
    }


    /**
     * This method returns the gameHandler belonging to the clientID parameter passed.
     * @param ID Unique ID of the player.
     * @return game handler reference.
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
     * @param ID Unique ID of the player.
     * @return virtual player corresponding to the passed ID.
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
     * @param username username of the player
     * @return Unique ID of the player corresponding to the passed username.
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
     * @param ID Unique ID of the player.
     * @return username of the player with the corresponding ID.
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


//    /**
//     * Get connected virtual player list.
//     * @return list of connected players.
//     */
//    public List<VirtualPlayer> getConnectedPlayers(){
//        return playersConnectedList;
//    }


    /**
     * This method removes a player from the current server, both from connected list and waiting list (if it exists).
     * @param ID Unique ID of the player.
     */
    public synchronized void removePlayer(int ID) {
        try{
            getGameHandlerByID(ID).removeWaitingPlayer(ID);
        }catch (NullPointerException e){
            //
        }
        try{
            getGameHandlerByID(ID).removeConnectedPlayer(ID);
        }catch (NullPointerException e){
            //
        }
        try{
            getGameHandlerByID(ID).removePlayerFromGame(ID);
        }catch (NullPointerException e){
            //
        }
        try{
            playersConnectedList.remove(getVirtualPlayerByID(ID));
        }catch (NullPointerException e){
            //
        }
    }


    /**
     * Method used to remove a gameHandler instance from the matchMap after the game has come to an end.
     * @param matchName The name of the match to remove.
     */
    public synchronized void removeGameHandler(String matchName){
        for(int i = 0; i < matchMap.size(); i++){
            if(matchMap.containsKey(matchName)){
                matchMap.remove(matchName);
            }
        }
    }


    /**
     * Suspend a client after failed ping {@link CSConnection#ping() ping} request
     * @param connection client-server connection of the client to be suspended.
     * @see Server#restoreClient(CSConnection)
     */
    public synchronized void suspendClient(CSConnection connection){
        VirtualPlayer suspendedClient = getVirtualPlayerByID(connection.getID());
        if(suspendedClient == null){
            System.out.println("Player doesn't exist. Impossible to suspend it");
            return;
        }
        Integer ID = connection.getID();

        // Check if game handler has not been created yet.
        if(getGameHandlerByID(ID) == null){
            System.out.println("Removing player " + suspendedClient.getUsername() + " ...");
            removePlayer(ID);
            System.out.println("Player successfully removed");
            return;
        }

        // Check if in lobby phase, remove the player from the lobby.
        if(getGameHandlerByID(ID).getController().getPhase() == Phase.LOBBY){
            System.out.println("Removing player " + suspendedClient.getUsername() + " from the lobby ...");
            removePlayer(ID);
            System.out.println("Player successfully removed from the lobby");
            return;
        }
        // Check if in setup mode
        if(getGameHandlerByID(ID).getController().getPhase() == Phase.SETUP) {
            System.out.println("Removing player " + suspendedClient.getUsername() + " ...");
            System.out.println("The number of players for this lobby has not been chosen. The created game will be deleted...");
            removePlayer(ID);
            removeGameHandler(suspendedClient.getGameHandler().getNameOfTheMatch());
            System.out.println("Player successfully removed");
            System.out.println("Game successfully deleted");
            return;
        }
        // Game started, suspend the player. The player can reconnect with the same username
        System.out.println("Suspending player " + suspendedClient.getUsername() + " ...");
        getGameHandlerByID(ID).suspendClient(ID);
        System.out.println("Player successfully suspended");
        return;
    }


    /**
     * Restore client after a {@link Server#suspendClient(CSConnection) suspension} from the server.
     * <p></p>
     * Note: in order to properly restore the client, username must be the same of the disconnected client
     * @param connection client-server connection of the client to be restored.
     */
    public synchronized void restoreClient(CSConnection connection){
        VirtualPlayer restoredClient = getVirtualPlayerByID(connection.getID());
        System.out.println("Restoring player " + restoredClient.getUsername() + " ...");
        getGameHandlerByID(connection.getID()).restoreClient(restoredClient.getID());
        System.out.println("Player restored successfully");
    }


    /**
     * Main class of the server.
     * @param args
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String serverIP;
        int rmiPort;
        int socketPort;

        while(true){
            // IP address
            System.out.println("Insert the IP Address of the server");
            System.out.print(">");
            serverIP = in.nextLine();
            // RMI port
            System.out.println("Insert the RMI port number of the server, it should be a number between 1024 and 65565");
            System.out.print(">");
            try {
                rmiPort = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                rmiPort = -1;
            }
            // Socket port
            System.out.println("Insert the SOCKET port number of the server, it should be a number between 1024 and 65565");
            System.out.print(">");
            try {
                socketPort = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException e) {
                socketPort = -1;
            }
            if(!serverIP.equals("") && rmiPort>0 && socketPort>0)
                break;
            System.out.println("Wrong input");
        }
        // Reset input
        in.reset();

        new Server(serverIP, rmiPort, socketPort);
    }
}
