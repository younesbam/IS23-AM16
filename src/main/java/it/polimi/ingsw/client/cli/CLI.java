package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.common.ConnectionType;
import it.polimi.ingsw.communications.serveranswers.PrintCardsAnswer;
import it.polimi.ingsw.communications.serveranswers.info.ConnectionOutcome;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import it.polimi.ingsw.model.cards.PersonalGoalCard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import static it.polimi.ingsw.Const.*;

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
        //System.out.println("SONO NEL LOOOOOOOOPPPPPPP");
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
            System.out.print(">");
            username = input.nextLine();
            System.out.println("You username choice is: " + username);
            System.out.println("Are you happy with your choice? [y/n] ");
            System.out.print(">");

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
        System.out.print(">");
        return ConnectionType.valueOf(input.nextLine().toUpperCase());
    }


    /**
     * Ask the server IP address.
     * @return selected IP address.
     */
    private String askIpAddress(){
        System.out.println("Insert the IP Address of the server");
        System.out.print(">");
        return input.nextLine();
    }


    /**
     * Ask the server port.
     * @return selected port
     */
    private int askPort(){
        System.out.println("Insert the port number of the server, it should be a number between 1024 and 65565");
        System.out.print(">");
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
            //Client.LOGGER.log(Level.INFO, "Client successfully connected");
        }catch (RemoteException | NotBoundException e){
            Client.LOGGER.log(Level.SEVERE, "Failed to start client-server connection: ", e);
            System.exit(-1);
        }
    }


    /**
     * Set unique ID generated by the server.
     * @param a
     */
    private void connectionOutcome(ConnectionOutcome a){
        System.out.println(a.getAnswer());
        client.setID(a.getID());
    }


    /**
     * Server asks for total number of players
     * @param s
     */
     private void howManyPlayerRequest(String s){
         System.out.println(s);
         modelView.setIsYourTurn(true);
         // TODO: qui viene settato a true ma non viene mai messo a FALSE. Bisogna gestire questo, per evitare che il client mandi messaggi al server
     }


    /**
     * Update turn
     * @param yourTurn
     */
    private void updateTurn(Boolean yourTurn) {
        if(yourTurn){
            System.out.println("\nIt's now your turn!");
        }
        else
            System.out.println("\nWait for your next turn now!");
        modelView.setIsYourTurn(yourTurn);

        if(yourTurn)
            System.out.print(">");
    }


    /**
     * Method that prints the messages for the initial phase of a player's turn. It also asks him to place his tiles.
     * @param request
     */
    private void initialPhaseOfTheTurn(String request){
        modelView.setIsYourTurn(true);

        System.out.println("\n\nHere is your Bookshelf:\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();

        System.out.println("\n\nAnd here is the game board:\n");

        modelView.getGame().getBoard().printBoard();
        System.out.println("\n");

        printMAN();
        System.out.print(request + "\n>");
    }


    /**
     * This method asks the player where he wants to place his tiles.
     * @param request
     */
    public void requestWhereToPlaceTiles(String request){

        System.out.println("\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();
        System.out.println("\n");
        printMAN();

        System.out.println(request + "\n");
        System.out.println(">");
    }


    /**
     * This method confirms the correct tiles placing, displaying the new Bookshelf.
     * @param string
     */
    private void tilesPlaced(String string) {
        System.out.println(string);

        System.out.println("\n\nHere is your new Bookshelf:\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();
    }


    /**
     * This method confirms the correct number of players choice.
     * @param s
     */
    public void playerNumberChosen(String s){
        System.out.println(s);

        updateTurn(false);
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
    private void customAnswer(String answer){
        System.out.println(answer);
    }


    /**
     * Method used to print the MAN.
     */
    public void printMAN(){
        System.out.println(BLUE_BOLD_COLOR + "\nType MAN to know all the valid commands\n" + RESET_COLOR);
    }


    /**
     * Errors from server.
     * @param a
     */
    private void errorAnswer(ErrorAnswer a){
        System.out.println(RED_COLOR + a.getAnswer() + RESET_COLOR);
        switch (a.getError()){
            case LOBBY_NOT_READY, MAX_PLAYERS_REACHED, TAKEN_USERNAME -> endGameMessage();
            //case INCORRECT_PHASE -> incorrectPhaseMessage((String) a.getAnswer());
            default -> System.out.println("\n>");
        }
    }


    /**
     * Print both common and personal goal cards.
     */
    public void printCards(){
        List<CommonGoalCard> commons = modelView.getGame().getCommonGoalCards();
        PersonalGoalCard personal = modelView.getGame().getCurrentPlayer().getPersonalGoalCard();

        // Print commons.
        for(int i=0; i < commons.size(); i++){
            commons.get(i).printCard();
        }
        System.out.println("");

        // Print personal.
        personal.printCard();
    }


    private void incorrectPhaseMessage(String message) {
        System.out.println(message);
    }



    public void propertyChange(PropertyChangeEvent event){
        switch (event.getPropertyName()){
            case "ConnectionOutcome" -> connectionOutcome((ConnectionOutcome) event.getNewValue());
            case "HowManyPlayersRequest" -> howManyPlayerRequest((String) event.getNewValue());
            case "UpdateTurn" -> updateTurn((Boolean) event.getNewValue());
            case "CustomAnswer" -> customAnswer((String) event.getNewValue());
            case "PickTilesRequest" -> initialPhaseOfTheTurn((String) event.getNewValue());
            case "RequestToPlaceTiles" -> requestWhereToPlaceTiles((String) event.getNewValue());
            case "BookShelfFilledWithTiles" -> tilesPlaced((String) event.getNewValue());
            case "ItsYourTurn" -> updateTurn(true);
            case "EndOfYourTurn" -> updateTurn(false);
            case "PlayerNumberChosen" -> playerNumberChosen((String) event.getNewValue());
            case "PrintCardsAnswer" -> printCards();

            case "ErrorAnswer" -> errorAnswer((ErrorAnswer) event.getNewValue());

        }
    }

}
