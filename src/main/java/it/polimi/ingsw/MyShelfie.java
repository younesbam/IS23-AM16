package it.polimi.ingsw;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.server.Server;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This is the main class, which permits the player to start a new game, or to join an already existing one.
 */
public class MyShelfie {
    public static void main(String[] args) {
            System.out.println("Welcome to \n" +
                                " ____    ____             ______   __             __     ___  _         \n" +
                    "|_   \\  /   _|          .' ____ \\ [  |           [  |  .' ..](_)        \n" +
                    "  |   \\/   |    _   __  | (___ \\_| | |--.  .---.  | | _| |_  __  .---.  \n" +
                    "  | |\\  /| |   [ \\ [  ]  _.____`.  | .-. |/ /__\\\\ | |'-| |-'[  |/ /__\\\\ \n" +
                    " _| |_\\/_| |_   \\ '/ /  | \\____) | | | | || \\__., | |  | |   | || \\__., \n" +
                    "|_____||_____|[\\_:  /    \\______.'[___]|__]'.__.'[___][___] [___]'.__.' \n" +
                    "               \\__.'                                                    ");
            System.out.println("Authors: \n" +
                    "Francesca Rosa Diz \nNicolò Gandini \nTommaso Baroni \nYounes Bamhaoud \n");
            System.out.println("Select one of the following:");
            System.out.println("0. SERVER\n1. CLI CLIENT\n2. GUI CLIENT");
            System.out.print(">");
            Scanner scanner = new Scanner(System.in);
            int choice = 0;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.err.println("A number between 0, 1 and 2 is required!\nShutting down...");
                System.exit(-1);
            }
            switch (choice) {
                case 0 -> Server.main(null);
                case 1 -> CLI.main(null);
                case 2 -> {
                    System.out.println("Great choice! MyShelfie is now starting, have fun!");
                    GUI.main(null);
                }
                default -> System.err.println("Please choose a number between 0, 1 and 2!");
            }
        }

}
