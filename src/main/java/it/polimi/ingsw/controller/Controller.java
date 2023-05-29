package it.polimi.ingsw.controller;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorClassification;
import it.polimi.ingsw.communications.serveranswers.requests.PickTilesRequest;
import it.polimi.ingsw.communications.serveranswers.requests.PlaceTilesRequest;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import it.polimi.ingsw.server.GameHandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.ingsw.Const.MAXBOARDDIM;
import static it.polimi.ingsw.controller.Phase.SETUP;

public class Controller implements PropertyChangeListener {
    private Game game;
    private final GameHandler gameHandler;
    private Player currentPlayer;
    private ArrayList<Tile> currentTiles = new ArrayList<>();
    private Phase phase;
    private boolean lastTurn = false;
    private int counter = 0;


    /**
     * Class constructor.
     */
    public Controller(GameHandler gameHandler, Game game) {
        this.gameHandler = gameHandler;
        this.game = game;
        this.phase = SETUP;
    }


    /**
     * Game instance getter.
     * @return
     */
    public Game getGame() {
        return game;
    }


    /**
     * Phase getter.
     * @return
     */
    public Phase getPhase(){
        return this.phase;
    }


    /**
     * Phase setter.
     * @param phase
     */
    public void setPhase(Phase phase){
        this.phase = phase;
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
        }

        game.createBoard();
        game.getBoard().updateBoard();
        setCurrentPlayer(game.getCurrentPlayer());
        this.counter = game.getNumOfPlayers() - 1;

        gameHandler.sendToEveryone(new GameReplica(game));

        setPhase(Phase.TILESPICKING);
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

        setPhase(Phase.TILESPLACING);
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


    /**
     * Method that switches the current player to the next one.
     */
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
        if(!lastTurn) {
            if (game.getCurrentPlayer().getBookShelf().checkEndGame()) {
                return true;
            } else
                return false;
        }
        else
            return false;
    }


    /**
     * Method that updates current player's points.
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


    /**
     * Method to check if the tiles selected by the player are actually pickable. If so, they are removed from the board.
     *
     * @param coordinates
     */
    public void pickTilesAction(int[][] coordinates) {

        if(phase == Phase.TILESPICKING) {
            int i;
            boolean canPick = true;

            if (coordinates.length > currentPlayer.getBookShelf().getFreeSpaces()) {
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
            if (canPick) {
                for (i = 0; i < coordinates.length; i++) {
                    if (!game.getBoard().isPickable(coordinates[i][0], coordinates[i][1])) {
                        canPick = false;
                    }
                }
            }
            if (canPick) {
                removeTilesFromBoard(coordinates);
            } else {
                gameHandler.sendToPlayer(new CustomAnswer(false, "You can't pick the tiles here! Please select other tiles!"), currentPlayer.getID());
            }
        }
        else{
            gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());
        }
    }


    /**
     * Method used to place the selected tiles in the current player's Bookshelf. It also checks if the selected column has enough free spaces.
     *
     * @param coordinates it contains the coordinates of the bookshelf on where to place the tiles.
     */
    public void placeTilesAction(String[] coordinates) {
        if (phase == Phase.TILESPLACING) {

            try {

                ArrayList<Tile> rightOrderTiles = new ArrayList<>();
                int column = 0;

                switch (coordinates.length) {
                    case 2 -> {
                        if (!currentTiles.get(0).name().equals(coordinates[0])) {
                            gameHandler.sendToPlayer(new CustomAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
                        } else {
                            rightOrderTiles.add(Tile.valueOf(coordinates[0]));
                            column = Integer.parseInt(coordinates[1]);
                        }
                    }
                    case 3 -> {
                        if (!(((currentTiles.get(0).name().equals(coordinates[0]) || currentTiles.get(1).name().equals(coordinates[0]))) && ((currentTiles.get(0).name().equals(coordinates[1]) || currentTiles.get(1).name().equals(coordinates[1]))))) {
                            gameHandler.sendToPlayer(new CustomAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
                        } else {
                            rightOrderTiles.add(Tile.valueOf(coordinates[0]));
                            rightOrderTiles.add(Tile.valueOf(coordinates[1]));
                            column = Integer.parseInt(coordinates[2]);
                        }
                    }
                    case 4 -> {
                        if (!(((currentTiles.get(0).name().equals(coordinates[0]) || currentTiles.get(1).name().equals(coordinates[0]) || currentTiles.get(2).name().equals(coordinates[0]))) && ((currentTiles.get(0).name().equals(coordinates[1]) || currentTiles.get(1).name().equals(coordinates[1]) || currentTiles.get(2).name().equals(coordinates[1]))) && ((currentTiles.get(0).name().equals(coordinates[2]) || currentTiles.get(1).name().equals(coordinates[2]) || currentTiles.get(2).name().equals(coordinates[2]))))) {
                            gameHandler.sendToPlayer(new CustomAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
                        } else {
                            rightOrderTiles.add(Tile.valueOf(coordinates[0]));
                            rightOrderTiles.add(Tile.valueOf(coordinates[1]));
                            rightOrderTiles.add(Tile.valueOf(coordinates[2]));
                            column = Integer.parseInt(coordinates[3]);
                        }
                    }
                }

                game.getCurrentPlayer().getBookShelf().checkColumn(column, coordinates.length - 1);
                game.getCurrentPlayer().getBookShelf().placeTiles(column, rightOrderTiles);

                gameHandler.sendToEveryone(new GameReplica(game));
                gameHandler.sendToPlayer(new BookShelfFilledWithTiles(), currentPlayer.getID());

                // Check scheme of personal and common cards. Also update points.
                checkScheme();
                gameHandler.sendToPlayer(new CustomAnswer(false, "Total points earned until now: " + game.getCurrentPlayer().getTotalPoints()), currentPlayer.getID());

                game.getBoard().updateBoard();

                //check if a player has completed his bookshelf, otherwise it sets lastTurn to true, in order to start the last turns for the remaining players.
                if (!checkEndGame()) {
                    setPhase(Phase.TILESPICKING);
                    nextPlayer();
                }
                else{
                    if(!lastTurn) {
                        lastTurn = true;
                        counter = counterCalculator();
                        gameHandler.sendToPlayer(new CustomAnswer(false, "\nCongratulations, you have completed your Bookshelf! Now let the remaining players complete their turn in order to complete the round, and than we will reward the winner!\n"), currentPlayer.getID());
                        gameHandler.sendToEveryone(new CustomAnswer(false, "\nPlayer " + currentPlayer.getUsername() + " has completed his Bookshelf!\nNow we will go on with turns until we reach the player that started the match! (The one and only with the majestic chair!)\n"));
                    }
                }

                gameHandler.sendToEveryone(new GameReplica(game));

                //if we're in the last round of turns, we call the right method.
                if(lastTurn)
                    lastTurnHandler();
                else
                    askToPickTiles();

            } catch (InvalidParameterException e) {
                gameHandler.sendToPlayer(new CustomAnswer(false, "Invalid parameters!"), currentPlayer.getID());
            } catch (NotEmptyColumnException e) {
                gameHandler.sendToPlayer(new CustomAnswer(false, "Not enough space in this column! Please select another one!"), currentPlayer.getID());
            }
        }
        else{
            gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());
        }
    }


    /**
     * This method handles the last turns, after a player has completed his bookshelf.
     */
    public void lastTurnHandler(){
        if(counter > 0){
            setPhase(Phase.TILESPICKING);
            nextPlayer();

            counter--;
            askToPickTiles();
        }
        else
            endGame();
    }


    /**
     * Method that computes and returns the right counter, which represents the number of players that still have to take their turn after the first player completed his bookshelf.
     * @return
     */
    public int counterCalculator(){
        int c;

            if(game.getFirstPlayer().getID() > currentPlayer.getID()){
                c = game.getFirstPlayer().getID() - currentPlayer.getID() - 1;
            }
            else if(game.getFirstPlayer().getID() < currentPlayer.getID()){
                c = (game.getNumOfPlayers() - currentPlayer.getID() - 1) + game.getFirstPlayer().getID();
            }
            else
                c = game.getNumOfPlayers() - 1;

        return c;
    }


    /**
     * Check the scheme of both commons and personal goal card. ALso update points of the player.
     */
    private void checkScheme(){
        checkCommonGoal();
        checkPersonalGoal();
        updateTotalPoints();
    }


    /**
     * Check if you're in the right game phase to print the cards.
     */
    public void checkPrintAction(){
        if(phase == Phase.TILESPLACING || phase == Phase.TILESPICKING){
            gameHandler.sendToPlayer(new PrintCardsAnswer(), currentPlayer.getID());
        }
        else
            gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());

    }


    /**
     * Method that terminates the game, crowning the winner.
     */
    public void endGame(){
        //calcolare i punteggi di tutti e assegnare il vincitore!
        setPhase(Phase.ENDGAME);

        List<Player> rightPointsOrder = new ArrayList<>();
        String s = "This is the final ranking:\n";
        int i = 1;

        //Players being ordered by descending points.
        rightPointsOrder = game.getActivePlayers().stream().sorted(Comparator.comparingInt(Player::getTotalPoints).reversed()).collect(Collectors.toList());

        //TODO gestire il caso di parità.
        //Ranking creation
        for (Player p : rightPointsOrder){
            gameHandler.sendToPlayer(new CustomAnswer(false, "You have collected " + p.getTotalPoints() + " points! Congratulations!\n"), p.getID());
            s = s + "\n" + i + ". " + p.getUsername() + "    " + p.getTotalPoints();
            i++;
        }

        //Sending the ranking and the final messages to everyone.
        //TODO POTREMMO METTERE DEI TIMER PER NON FAR ARRIVARE TUTTI I MESSAGGI INSIEME QUA IN MEZZO!
        gameHandler.sendToEveryone(new CustomAnswer(false, s));
        gameHandler.sendToEveryone(new CustomAnswer(false, "And the winner is... " + rightPointsOrder.get(0).getUsername() + "!!\nCongratulations!"));
        gameHandler.sendToEveryoneExcept(new CustomAnswer(false, "\nUnfortunately you have not won this game, but better luck next time!"), rightPointsOrder.get(0).getID());
        gameHandler.sendToPlayer(new CustomAnswer(false, "\nYou are the undisputed winner! Congratulations again!"), rightPointsOrder.get(0).getID());
        gameHandler.sendToEveryone(new CustomAnswer(false, "\nThe game has come to an end."));

        //TODO c'è da chiudere le connessioni e finire gli ultimi messaggi di fine partita, e poi bona.

        //chiusura connessioni.
        gameHandler.sendToEveryone(new DisconnectPlayer());
        for(Player p  : game.getActivePlayers()) {
            gameHandler.getServer().getVirtualPlayerByID(p.getID()).getConnection().disconnect();
        }
    }


    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case "PickTilesAction" -> pickTilesAction((int[][]) evt.getNewValue());
            case "PlaceTilesAction" -> placeTilesAction((String[]) evt.getNewValue());
            case "PrintCardsAction" -> checkPrintAction();
        }

    }
}