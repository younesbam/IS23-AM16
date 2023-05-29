package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.communications.clientmessages.messages.HowManyPlayersResponse;
import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import it.polimi.ingsw.model.cards.PersonalGoalCard;

import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.Const.*;

/**
 * This class is used to check if the action performed by the user is actually feasible and, if so, returns the action to the dispatcher.
 */
public class InputValidator {

    private final CLI cli;
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
     * Check if the maximum number of players written is a number
     * @param s
     * @return
     */
    public HowManyPlayersResponse players(String[] s){
        int numOfPlayers;
        try{
            numOfPlayers = Integer.parseInt(s[1]);
        } catch (NumberFormatException e){
            numOfPlayers = 0;
        }

        return new HowManyPlayersResponse(numOfPlayers);
    }


        /*
        Io qui andrei ad impacchettare direttamente l'input per evitare che il server debba lavorarci ancora. Prende quello che gli è stato inviato e lo manda al controller, come già fa.

        Non andrei ad appesantire ancora con i controlli di MAXDIM ecc... perchè tanto li fa il controller (e se non li fa male!). Se vede che non vanno bene, deve essere il server a restituire il messaggio
        al client "guarda che hai pescato delle tiles sbagliate, scrivi di nuovo cosa vuoi prendere".

        Non mi sembra proprio giusto da qui andare a chiamare nuovamente la cli con "requestTiles" visto che andrebbe a bloccare di nuovo la cli.

        Io proporrei quindi:
        - Il giocatore scrive PICKTILES ONE 1 2 3 4 ecc...
        - Qui impacchetto quello che ha scritto per mandarlo e renderlo subito leggibile al server, pronti via
        - il server manda quello che ha letto al controller (già fa così mi pare).
        - il controller risponde con va bene/non va bene.
        - il server agisce di conseguenza e manda un messaggio al client "ok hai pescato x, y ora dimmi dove vuoi piazzarle" oppure "hai sbagliato coordinate, ripesca".

         Tutto questo senza chiamare input.nextLine da nessuna parte perchè tanto il loop fa già il suo lavoro
         */


    /**
     * Method used to check if the input of the tiles requested makes sense.
     * @param s
     * @return
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
                    System.out.println("Input error, please try again!");
                    return null;
                }
            }
            return new PickTilesAction(coordinates);

        } catch (NumberFormatException e) {
            System.out.println("Input error, please try again!");
            return null;
        }
    }


    /**
     * Method used to check if the coordinates chosen where to place the tiles actually make sense.
     * @param s
     * @return
     */
    public PlaceTilesAction placeTiles(String[] s) {
        List<Tile> tiles = new ArrayList<>();
        int col = 0;

        try {
            // Check the length of the string in order to save the correct parameters
            switch (s.length){
                case 3 -> {
                    tiles.add(Tile.valueOf(s[0]));
                    col = Integer.parseInt(s[1]);
                }
                case 4 -> {
                    tiles.add(Tile.valueOf(s[0]));
                    tiles.add(Tile.valueOf(s[1]));
                    col = Integer.parseInt(s[2]);
                }
                case 5 -> {
                    tiles.add(Tile.valueOf(s[0]));
                    tiles.add(Tile.valueOf(s[1]));
                    tiles.add(Tile.valueOf(s[2]));
                    col = Integer.parseInt(s[3]);
                }
                default -> {
                    System.out.println("Input error, please try again!");
                    return null;
                }
            }
            return new PlaceTilesAction(tiles, col);

        } catch (IllegalArgumentException e){
            System.out.println("Input error, please try again!");
            return null;
        }
    }


    /**
     * Shows user manual. List all the possible commands.
     */
    public void manual(){
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

    /**
     * Quit game command.
     */
    public void exitGame(){
        System.err.println("Disconnected from the server.");
        System.exit(0);
        //cli.disconnectFromServer();
    }
}
