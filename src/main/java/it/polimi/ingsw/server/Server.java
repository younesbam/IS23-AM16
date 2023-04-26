package it.polimi.ingsw.server;

import it.polimi.ingsw.common.JSONParser;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

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
    private final List<ClientSocketConnection> playersWaitingList = new ArrayList<>();

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
    private final Map<VirtualPlayer, ClientSocketConnection> virtualPlayerToClientSocketConnection;

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
        virtualPlayerToClientSocketConnection = new HashMap<>();

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

        new Thread(new ServerPing(clients)).start();

        Thread thread = new Thread(this::quitConnection);
        thread.start();
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
     */
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
        playersWaitingList.remove(virtualPlayerToClientSocketConnection.get(player));
        IDMapUsername.remove(player.getID());
        virtualPlayerToClientSocketConnection.remove(player);
        System.out.println("The player has been successfully removed from the game.");
    }


    /**
     * Main class.
     * @param args
     */
    public static void main(String[] args) {

        System.out.println(""" __  __       ____  _          _  __ _      
                               |  \/  |_   _/ ___|| |__   ___| |/ _(_) ___ 
                               | |\/| | | | \___ \| '_ \ / _ \ | |_| |/ _ \
                               | |  | | |_| |___) | | | |  __/ |  _| |  __/
                               |_|  |_|\__, |____/|_| |_|\___|_|_| |_|\___|
                                       |___/""");

        System.out.print("Welcome to the server of MyShelfie!");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please type the port number of this Server Socket\n>");

        int numOfPort = 0;
        try {
            numOfPort = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.err.println("A number is required! Shutting down...");
            System.exit(-1);
        }
        if (numOfPort < 0 || (numOfPort > 0 && numOfPort < 1024)) {
            System.err.println("A port number between 1024 and 65535 is required! Please try again.");
            main(null);
        }

        Utils.setPort(port);

        Server server = new Server();
    }


}