package it.polimi.ingsw.server;

import it.polimi.ingsw.Const;
import it.polimi.ingsw.Utils;
import it.polimi.ingsw.client.common.ClientConnection;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.communications.clientmessages.Communication;
import it.polimi.ingsw.communications.clientmessages.UsernameSetup;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.SocketCSConnection;
import it.polimi.ingsw.server.rmi.RMIServerHandler;
import it.polimi.ingsw.server.socket.ServerSideSocket;
import it.polimi.ingsw.server.utils.ServerPing;

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
    private int numOfPlayers;
    int currentPlayerID;

    /**
     * List of players waiting for game to start.
     */
    private final List<SocketCSConnection> playersWaitingList = new ArrayList<>();

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

        /*
        Socket initialization
         */
        try {
            socketInit(this);
        } catch (Exception e){
            LOGGER.log(Level.SEVERE, "Socket setup failed", e);
            System.exit(-1);
        }
        LOGGER.log(Level.INFO, "Socket setup complete");

        /*
        RMI initialization
         */
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
            for (Map.Entry<String, CSConnection> client : clients.entrySet()) {
                if (client.getValue().isAlive()) {
                    client.getValue().ping();
                }
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


    public void actionHandler(Communication message) {
        if (message instanceof UsernameSetup) {
            checkConnection((UsernameSetup) message);

        }
    }

    public void checkConnection(UsernameSetup usernameChoice) {
        try {
            currentPlayerID = registerClient(usernameChoice.getUsername(),  usernameChoice.getConnectionType());
            if (clientID == null) {
                activeConnection = false;
                return;
            }
            server.lobby(this);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            Thread.currentThread().interrupt();
        }


        /*
        Qui va gestito il giocatore, se il nickname è già esistente si fa qualcosa altrimenti si aggiunge alla lobby
        Da verificare anche il controller (la lobby dovrebbe appartenere al controller)
         */
        }


    public synchronized Integer registerClient(String clientNickname, ClientConnection clientConnection) {
        Integer clientID = usernameMapID.get(clientNickname);

        //checks about nickname
        if (clientID == null) {
            if (playersWaitingList.isEmpty()) {
                gameHandler = new GameHandler(this);
            }
            if (usernameMapID.keySet().stream().anyMatch(clientNickname::equalsIgnoreCase)) {
                SerializedAnswer duplicateNicknameError = new SerializedAnswer();
                duplicateNicknameError.setServerAnswer(new ServerError(ServerErrorTypes.DUPLICATENICKNAME));
                socketClientConnection.sendServerMessage(duplicateNicknameError);
                return null;
            }

            //checks about waiting list and available slot for the game
            clientID = generateNewClientID();
            gameHandler.addGamePlayer(clientNickname, clientID);
            VirtualClientView virtualClientView = new VirtualClientView(clientID, clientNickname, socketClientConnection, gameHandler);
            if (totalGamePlayers != 0 && playersWaitingList.size() >= totalGamePlayers) {
                virtualClientView.sendAnswerToClient(new ServerError(ServerErrorTypes.FULLGAMESERVER));
                return null;
            }

            idMapNickname.put(clientID, clientNickname);
            usernameMapID.put(clientNickname, clientID);
            idMapVirtualClient.put(clientID, virtualClientView);
            virtualClientToClientConnection.put(virtualClientView, socketClientConnection);


            System.out.println("Player " + virtualClientView.getClientNickname() + " has successfully connected with id " + virtualClientView.getClientID());
            SerializedAnswer connectionCompleted = new SerializedAnswer();
            connectionCompleted.setServerAnswer(new ConnectionResult("Congrats! You successfully connected with id " + virtualClientView.getClientID(), true));
            socketClientConnection.sendServerMessage(connectionCompleted);


            if (playersWaitingList.size() > 1) {
                gameHandler.sendExcept(new PlayerJoinedNotification("Player " + virtualClientView.getClientNickname() + " has officially joined the game!"), clientID);
            }
        } else { //client già registrato con quel nickname (quindi ID != null)
            VirtualClientView registeredClient = idMapVirtualClient.get(clientID);
            if (registeredClient.getSocketClientConnection() != null) {
                SerializedAnswer duplicateNicknameError = new SerializedAnswer();
                duplicateNicknameError.setServerAnswer(new ServerError(ServerErrorTypes.DUPLICATENICKNAME));
                socketClientConnection.sendServerMessage(duplicateNicknameError);
                return null;
            }
        }

        return clientID;
    }




    /**
         * Cut off the connection with a client
         * @param connectionClientServer connection between a client and the server
         */
        public void onClientDisconnection(CSConnection connectionClientServer){
                String nickname = "";  // Mettere qui il nome del player da ricavare dalla connessione.
                clients.remove(nickname);
        /*
        Metodo da modificare perchè deve chiamare il removePlayer definito qui.
         */

        /*
        qui il controller deve verificare in che stato era il player prima e agire di conseguenza.
         */
        }


        /**
         * This method closes all the connections to the server.
         */
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
