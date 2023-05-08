package it.polimi.ingsw.server;

import it.polimi.ingsw.Const;
import it.polimi.ingsw.Utils;
import it.polimi.ingsw.client.common.Connection;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.communications.clientmessages.Communication;
import it.polimi.ingsw.communications.clientmessages.UsernameSetup;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.exceptions.OutOfBoundException;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.SocketCSConnection;
import it.polimi.ingsw.server.rmi.RMIServerHandler;
import it.polimi.ingsw.server.socket.ServerSideSocket;
import it.polimi.ingsw.server.utils.ServerPing;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private JSONParser jsonParser;
    private GameHandler gameHandler;
    /**
     * Number of players for this match, chosen by the host in the initial phase of the game.
     */
    private int numOfPlayers;
    int currentPlayerID;

    /**
     * List of players waiting for game to start.
     */
    private final List<CSConnection> playersWaitingList = new ArrayList<>();

    /**
     * Player ID mapped to their username.
     */
    private final Map<Integer, String> IDMapUsername;

    /**
     * Username of players mapped to their ID.
     */
    private final Map<String, Integer> usernameMapID;

    /**
     * VirtualPlayer mapped to its connection.
     */
    private final Map<VirtualPlayer, CSConnection> virtualPlayerToCSConnection;

    /**
     * Player ID mapped to VirtualPlayer.
     */
    private final Map<Integer, VirtualPlayer> IDMapVirtualPlayer;

    /**
     * List of clients represented by nickname and connection
     */
    private Map<String, CSConnection> clients;


    /**
     * Class constructor
     */
    public Server() {
        numOfPlayers = 0;
        usernameMapID = new HashMap<>();
        IDMapUsername = new HashMap<>();
        IDMapVirtualPlayer = new HashMap<>();
        virtualPlayerToCSConnection = new HashMap<>();

        /*
        Load parameters: socket and rmi port
         */
        jsonParser = new JSONParser("json/network.json");
        this.rmiPort = jsonParser.getServerRmiPort();
        this.socketPort = jsonParser.getServerSocketPort();
        
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

        /*
        Verificare questa roba, può essere inglobato nella ricezione di messaggi.
         */
        Thread thread = new Thread(this::quitConnection);
        thread.start();

        /*
        Ping thread to check if clients are still alive
        WARNING: remember to shut down the thread with exec.shutdownNow();
         */
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            /*
            Each client registered in the server is pinged. If the client doesn't respond, the ping() method proceed itself to disconnect the client.
             */
            for (Map.Entry<String, it.polimi.ingsw.server.connection.CSConnection> client : clients.entrySet()) {
                client.getValue().ping();
            }
        }, 1, Const.SERVER_PING_DELAY, TimeUnit.SECONDS);
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
     * This method is the one that registers the new client to the match. It also checks if the username chosen by the player is not already taken.
     */
    public synchronized Integer newClientRegistration(String username, CSConnection clientConnection) {
        Integer clientID = usernameMapID.get(username);

        //checks about nickname
        if (clientID == null) {//l'username scelto va bene, non è già in uso da un altro player.
            if (playersWaitingList.isEmpty()) {
                gameHandler = new GameHandler(this);
            }

            //ho tolto una parte del metodo che sembrava ricontrollare di nuovo se ci fosse un  utente già collegato con lo stesso nome.

            //checks about waiting list and available slot for the game
            clientID = newClientID();
            gameHandler.createPlayer(username, clientID);
            VirtualPlayer virtualPlayer = new VirtualPlayer(username, clientID, clientConnection, gameHandler);

            if (numOfPlayers != 0 && playersWaitingList.size() >= numOfPlayers) {
                virtualPlayer.send(new ErrorAnswer(ErrorClassification.MAXPLAYERSREACHED));
                return null;
            }

            IDMapUsername.put(clientID, username);
            usernameMapID.put(username, clientID);
            IDMapVirtualPlayer.put(clientID, virtualPlayer);
            virtualPlayerToCSConnection.put(virtualPlayer, clientConnection);


            System.out.println(virtualPlayer.getUsername() + "is now connected, his ID is" + virtualPlayer.getID());
            SerializedAnswer nowConnected = new SerializedAnswer();
            nowConnected.setAnswer(new ConnectionOutcome(true, "Welcome! You have been associated with the following ID" + virtualPlayer.getID()));
            clientConnection.sendAnswerToClient(nowConnected);

            if (playersWaitingList.size() > 1) {
                gameHandler.sendToEveryoneExcept(new NewPlayerHasJoined("We have a new mate! Please call him: " + virtualPlayer.getUsername() + " :)"), clientID);
            }

        } else { //username già in uso!
            VirtualPlayer registeredClient = IDMapVirtualPlayer.get(clientID);
            if (registeredClient.getConnection() != null) {
                SerializedAnswer duplicateNicknameError = new SerializedAnswer();
                duplicateNicknameError.setAnswer(new ErrorAnswer(ErrorClassification.TAKENUSERNAME));
                clientConnection.sendAnswerToClient(duplicateNicknameError);
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
    public synchronized void lobby(CSConnection connection) throws InterruptedException {
        playersWaitingList.add(connection); //new connected player

        if(playersWaitingList.size() == 1) { //if it's the first player
            connection.setupPlayers(new HowManyPlayersRequest("Hi " + IDMapVirtualPlayer.get(connection.getID()).getUsername() + " you are now the host of this lobby.\nPlease choose the number of player you want to play with [2, 3, 4]:"));
        } else if(playersWaitingList.size() == numOfPlayers) {
            System.out.println(numOfPlayers + " players are now ready to play. Game is starting...");
            for(int i = 3; i > 0; i--) {
                gameHandler.sendToEveryone(new PersonalizedAnswer(false, "Game will start in" + i));
                TimeUnit.MILLISECONDS.sleep(1000);
            }

            gameHandler.initialSetup();

        } else {
            gameHandler.sendToEveryone(new PersonalizedAnswer(false, "There are " + (numOfPlayers - playersWaitingList.size()) + " slots left!"));
        }
    }


    /**
     * Method used to verify if the number of players chosen by the player is in the possible range.
     * @param numOfPlayers
     * @throws OutOfBoundException
     */
    public void setNumOfPlayers(int numOfPlayers) throws OutOfBoundException{
        if(numOfPlayers > 4 || numOfPlayers < 2)
            throw new OutOfBoundException();
        else
            this.numOfPlayers = numOfPlayers;
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // START COMMENTS
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    /*
    Io metterei qui i metodi (che ora sono commentati ma si trovano ancora in CSConnection.
    Il metodo onMessage gestisce tutti i messaggi in arrivo dal client.
     */

    /**
     * Server looking for new incoming messages from clients.
     * @param messageFromClient
     */
    public void onMessage(Communication messageFromClient){
        /*
        Non penso abbia senso controllare la connessione e se esiste il player, non viene già fatto nel metodo sopra "newClientRegistration"?
        Lì si setta l'username, è un doppio controllo questo oppure un refuso?
         */
        if (messageFromClient instanceof UsernameSetup)
            checkConnection((UsernameSetup) messageFromClient);

        /*
        Qui andrei a gestire l'arrivo dei messaggi, in un modo simile a questo.
        Il virtualPlayer ha già tutte le informazioni di cui ho bisogno il metodo per poter spedire il messaggio.
         */
        Answer answerToClient = gameHandler.interpretaIlMessaggio(messageFromClient);
        sendAnswerToClient(virtualPlayer, answerToClient);
    }


    /*
    Qui viene gestito l'invio di messaggi.
    ATTENZIONE: lo stesso metodo è presente anche in CSConnection perchè ogni protocollo implemeterà l'invio di messaggi a modo suo!
     */
    public void sendAnswerToClient(VirtualPlayer virtualPlayer, SerializedAnswer answer){
        CSConnection connection = virtualPlayer.getConnection();
        try{
            connection.sendAnswerToClient(answer);
        } catch (RemoteException e){
            LOGGER.log(Level.SEVERE, "Failed to send message to the client", e);
        }
    }



    /*
    HA SENSO QUESTO NOME? NON CONTROLLA LA CONNESSIONE MA L'USERNAME SE ESISTE GIA' (?).
    Come detto sopra non penso abbia senso qui comunque.

    E' più sensato un metodo ping (che è già dichiarato in CSConnection e già implementato a livello RMI) che pinga i client e setta alive=false se non rispondono.
    Viene già avviato da server appena istanziato (vedi costruttore del server in fondo viene chiamato il ping su ogni client). Sarà poi il metodo ping ad occuparsi
    di settare o no il bit di alive a 0 se il client non risponde.
     */
    /**
     * This method is used to check if the player trying to connect to the server
     * @param usernameChoice
     */
    private void checkConnection(UsernameSetup usernameChoice) {
        try {
            ID = newClientRegistration(usernameChoice.getUsername(), this);
            if (ID == null) {
                alive = false;
                return;
            }
            server.lobby(this);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    // END COMMENTS
    // --------------------------------------------------------------------------------------------------------------------------------------------------------------
    
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
    public void quitConnection() {
        //DA MODIFICARE E FARE IN MODO CHE CON "QUIT" DI CHIUDA ANCHE LA CONNESSIONE RMI (IF/ELSE)
        //forse sto metodo è inutile e poteva essere gestito come un normale messaggio (?)
        Scanner in = new Scanner(System.in);
        while (true) {
            if (in.next().equalsIgnoreCase("QUIT")) {
                serverSideSocket.setIsActive(false);
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
    public GameHandler getGameHandlerByID(int ID) {
            return IDMapVirtualPlayer.get(ID).getGameHandler();
    }

    /**
     * This method returns the VirtualPlayer instance corresponding to the passed ID.
     * @param ID
     * @return
     */
    public VirtualPlayer getVirtualPlayerByID(int ID) {
        return IDMapVirtualPlayer.get(ID);
    }


    /**
     * This method returns the corresponding ID of the player with that username.
     * @param username
     * @return
     */
    public int getIDByUsername(String username){
        return usernameMapID.get(username);
    }


    /**
     * This method returns the corresponding username of the player with that ID.
     * @param ID
     * @return
     */
    public String getUsernameByID(int ID){
        return IDMapUsername.get(ID);
    }



    /**
     * This method removes a player from the current server.
     * @param ID
     */
    public synchronized void removePlayer(int ID){
        getGameHandlerByID(ID).removePlayer(ID);
        VirtualPlayer player = IDMapVirtualPlayer.get(ID);
        System.out.println("Removing player" + player.getUsername());
        IDMapVirtualPlayer.remove(ID);
        usernameMapID.remove(player.getUsername());
        playersWaitingList.remove(virtualPlayerToCSConnection.get(player));
        IDMapUsername.remove(player.getID());
        virtualPlayerToCSConnection.remove(player);
        LOGGER.log(Level.INFO,"The player has been successfully removed from the game.");
    }


    /**
     * Main class of the server.
     * @param args
     */
    public static void main(String[] args) {
        Utils.printLogo();
        System.out.print("Welcome to the server of MyShelfie!");
        new Server();
    }
}
