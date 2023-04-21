package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.Dispatcher;
import it.polimi.ingsw.client.SocketClass;
import it.polimi.ingsw.exceptions.TakenUsername;

import java.beans.PropertyChangeSupport;
import java.util.Scanner;

public class CLI{

    private final ActionHandler actionHandler;
    private final ModelView modelView;
    private final Scanner input;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private SocketClass socketClass;


    /**
     * This is the main class of the the client using CLI.
     * @param args
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert the IP Address of the server");
        String ipAddress = scanner.nextLine();
        System.out.println("Insert the port number of the server, it should be a number between 1024 and 65565");
        int numOfPOrt = scanner.nextInt();

        CLI cli = new CLI();
        cli.playerUsernameSetup();
    }


    /**
     * This method lets the player choose his username, trying to establish a connection with the server.
     */
    public void playerUsernameSetup() {
        String username = null;
        boolean nameChosen = false;
        while (!nameChosen) {
            do {
                System.out.println("Choose your username:");
                username = input.nextLine();
            } while (username == null);
            System.out.println(">You username choice is: " + username);
            System.out.println(">Are you happy with your choice? [yes/no] ");

            if (input.nextLine().equalsIgnoreCase("yes")) {
                nameChosen = true;
            } else {
                username = null;
            }
        }
        socketClass = new SocketClass();
        modelView.setUsername(username);
        try {
            if(!socketClass.setup(username, modelView, actionHandler)) {
                System.err.println("The entered IP/port doesn't match any active server or the server is not running. Please try again!");
                CLI.main(null);
            }
            System.out.println("Connection established!");
        } catch (TakenUsername e) {
            playerUsernameSetup();
        }
        support.addPropertyChangeListener(new Dispatcher(modelView, socketClass));
    }


}
