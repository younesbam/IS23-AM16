package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.common.Connection;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.common.ConnectionType;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.Scanner;
import java.util.logging.Level;

public class CLI extends UI implements Runnable implements{
    /**
     * Input scanner.
     */
    private final Scanner input;



    /**
     * CLI constructor.
     */
    public CLI() {
        this.pcs = new PropertyChangeSupport(this);
        this.input = new Scanner(System.in);
        this.modelView = new ModelView(this);
        this.actionHandler = new ActionHandler(this, this.modelView);
    }


    /**
     * This is the main class of the client using CLI.
     * @param args
     */
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.run();
    }


    @Override
    public void run() {
        connect();
        loop();
    }


    /**
     * Ask player's username.
     * @return player's username
     */
    private String askUsername(){
        String username = null;
        boolean nameChosen = false;
        while (!nameChosen) {
            do {
                System.out.println("Choose your username:");
                System.out.println(">");
                username = input.nextLine();
            } while (username == null);
            System.out.println("You username choice is: " + username);
            System.out.println("Are you happy with your choice? [yes/no] ");
            System.out.println(">");

            if (input.nextLine().equalsIgnoreCase("yes")) {
                nameChosen = true;
            } else {
                username = null;
            }
        }
        return username;
    }


    /**
     * Ask the connection type.
     * @return type of connection
     */
    private ConnectionType askConnectionType(){
        System.out.println("Please choose your connection type.\nType SOCKET or RMI to confirm your choice.");
        System.out.println(">");
        return ConnectionType.valueOf(input.nextLine().toUpperCase());
    }


    /**
     * Ask the server IP address.
     * @return selected IP address.
     */
    private String askIpAddress(){
        System.out.println("Insert the IP Address of the server");
        System.out.println(">");
        return input.nextLine();
    }


    /**
     * Ask the server port.
     * @return selected port
     */
    private int askPort(){
        System.out.println("Insert the port number of the server, it should be a number between 1024 and 65565");
        System.out.println(">");
        return input.nextInt();
    }


    /**
     * Connect to the server
     */
    private void connect(){
        /*
        Set port, IP address, username.
         */
        ConnectionType connectionType = askConnectionType();
        String ipAddress = askIpAddress();
        int numOfPort = askPort();
        String username = askUsername();

        /*
        Model view handler
         */
        modelView.setUsername(username);

        /*
        Start connections, based on the user answer (RMI or socket).
         */
        try{
            connectToServer(connectionType, ipAddress, numOfPort, username);
        }catch (Exception e){
            Connection.LOGGER.log(Level.SEVERE, "Failed to start client-server connection: ", e.getMessage());
            System.exit(-1);
        }
    }


    /**
     * Loop the CLI.
     */
    private void loop(){
        input.reset();
        pcs.firePropertyChange("action", null, input.nextLine());
    }


    public void requestTiles(){

        modelView.getGame().getCurrentPlayer().getBookShelf().printBookshelf();
        modelView.getGame().getBoard().printBoard();

        System.out.println("Now please select the tiles you want to pick from the board.\n");

        System.out.println("In order to do it, please write firstly the number of tiles that you want to pick, followed by the coordinates of the tiles, like this: THREE row1 col1 row2 col2 row3 col3");

        Scanner in = new Scanner(System.in);
        String chosenTiles = input.nextLine();
        pcs.firePropertyChange("PickTiles", null, chosenTiles);
    }


    public void propertyChange(PropertyChangeEvent event){
        switch (event.getPropertyName()){
            case "RequestTiles" -> requestTiles();
            case "GameReplica" -> modelView.updateGame();
        }
    }



}
