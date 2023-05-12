package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.actions.TilesPicked;
import it.polimi.ingsw.communications.serveranswers.RequestTiles;

import static it.polimi.ingsw.Const.MAXBOARDDIM;

/**
 * This class is used to check if the action performed by the user is actually feasible and, if so, returns the action to the dispatcher.
 */
public class InputChecker {

    private final CLI cli;
    private final ModelView modelView;
    private final Client client;


    /**
     * Class constructor, only for CLI use.
     * @param cli
     * @param modelView
     * @param client
     */
    public InputChecker(CLI cli, ModelView modelView, Client client) {
        this.cli = cli;
        this.modelView = modelView;
        this.client = client;
    }


    public TilesPicked checkTiles(String[] tiles){
        try{
            String numOfTiles = tiles[0];

            GameAction messageToServer = null;

            switch(numOfTiles){
                case "ONE" -> {
                    int row1 = Integer.parseInt(tiles[1]);
                    int col1 = Integer.parseInt(tiles[2]);

                    if(row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0){
                        System.err.println("You selected an invalid row/col, please try again");
                        cli.requestTiles(new RequestTiles().getAnswer());
                    }
                    else
                        messageToServer = new TilesPicked(row1, col1);
                }
                case "TWO" -> {
                    int row1 = Integer.parseInt(tiles[1]);
                    int col1 = Integer.parseInt(tiles[2]);
                    int row2 = Integer.parseInt(tiles[3]);
                    int col2 = Integer.parseInt(tiles[4]);

                    if(row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0 ||
                            row2 > MAXBOARDDIM || col2 > MAXBOARDDIM || row2 < 0 || col2 < 0){
                        System.err.println("You selected an invalid row/col, please try again");
                        cli.requestTiles(new RequestTiles().getAnswer());
                    }
                    else
                        messageToServer = new TilesPicked(row1, col1, row2, col2);
                }
                case "THREE" -> {
                    int row1 = Integer.parseInt(tiles[1]);
                    int col1 = Integer.parseInt(tiles[2]);
                    int row2 = Integer.parseInt(tiles[3]);
                    int col2 = Integer.parseInt(tiles[4]);
                    int row3 = Integer.parseInt(tiles[5]);
                    int col3 = Integer.parseInt(tiles[6]);

                    if(row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0 ||
                            row2 > MAXBOARDDIM || col2 > MAXBOARDDIM || row2 < 0 || col2 < 0 ||
                            row3 > MAXBOARDDIM || col3 > MAXBOARDDIM || row3 < 0 || col3 < 0){
                        System.err.println("You selected an invalid row/col, please try again");
                        cli.requestTiles(new RequestTiles().getAnswer());
                    }
                    else
                        messageToServer = new TilesPicked(row1, col1, row2, col2, row3, col3);
            }
            default -> {
                    System.out.println("Input error, please try again!");
                    return null;
            }
        }
            return (TilesPicked) messageToServer;

        } catch (NumberFormatException e) {
            System.out.println("Input error, please try again!");
            return null;
        }
    }

}
