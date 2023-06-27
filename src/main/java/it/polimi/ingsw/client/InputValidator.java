package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.communications.clientmessages.messages.CreateGameMessage;
import it.polimi.ingsw.communications.clientmessages.messages.HowManyPlayersResponse;
import it.polimi.ingsw.communications.clientmessages.messages.JoinGameMessage;
import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.Const.*;

/**
 * Check if the action performed by the user is actually feasible and, if so, returns the action to the dispatcher.
 */
public class InputValidator {

    private CLI cli;
    private GUI gui;
    private final ModelView modelView;
    private final Client client;


    /**
     * Class constructor, only for CLI use.
     * @param cli
     * @param modelView
     * @param client
     */
    public InputValidator(CLI cli, ModelView modelView, Client client) {
        this.cli = cli;
        this.modelView = modelView;
        this.client = client;
    }

    /**
     * Class constructor, only for GUI use.
     * @param gui
     * @param modelView
     * @param client
     */
    public InputValidator(GUI gui, ModelView modelView, Client client) {
        this.gui = gui;
        this.modelView = modelView;
        this.client = client;
    }


    /**
     * Check if the input for the CreateGameMessage is valid.
     * @param s
     * @return
     */
    public CreateGameMessage createGame(String[] s){
        String nameOfTheGame;
        if(s.length > 2){
            System.out.print(CLI_INPUT_ERROR);
            return null;
        }
        else {
            nameOfTheGame = s[1];
            return new CreateGameMessage(nameOfTheGame);
        }
    }


    /**
     * Check if the input for the JoinGameMessage is valid.
     * @param s
     * @return
     */
    public JoinGameMessage joinGame(String[] s){
        String nameOfTheGame;
        if(s.length > 2){
            System.out.print(CLI_INPUT_ERROR);
            return null;
        }
        else {
            nameOfTheGame = s[1];
            return new JoinGameMessage(nameOfTheGame);
        }
    }


    /**
     * Check if the maximum number of players written is a number
     * @param s String written by the user
     * @return message ready to be sent to the server.
     */
    public HowManyPlayersResponse players(String[] s){
        int numOfPlayers;
        try{
            numOfPlayers = Integer.parseInt(s[1]);
        } catch (NumberFormatException e){
            numOfPlayers = 0;
        } catch (ArrayIndexOutOfBoundsException e){  // Invalid parameters in input
            System.out.print(CLI_INPUT_ERROR);
            return null;
        }

        return new HowManyPlayersResponse(numOfPlayers);
    }


    /**
     * Check if the input of the tiles requested makes sense. Wrap the coordinates written by the user into a class, ready to be sent.
     * @param s String written by the user
     * @return message ready to be sent to the server.
     */
    public PickTilesAction pickTiles(String[] s){
        int row, col;
        List<Coordinate> coordinates = new ArrayList<>();
        try{
            String numOfTiles = s[1];

            switch(numOfTiles.toUpperCase()){
                case "ONE" -> {
                    // Fist tile's coordinates
                    row = Integer.parseInt(s[2]);
                    col = Integer.parseInt(s[3]);
                    coordinates.add(new Coordinate(row, col));
                }
                case "TWO" -> {
                    // Fist tile's coordinates
                    row = Integer.parseInt(s[2]);
                    col = Integer.parseInt(s[3]);
                    coordinates.add(new Coordinate(row, col));
                    // Second tile's coordinates
                    row = Integer.parseInt(s[4]);
                    col = Integer.parseInt(s[5]);
                    coordinates.add(new Coordinate(row, col));
                }
                case "THREE" -> {
                    // Fist tile's coordinates
                    row = Integer.parseInt(s[2]);
                    col = Integer.parseInt(s[3]);
                    coordinates.add(new Coordinate(row, col));
                    // Second tile's coordinates
                    row = Integer.parseInt(s[4]);
                    col = Integer.parseInt(s[5]);
                    coordinates.add(new Coordinate(row, col));
                    // Third tile's coordinates
                    row = Integer.parseInt(s[6]);
                    col = Integer.parseInt(s[7]);
                    coordinates.add(new Coordinate(row, col));
                }
                default -> {
                    System.out.print(CLI_INPUT_ERROR);
                    return null;
                }
            }
            return new PickTilesAction(coordinates);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.print(CLI_INPUT_ERROR);
            return null;
        }
    }


    /**
     * Check if the coordinates chosen where to place the tiles actually make sense.
     * @param s String written by the user
     * @return message ready to be sent to the server.
     */
    public PlaceTilesAction placeTiles(String[] s) {
        List<Tile> tiles = new ArrayList<>();
        int col = 0;

        try {
            // Check the length of the string in order to save the correct parameters
            switch (s.length){
                case 3 -> {
                    tiles.add(Tile.valueOf(s[1].toUpperCase()));
                    col = Integer.parseInt(s[2]);
                }
                case 4 -> {
                    tiles.add(Tile.valueOf(s[1].toUpperCase()));
                    tiles.add(Tile.valueOf(s[2].toUpperCase()));
                    col = Integer.parseInt(s[3]);
                }
                case 5 -> {
                    tiles.add(Tile.valueOf(s[1].toUpperCase()));
                    tiles.add(Tile.valueOf(s[2].toUpperCase()));
                    tiles.add(Tile.valueOf(s[3].toUpperCase()));
                    col = Integer.parseInt(s[4]);
                }
                default -> {
                    System.out.print(CLI_INPUT_ERROR);
                    return null;
                }
            }
            return new PlaceTilesAction(tiles, col);

        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e){
            System.out.print(CLI_INPUT_ERROR);
            return null;
        }
    }


    /**
     * Shows user manual. List all the possible commands.
     */
    public void printManual(){
        String man = """
                Here all the possible commands:
                - PLAYERS <num_of_players> : use this command when the server asks for the number of players you want to play with. Numeric format.
                - PICKTILES <num_of_tiles> <row1> <col1> <row2> <col2> <row3> <col3>: use this command in order to pick tiles from the board.
                  <num_of_tiles> : number of tiles you want to pick. Possible options are ONE, TWO, THREE.
                  <row> <col> : row and column of the board, based on how many tiles you want to pick. Numeric format.
                  Note that "<row2> <col2> <row3> <col3>" are optional inputs.
                - PLACETILES <Tile1> <Tile2> <Tile3> <column>: use this command in order to place the tiles in your bookshelf.
                  <Tile> : Possible options are YELLOW, BLUE, LIGHTBLUE, GREEN, WHITE, PINK.
                  <column> : column where you want to place the tiles. Numeric format.
                  Note that "<Tile2> <Tile3>" are optional inputs.
                - PRINTCARDS : print both common and personal goal cards.
                - EXIT : in order to close the game.
                - MAN : here we go again.
                """;
        System.out.println(BLUE_BOLD_COLOR + man + RESET_COLOR);
        System.out.print(">");
    }
}
