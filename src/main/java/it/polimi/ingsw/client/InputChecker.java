package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.clientmessages.actions.TilesPicked;
import it.polimi.ingsw.communications.clientmessages.actions.TilesPlaced;
import it.polimi.ingsw.communications.serveranswers.RequestTiles;
import it.polimi.ingsw.communications.serveranswers.RequestWhereToPlaceTiles;

import static it.polimi.ingsw.Const.*;

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


    /**
     * Method used to check if the input of the tiles requested makes sense.
     * @param tiles
     * @return
     */
    public TilesPicked checkTilesPicked(String[] tiles){
        try{

            //TODO da aggiungere un check per vedere se il numero di tiles prese possono essere posizionate in almeno una colonna. Se ad esempio un giocatore ha solo uno spazio libero nella sua bookshelf, non potrà ovviamente pescare 3 tiles, ed è da gestire questa cosa.
            String numOfTiles = tiles[0];

            GameAction messageToServer = null;

            switch(numOfTiles){
                case "ONE" -> {
                    int row1 = Integer.parseInt(tiles[1]) - 1;
                    int col1 = Integer.parseInt(tiles[2]) - 1;


                    if(row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0){
                        System.out.println("You selected an invalid row/col, please try again");
                        cli.requestTiles(new RequestTiles().getAnswer());
                    }
                    else
                        messageToServer = new TilesPicked(row1, col1);
                }
                case "TWO" -> {
                    int row1 = Integer.parseInt(tiles[1]) - 1;
                    int col1 = Integer.parseInt(tiles[2]) - 1;
                    int row2 = Integer.parseInt(tiles[3]) - 1;
                    int col2 = Integer.parseInt(tiles[4]) - 1;

                    int diffRows = Math.abs(row1 - row2);
                    int diffCol = Math.abs(col1 - col2);


                    if(row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0 ||
                            row2 > MAXBOARDDIM || col2 > MAXBOARDDIM || row2 < 0 || col2 < 0){
                        System.out.println("You selected an invalid row/col, please try again");
                        cli.requestTiles(new RequestTiles().getAnswer());
                    }
                    else if(col1 != col2 && row1 != row2){
                        System.out.println("You have to select tiles that lie on the board in a straight line!");
                        cli.requestTiles(new RequestTiles().getAnswer());
                    }
                    else if(!((diffRows == 1 && diffCol == 0) || (diffRows == 0 && diffCol == 1))) {
                        System.out.println("The tiles have to be adjacent!");
                        cli.requestTiles(new RequestTiles().getAnswer());
                    }
                    else
                        messageToServer = new TilesPicked(row1, col1, row2, col2);
                }
                case "THREE" -> {
                    int row1 = Integer.parseInt(tiles[1]) - 1;
                    int col1 = Integer.parseInt(tiles[2]) - 1;
                    int row2 = Integer.parseInt(tiles[3]) - 1;
                    int col2 = Integer.parseInt(tiles[4]) - 1;
                    int row3 = Integer.parseInt(tiles[5]) - 1;
                    int col3 = Integer.parseInt(tiles[6]) - 1;

                    int diffRows12 = Math.abs(row1 - row2);
                    int diffCol12 = Math.abs(col1 - col2);
                    int diffRows13 = Math.abs(row1 - row3);
                    int diffCol13 = Math.abs(col1 - col3);
                    int diffRows23 = Math.abs(row2 - row3);
                    int diffCol23 = Math.abs(col2 - col3);

                    System.out.println(tiles[1] + tiles[2] + tiles[3] + tiles[4] + tiles[5] + tiles[6]);

                    if(row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0 ||
                            row2 > MAXBOARDDIM || col2 > MAXBOARDDIM || row2 < 0 || col2 < 0 ||
                            row3 > MAXBOARDDIM || col3 > MAXBOARDDIM || row3 < 0 || col3 < 0){
                        System.out.println("You selected an invalid row/col, please try again");
                        cli.requestTiles(new RequestTiles().getAnswer());
                    }
                    else if(col1 != col2 && col2 != col3 && col1 != col3 && row1 != row2 && row2 != row3 && row1 != row3){
                        System.out.println("You have to select tiles that lie on the board in a straight line!");
                        cli.requestTiles(new RequestTiles().getAnswer());
                    }
                    else if(!((((diffRows12 == 1 && diffCol12 == 0) ||  (diffRows12 == 0 && diffCol12 == 1)) && (((diffRows13 == 1 && diffCol13 == 0) ||  (diffRows13 == 0 && diffCol13 == 1)) || ((diffRows23 == 1 && diffCol23 == 0) ||  (diffRows23 == 0 && diffCol23 == 1))))
                            || (((diffRows13 == 1 && diffCol13 == 0) ||  (diffRows13 == 0 && diffCol13 == 1)) && ((diffRows12 == 1 && diffCol12 == 0) ||  (diffRows12 == 0 && diffCol12 == 1)) || ((diffRows23 == 1 && diffCol23 == 0) ||  (diffRows23 == 0 && diffCol23 == 1))))) {
                        System.out.println("The tiles have to be adjacent!");
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

    /**
     * Method used to check if the coordinates chosen where to place the tiles actually make sense.
     * @param coordinates
     * @return
     */
    public TilesPlaced checkTilesPlaced(String[] coordinates) {
        try{

            GameAction messageToServer = null;

            for(int i = 0; i < coordinates.length -1; i++){
                if(!(coordinates[i] instanceof String)){
                    System.out.println("Invalid input!");
                    cli.requestWhereToPlaceTiles(new RequestWhereToPlaceTiles().getAnswer());
                }
            }

            if(coordinates.length > 4){
                System.out.println("Invalid input!");
                cli.requestWhereToPlaceTiles(new RequestWhereToPlaceTiles().getAnswer());
            }

            messageToServer = new TilesPlaced(coordinates);

//            switch (numOfTiles) {
//                case "ONE" -> {
//                    int row1 = Integer.parseInt(coordinates[1]) - 1;
//                    int col1 = Integer.parseInt(coordinates[2]) - 1;
//
//                    if (row1 < 0 || col1 < 0 || row1 > MAXBOOKSHELFROW || col1 > MAXBOOKSHELFCOL) {
//                        System.out.println("The selected coordinates are out of the Bookshelf space! Please select new coordinates!\n");
//                        cli.requestWhereToPlaceTiles(new RequestWhereToPlaceTiles().getAnswer());
//                    } else
//                        messageToServer = new TilesPlaced(row1, col1);
//                }
//                case "TWO" -> {
//                    int row1 = Integer.parseInt(coordinates[1]) - 1;
//                    int col1 = Integer.parseInt(coordinates[2]) - 1;
//                    int row2 = Integer.parseInt(coordinates[3]) - 1;
//                    int col2 = Integer.parseInt(coordinates[4]) - 1;
//
//                    if (row1 < 0 || col1 < 0 || row1 > MAXBOOKSHELFROW || col1 > MAXBOOKSHELFCOL || row2 < 0 || col2 < 0 || row2 > MAXBOOKSHELFROW || col2 > MAXBOOKSHELFCOL) {
//                        System.out.println("The selected coordinates are out of the Bookshelf space! Please select new coordinates!\n");
//                        cli.requestWhereToPlaceTiles(new RequestWhereToPlaceTiles().getAnswer());
//                    } else if (row1 != row2) {
//                        System.out.println("You have to place the tiles on the same column! Please select new coordinates!\n");
//                        cli.requestWhereToPlaceTiles(new RequestWhereToPlaceTiles().getAnswer());
//                    } else
//                        messageToServer = new TilesPlaced(row1, col1, row2, col2);
//                }
//                case "THREE" -> {
//                    int row1 = Integer.parseInt(coordinates[1]) - 1;
//                    int col1 = Integer.parseInt(coordinates[2]) - 1;
//                    int row2 = Integer.parseInt(coordinates[3]) - 1;
//                    int col2 = Integer.parseInt(coordinates[4]) - 1;
//                    int row3 = Integer.parseInt(coordinates[1]) - 1;
//                    int col3 = Integer.parseInt(coordinates[2]) - 1;
//
//
//                    if (row1 < 0 || col1 < 0 || row1 > MAXBOOKSHELFROW || col1 > MAXBOOKSHELFCOL || row2 < 0 || col2 < 0 || row2 > MAXBOOKSHELFROW || col2 > MAXBOOKSHELFCOL || row3 < 0 || col3 < 0 || row3 > MAXBOOKSHELFROW || col3 > MAXBOOKSHELFCOL) {
//                        System.out.println("The selected coordinates are out of the Bookshelf space! Please select new coordinates!\n");
//                        cli.requestWhereToPlaceTiles(new RequestWhereToPlaceTiles().getAnswer());
//                    } else if (row1 != row2 || row1 != row3 || row2 != row3) {
//                        System.out.println("You have to place the tiles on the same column! Please select new coordinates!\n");
//                        cli.requestWhereToPlaceTiles(new RequestWhereToPlaceTiles().getAnswer());
//                    } else
//                        messageToServer = new TilesPlaced(row1, col1, row2, col2, row3, col3);
//                }
//                default -> {
//                    System.out.println("Input error, please try again!");
//                    return null;
//                }
//            }
            return (TilesPlaced) messageToServer;

        } catch (NumberFormatException e) {
            System.out.println("Input error, please try again!");
            return null;
        }
    }


    /**
     * Quit game command.
     */
    public void exitGame(){
        cli.disconnectFromServer();
    }
}
