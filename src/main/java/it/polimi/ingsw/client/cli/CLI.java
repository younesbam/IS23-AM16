package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.common.ConnectionType;
import it.polimi.ingsw.communications.serveranswers.Answer;
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

/**
 * Command line interface. It allows interacting with the game with commands from a client
 */
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


    /**
     * Run the {@link  it.polimi.ingsw.client.cli.CLI#loop() loop} method to read new incoming commands.
     */
    @Override
    public void run() {
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
     * Loop the CLI waiting for new ACTION command.
     */
    private void loop(){
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
        ConnectionType conn = null;
        boolean connChosen = false;
        while (!connChosen){
            System.out.println("Please choose your connection type.\nType SOCKET or RMI to confirm your choice.");
            System.out.print(">");

            try {
                conn = ConnectionType.valueOf(input.nextLine().toUpperCase());
                connChosen = true;
            } catch (IllegalArgumentException e) {
                connChosen = false;
            }
        }

        return conn;
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
        int port = 0;
        boolean portChosen = false;
        while (!portChosen){
            System.out.println("Insert the port number of the server, it should be a number between 1024 and 65565");
            System.out.print(">");

            try {
                port = Integer.parseInt(input.nextLine());
                portChosen = true;
            } catch (NumberFormatException e) {
                portChosen = false;
            }
        }

        return port;
    }


    /**
     * Connect to the server
     */
    private void connect(){
        /*
        Set port, IP address, username.
         */
        ConnectionType connectionType = ConnectionType.SOCKET; // askConnectionType();
        String ipAddress = "localhost";    //askIpAddress();
        int numOfPort = 2345;  //askPort();
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
        }catch (RemoteException | NotBoundException e){
            Client.LOGGER.log(Level.SEVERE, "Failed to start client-server connection: ", e);
            System.exit(-1);
        }
    }


    /**
     * Set unique ID generated by the server.
     * @param a answer from the server.
     */
    private void connectionOutcome(ConnectionOutcome a){
        System.out.println(a.getAnswer());
        client.setID(a.getID());
    }


    /**
     * Server asks for total number of players
     * @param s number of players. String format.
     */
     private void howManyPlayerRequest(String s){
         System.out.println(s);
         System.out.print(">");
         modelView.setIsYourTurn(true);
     }


    /**
     * Update turn in model view. Used to know if the user can write a command or not.
     * @see ModelView#getIsYourTurn()
     * @param yourTurn set if it's your turn or not.
     */
    private void updateTurn(Boolean yourTurn) {
        if(yourTurn){
            System.out.println("\nIt's now your turn!");
        }
        else
            System.out.println("\nWait for your next turn now!");
        modelView.setIsYourTurn(yourTurn);
    }


    /**
     * Prints the messages for the initial phase of a player's turn. It also asks him to place his tiles.
     * @param request message from server.
     */
    private void initialPhaseOfTheTurn(String request){
        modelView.setIsYourTurn(true);

        System.out.println("\nHere is your Bookshelf:\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();

        System.out.println("\nHere is the game board:\n");
        modelView.getGame().getBoard().printBoard();

        System.out.println("\nAnd here your total points earned until now:");
        System.out.println(modelView.getGame().getCurrentPlayer().getTotalPoints());
        
        printManMessage();
        System.out.print(request + "\n>");
    }


    /**
     * This method asks the player where he wants to place his tiles.
     * @param request message from server.
     */
    public void requestWhereToPlaceTiles(String request){

        System.out.println("\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();
        System.out.println("\n");
        printManMessage();

        System.out.println(request);
        System.out.print(">");
    }


    /**
     * This method confirms the correct tiles placing, displaying the new Bookshelf.
     * @param request message from server.
     */
    private void tilesPlaced(String request) {
        System.out.println(request);

        System.out.println("\n\nHere is your new Bookshelf:\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();
    }


    /**
     * Confirms the correct number of players choice.
     * @param request message from server.
     */
    public void playerNumberChosen(String request){
        System.out.println(request);

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
     * Print a custom answer sent by the server.
     * @param answer message from server.
     */
    private void customAnswer(String answer){
        System.out.println(answer);
    }


    /**
     * Print the manual message. Helps the player to know all the valid commands.
     * @see InputValidator#printManual()
     */
    public void printManMessage(){
        System.out.println(BLUE_BOLD_COLOR + "\nType MAN to know all the valid commands\n" + RESET_COLOR);
    }


    /**
     * Handles the errors sent by the server.
     * @param a error answer by the server.
     */
    private void errorAnswer(ErrorAnswer a){
        System.out.println(RED_COLOR + a.getAnswer() + RESET_COLOR);
        switch (a.getError()){
            case LOBBY_NOT_READY, MAX_PLAYERS_REACHED, TAKEN_USERNAME -> endGameMessage();
            default -> System.out.print(">");
        }
    }


    /**
     * Print both common and personal goal cards assigned to the game.
     */
    public void printCards(){
        List<CommonGoalCard> commons = modelView.getGame().getCommonGoalCards();
        PersonalGoalCard personal = modelView.getGame().getCurrentPlayer().getPersonalGoalCard();

        // Print commons.
        for(int i=0; i < commons.size(); i++){
            commons.get(i).printCard();
        }
        System.out.println();

        // Print personal.
        personal.printCard();
    }


    /**
     * {@inheritDoc}
     * <p></p>
     * This method is called by the {@link  ActionHandler#answerManager(Answer) answer manager} after a successfully received message from the server.
     * @param event A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent event){
        switch (event.getPropertyName()){
            case "ConnectionOutcome" -> connectionOutcome((ConnectionOutcome) event.getNewValue());
            case "HowManyPlayersRequest" -> howManyPlayerRequest((String) event.getNewValue());
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
