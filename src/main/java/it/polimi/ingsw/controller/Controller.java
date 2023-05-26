package it.polimi.ingsw.controller;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.requests.PickTilesRequest;
import it.polimi.ingsw.communications.serveranswers.requests.PlaceTilesRequest;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import it.polimi.ingsw.server.GameHandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.InvalidParameterException;
import java.util.ArrayList;

import static it.polimi.ingsw.Const.MAXBOARDDIM;

public class Controller implements PropertyChangeListener {
    private Game game;
    private final GameHandler gameHandler;
    private Player currentPlayer;
    private ArrayList<Tile> currentTiles = new ArrayList<>();


    /**
     * Class constructor.
     */
    public Controller(GameHandler gameHandler, Game game) {
        this.gameHandler = gameHandler;
        this.game = game;
    }


    /**
     * Game instance getter.
     * @return
     */
    public Game getGame() {
        return game;
    }


    /**
     * Method used to set game parameters for the initial phase of the match.
     */
    public void setup() {

        //select 2 common goal cards
        game.getCommonGoalCards().add(game.getBag().pickCommonGoalCard(game.getNumOfPlayers()));
        game.getCommonGoalCards().add(game.getBag().pickCommonGoalCard(game.getNumOfPlayers()));

        game.getCommonGoalCards().get(0).placePoints(game.getNumOfPlayers());
        game.getCommonGoalCards().get(1).placePoints(game.getNumOfPlayers());


        int i;
        //select a personal goal card for each player
        for (i = 0; i < game.getNumOfPlayers(); i++) {
            game.getPlayers().get(i).setPersonalGoalCard(game.getBag().pickPersonalGoalCard());
            game.getPlayers().get(i).setBookShelf(new BookShelf());
        }

        game.createBoard();
        game.getBoard().updateBoard();
        setCurrentPlayer(game.getCurrentPlayer());

        gameHandler.sendToEveryone(new GameReplica(game));

        askToPickTiles();
        //the game board should be already filled with tiles
    }


    /**
     * Current player getter.
     * @return
     */
    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }


    /**
     * Method used to ask the player what he wants to do at the start of his turn.
     */
    private void askToPickTiles(){
        gameHandler.sendToPlayer(new PickTilesRequest(), currentPlayer.getID());
    }


//    /**
//     * Method that asks the current player which tiles he wants to pick from the board.
//     */
//    public void askTiles(){
//        gameHandler.sendToPlayer(new GameReplica(getGame()), getCurrentPlayer().getID());
//        gameHandler.sendToPlayer(new RequestTiles(), getCurrentPlayer().getID());
//    }


    /**
     * Method used to remove tiles from the player board.
     * @param coordinates
     */
    public void removeTilesFromBoard(int[][] coordinates){
        //ArrayList<Tile> tiles = new ArrayList<>();

        for (int i = 0;  i < coordinates.length; i++) {
            //tiles.add(game.getBoard().getTile(coordinates[i][0], coordinates[i][1]));
            currentTiles.add(game.getBoard().getTile(coordinates[i][0], coordinates[i][1]));
            gameHandler.sendToPlayer(new CustomAnswer(false, "\nYou picked the following tile:" + game.getBoard().getTile(coordinates[i][0], coordinates[i][1]).name()), currentPlayer.getID());
            game.getBoard().removeTile(coordinates[i][0], coordinates[i][1]);
        }

        askToPlaceTiles();
    }


    private void askToPlaceTiles(){
        gameHandler.sendToPlayer(new PlaceTilesRequest(), getCurrentPlayer().getID());
    }


    /**
     * Method called after every turn, it checks if the current player has reached any common goal in this turn.
     */
    private void checkCommonGoal() {
        int points = 0;
        /*
        Iterate all the common cards in the game and check the related scheme.
        If respected, add points to the current player.
        Check also if the player has already earned points from the card. If true, skip control.
         */
        for(int i=0; i<game.getCommonGoalCards().size(); i++){
            CommonGoalCard card = game.getCommonGoalCards().get(i);
            points = currentPlayer.getCommonCardPointsEarned(i);
            if(points <= 0)
                currentPlayer.setCommonCardPointsEarned(i, card.checkScheme(currentPlayer));
        }

//        for (int i = 0; i < 2; i++) {
//            if (game.getCurrentPlayer().getCommonCardPointsEarned()[i] < 0) {
//
//                game.getCommonGoalCards().get(i).checkScheme(game.getCurrentPlayer());
//
//                if (game.getCurrentPlayer().getCommonCardPointsEarned()[i] > 0) {
//                    updateTotalPoints(game.getCurrentPlayer().getCommonCardPointsEarned()[i]);
//                }
//            }
//        }
    }


    /**
     * Method called at the end of the game to calculate the points gained by the personal goal.
     */
    private void checkPersonalGoal() {
        currentPlayer.checkPersonalGoalCardScheme();
//        int numOfPlayers = game.getNumOfPlayers();
//
//        for(int i = 0; i < numOfPlayers; i++){
//            updateTotalPoints(game.getPlayers().get(i).getPersonalGoalCard().checkScheme(player));
//        }
    }


    public void nextPlayer(){
        gameHandler.sendToPlayer(new EndOfYourTurn(), currentPlayer.getID());
        game.nextPlayer();
        this.currentPlayer = game.getCurrentPlayer();
        gameHandler.sendToPlayer(new ItsYourTurn(), currentPlayer.getID());
        currentTiles.clear();
    }


    /**
     * Method that after every player's turn checks if he has completely filled its Bookshelf, and so the game has reached an end.
     * @return
     */
    public boolean checkEndGame() {
        if (game.getCurrentPlayer().getBookShelf().checkEndGame()) {
            return true;
        }
        else
            return false;
    }


    /**
     * Method that updates current player's points.
     * @param points
     */
    public void updateTotalPoints() {
        game.getCurrentPlayer().updateTotalPoints();
    }


    /**
     * Method used to set the current player.
     * @param player
     */
    public void setCurrentPlayer(Player player) {
        game.setCurrentPlayer(player);
        this.currentPlayer = player;
    }


//    /**
//     * Method that switches the current player to the next one.
//     */
//    public void nextPlayer() {
//        game.nextPlayer();
//    }

    public void endGame() {

    }


    /**
     * Method to check if the tiles selected by the player are actually pickable. If so, they are removed from the board.
     *
     * @param coordinates
     */
    public void pickTilesAction(int[][] coordinates) {

        int i;
        boolean canPick = true;

        if(coordinates.length > currentPlayer.getBookShelf().getFreeSpaces()){
            canPick = false;
            gameHandler.sendToPlayer(new CustomAnswer(false, "You don't have enough spaces in your bookshelf to select these many tiles!"), currentPlayer.getID());
        }


        switch (coordinates.length) {
            case 1 -> {
                int row1 = coordinates[0][0];
                int col1 = coordinates[0][1];

                if (row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0) {
                    canPick = false;
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You selected an invalid row/col, please try again"), currentPlayer.getID());
                }
            }
            case 2 -> {
                int row1 = coordinates[0][0];
                int col1 = coordinates[0][1];
                int row2 = coordinates[1][0];
                int col2 = coordinates[1][1];

                int diffRows = Math.abs(row1 - row2);
                int diffCol = Math.abs(col1 - col2);

                if (row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0 ||
                        row2 > MAXBOARDDIM || col2 > MAXBOARDDIM || row2 < 0 || col2 < 0) {
                    canPick = false;
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You selected an invalid row/col, please try again"), currentPlayer.getID());

                } else if (col1 != col2 && row1 != row2) {
                    canPick = false;
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You have to select tiles that lie on the board in a straight line!"), currentPlayer.getID());

                } else if (!((diffRows == 1 && diffCol == 0) || (diffRows == 0 && diffCol == 1))) {
                    canPick = false;
                    gameHandler.sendToPlayer(new CustomAnswer(false, "The tiles have to be adjacent!"), currentPlayer.getID());
                }
            }
            case 3 -> {
                int row1 = coordinates[0][0];
                int col1 = coordinates[0][1];
                int row2 = coordinates[1][0];
                int col2 = coordinates[1][1];
                int row3 = coordinates[2][0];
                int col3 = coordinates[2][1];

                int diffRows12 = Math.abs(row1 - row2);
                int diffCol12 = Math.abs(col1 - col2);
                int diffRows13 = Math.abs(row1 - row3);
                int diffCol13 = Math.abs(col1 - col3);
                int diffRows23 = Math.abs(row2 - row3);
                int diffCol23 = Math.abs(col2 - col3);

                if (row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0 ||
                        row2 > MAXBOARDDIM || col2 > MAXBOARDDIM || row2 < 0 || col2 < 0 ||
                        row3 > MAXBOARDDIM || col3 > MAXBOARDDIM || row3 < 0 || col3 < 0) {
                    canPick = false;
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You selected an invalid row/col, please try again"), currentPlayer.getID());

                } else if (col1 != col2 && col2 != col3 && col1 != col3 && row1 != row2 && row2 != row3 && row1 != row3) {
                    canPick = false;
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You have to select tiles that lie on the board in a straight line!"), currentPlayer.getID());

                } else if (!((((diffRows12 == 1 && diffCol12 == 0) || (diffRows12 == 0 && diffCol12 == 1)) && (((diffRows13 == 1 && diffCol13 == 0) || (diffRows13 == 0 && diffCol13 == 1)) || ((diffRows23 == 1 && diffCol23 == 0) || (diffRows23 == 0 && diffCol23 == 1))))
                        || (((diffRows13 == 1 && diffCol13 == 0) || (diffRows13 == 0 && diffCol13 == 1)) && ((diffRows12 == 1 && diffCol12 == 0) || (diffRows12 == 0 && diffCol12 == 1)) || ((diffRows23 == 1 && diffCol23 == 0) || (diffRows23 == 0 && diffCol23 == 1))))) {
                    canPick = false;
                    gameHandler.sendToPlayer(new CustomAnswer(false, "The tiles have to be adjacent!"), currentPlayer.getID());
                }
            }
        }
        if(canPick) {
            for (i = 0; i < coordinates.length; i++) {
                if (!game.getBoard().isPickable(coordinates[i][0], coordinates[i][1])) {
                    canPick = false;
                }
            }
        }
        if(canPick){
            removeTilesFromBoard(coordinates);
        }
        else {
            gameHandler.sendToPlayer(new CustomAnswer(false, "You can't pick the tiles here! Please select other tiles!"), currentPlayer.getID());
        }
    }


    /**
     * Method used to place the selected tiles in the current player's Bookshelf. It also checks if the selected column has enough free spaces.
     *
     * @param coordinates it contains the coordinates of the bookshelf on where to place the tiles.
     */
    public void placeTilesAction(String[] coordinates) {
        try{

            ArrayList<Tile> rightOrderTiles = new ArrayList<>();
            int column = 0;

            switch (coordinates.length) {
                case 2 -> {
                    if (!currentTiles.get(0).name().equals(coordinates[0])) {
                        gameHandler.sendToPlayer(new CustomAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
                    }
                    else{
                        rightOrderTiles.add(Tile.valueOf(coordinates[0]));
                        column = Integer.parseInt(coordinates[1]);
                    }
                }
                case 3 -> {
                    if (!(((currentTiles.get(0).name().equals(coordinates[0]) || currentTiles.get(1).name().equals(coordinates[0]))) && ((currentTiles.get(0).name().equals(coordinates[1]) || currentTiles.get(1).name().equals(coordinates[1]))))) {
                        gameHandler.sendToPlayer(new CustomAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
                    }
                    else {
                        rightOrderTiles.add(Tile.valueOf(coordinates[0]));
                        rightOrderTiles.add(Tile.valueOf(coordinates[1]));
                        column = Integer.parseInt(coordinates[2]);
                    }
                }
                case 4 -> {
                    if (!(((currentTiles.get(0).name().equals(coordinates[0]) || currentTiles.get(0).name().equals(coordinates[0]))) && ((currentTiles.get(1).name().equals(coordinates[1]) || currentTiles.get(1).name().equals(coordinates[1]))) && ((currentTiles.get(2).name().equals(coordinates[2]) || currentTiles.get(2).name().equals(coordinates[2]))))) {
                        gameHandler.sendToPlayer(new CustomAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
                    }
                    else {
                        rightOrderTiles.add(Tile.valueOf(coordinates[0]));
                        rightOrderTiles.add(Tile.valueOf(coordinates[1]));
                        rightOrderTiles.add(Tile.valueOf(coordinates[2]));
                        column = Integer.parseInt(coordinates[3]);
                    }
                }
            }

            game.getCurrentPlayer().getBookShelf().checkColumn(column - 1, coordinates.length - 1);
            game.getCurrentPlayer().getBookShelf().placeTiles(column - 1, rightOrderTiles);

            gameHandler.sendToEveryone(new GameReplica(game));
            gameHandler.sendToPlayer(new BookShelfFilledWithTiles(), currentPlayer.getID());

            // Check scheme of personal and common cards. Also update points.
            checkScheme();

            if(!checkEndGame()){
                nextPlayer();
            }
            gameHandler.sendToEveryone(new GameReplica(game));
            askToPickTiles();
        }
        catch (InvalidParameterException e){
            gameHandler.sendToPlayer(new CustomAnswer(false, "Invalid parameters!"), currentPlayer.getID());
        }
        catch (NotEmptyColumnException e){
            gameHandler.sendToPlayer(new CustomAnswer(false, "Not enough space in this column! Please select another one!"), currentPlayer.getID());
        }
    }


    /**
     * Check the scheme of both commons and personal goal card. ALso update points of the player.
     * @param s
     */
    private void checkScheme(){
        checkCommonGoal();
        checkPersonalGoal();
        updateTotalPoints();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case "PickTilesAction" -> pickTilesAction((int[][]) evt.getNewValue());
            case "PlaceTilesAction" -> placeTilesAction((String[]) evt.getNewValue());
        }

    }
}