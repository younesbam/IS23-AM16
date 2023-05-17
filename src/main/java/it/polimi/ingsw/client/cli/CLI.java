package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.common.ConnectionType;
import it.polimi.ingsw.communications.serveranswers.RequestTiles;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Level;

public class CLI extends UI implements Runnable{
    /**
     * Input scanner.
     */
    private final Scanner input;



    /**
     * CLI constructor.
     */
    public CLI() {
        this.pcsDispatcher = new PropertyChangeSupport(this);
        this.input = new Scanner(System.in);
        this.modelView = new ModelView(this);
        this.actionHandler = new ActionHandler(this, this.modelView);
        setActiveGame(true);
//        setSetupMode(true);
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
        // TODO: bisogna gestire questo bit di activeGame. Va messo a FALSE da qualcuno che sa quando il gioco è finito
        connect();

        while(isActiveGame()){
            if(!isSetupMode())
                loop();
        }

        /*
        Disconnect from server when the game is ended.
         */
        disconnectFromServer();
    }


    /**
     * Loop the CLI waiting for new ACTION command
     */
    private void loop(){
        System.out.println("SONO NEL LOOOOOOOOPPPPPPP");
        input.reset();
        pcsDispatcher.firePropertyChange("action", null, input.nextLine());
    }


    /**
     * Ask player's username.
     * @return player's username
     */
    private String askUsername(){
        String username = null;
        boolean nameChosen = false;
        while (!nameChosen) {
            System.out.println("Choose your username:");
            System.out.println(">");
            username = input.nextLine();
            System.out.println("You username choice is: " + username);
            System.out.println("Are you happy with your choice? [y/n] ");
            System.out.println(">");

            if (input.nextLine().equalsIgnoreCase("y"))
                nameChosen = true;
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
        ConnectionType connectionType = ConnectionType.RMI;   // askConnectionType();
        String ipAddress = "127.0.0.1";    //askIpAddress();
        int numOfPort = 1098;    //askPort();
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
            Client.LOGGER.log(Level.INFO, "Client successfully connected");
        }catch (RemoteException | NotBoundException e){
            Client.LOGGER.log(Level.SEVERE, "Failed to start client-server connection: ", e);
            System.exit(-1);
        }
    }


    /**
     * Server asks for total number of players
     * @param s
     */
     private void howManyPlayerRequest(String s){
         System.out.println(s);

         updateTurn(true);
         setSetupMode(true);

         pcsDispatcher.firePropertyChange("playerResponse", null, input.nextLine());

         updateTurn(false);
     }


    /**
     * Server tells the client that setup of players has been completed. Now the client can listen for commands from the player.
     * @param s
     */
    private void setupCompleted(String s){
         System.out.println(s);
         setSetupMode(false);
     }


    /**
     * Update turn
     * @param yourTurn
     */
    private void updateTurn(Boolean yourTurn) {
        modelView.setIsYourTurn(yourTurn);
        if(yourTurn)
            System.out.print(">");
    }

    private void askWhatToDo(String request){

        if(tmp == 0){
            modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();
            modelView.getGame().getBoard().printBoard();
        }

        System.out.println(request);

        Scanner in = new Scanner(System.in);
        String action = in.nextLine();


        switch (Integer.parseInt(action)){
            case 1 -> {
                requestTiles(new RequestTiles().getAnswer());
                tmp = 0;
            }
            case 2 -> {
                printPersonalGoalCard();
                tmp = 1;
            }
            case 3 -> {
                printCommonGoalCard();
                tmp = 1;
            }
        }
        //pcs.firePropertyChange("ActionToPerform", null, action);

    }


    private void printPersonalGoalCard(){
        //print the card
    }


    private void printCommonGoalCard(){
        //print the card

    }


    /**
     * Method used to request the tiles to the player.
     */
    public void requestTiles(String request){

        System.out.println(request);

        System.out.println("In order to do it, please write firstly the number of tiles that you want to pick in capital letters, followed by the coordinates of the each tile, just like this: THREE row1 col1 row2 col2 row3 col3");

        Scanner in = new Scanner(System.in);
        String chosenTiles = in.nextLine();
        pcsDispatcher.firePropertyChange("PickTiles", null, chosenTiles);
    }


    public void requestWhereToPlaceTiles(String request){
        System.out.println(request);

        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();

        System.out.println("Please type the coordinates");

    }


    /**
     * Message shown when a game ends.
     */
    public void endGameMessage() {
        System.out.println("Thanks for playing MyShelfie! Shutting down...");
        System.exit(0);
    }


    /**
     * Method used to print a personalized answer sent by the server.
     * @param answer
     */
    private void printAnswer(String answer){
        System.out.println(answer);
    }


    public void propertyChange(PropertyChangeEvent event){
        switch (event.getPropertyName()){
            case "HowManyPlayersRequest" -> howManyPlayerRequest((String) event.getNewValue());
            case "SetupCompleted" -> setupCompleted((String) event.getNewValue());
            case "UpdateTurn" -> updateTurn((Boolean) event.getNewValue());
            case "PersonalizedAnswer" -> printAnswer((String) event.getNewValue());
            case "RequestWhatToDo" -> askWhatToDo((String) event.getNewValue());
            case "RequestTiles" -> requestTiles((String) event.getNewValue());
            case "RequestToPlaceTiles" -> requestWhereToPlaceTiles((String) event.getNewValue());


        }
    }
}
