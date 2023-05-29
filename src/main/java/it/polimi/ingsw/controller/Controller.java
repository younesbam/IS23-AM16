package it.polimi.ingsw.controller;
import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
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
import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.controller.Phase.SETUP;

public class Controller implements PropertyChangeListener {
    private Game game;
    private final GameHandler gameHandler;
    private Player currentPlayer;
    private List<Tile> pickedTiles = new ArrayList<>();
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


        //select a personal goal card for each player
        for (int i = 0; i < game.getNumOfPlayers(); i++) {
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


    /**
     * Method used to remove tiles from the player board.
     * @param coordinates
     */
    private void removeTilesFromBoard(List<Coordinate> coordinates){
        //ArrayList<Tile> tiles = new ArrayList<>();

        for(Coordinate c : coordinates){
            //tiles.add(game.getBoard().getTile(coordinates[i][0], coordinates[i][1]));
            pickedTiles.add(game.getBoard().getTile(c.getRow(), c.getCol()));
            gameHandler.sendToPlayer(new CustomAnswer(false, "\nYou picked the following tile:" + game.getBoard().getTile(c.getRow(), c.getCol()).name()), currentPlayer.getID());
            game.getBoard().removeTile(c.getRow(), c.getCol());
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
    }


    /**
     * Method called at the end of the game to calculate the points gained by the personal goal.
     */
    private void checkPersonalGoal() {
        currentPlayer.checkPersonalGoalCardScheme();
    }


    /**
     * Method that switches the current player to the next one.
     */
    private void nextPlayer(){
        gameHandler.sendToPlayer(new EndOfYourTurn(), currentPlayer.getID());
        game.nextPlayer();
        this.currentPlayer = game.getCurrentPlayer();
        gameHandler.sendToPlayer(new ItsYourTurn(), currentPlayer.getID());
        pickedTiles.clear();
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
     * @param action list of coordinates where to pick tiles from the board
     */
    public void pickTilesAction(PickTilesAction action) {
        List<Coordinate> coordinates = action.getCoordinates();
        boolean canPick;

        // Check if the phase is correct.
        if(!(phase == Phase.TILESPICKING)) {
            gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());
            return;
        }

        // Check if at least one column has enough free space
        canPick = false;
        for(int i=0; i<MAXBOOKSHELFCOL; i++){
            try{
                currentPlayer.getBookShelf().checkColumn(i, coordinates.toArray().length);
                canPick = true;
                break;
            }catch (NotEmptyColumnException e){
               // do nothing
            }
        }
        if(!canPick){
            gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());
            return;
        }

        // Check if the tiles are pickable.
        for(Coordinate c : coordinates){
            canPick = game.getBoard().isPickable(c.getCol(), c.getRow());
            if(!canPick){
                gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());
                return;
            }
        }

        // Check the coordinates sent from client
        switch (coordinates.toArray().length) {
            case 1 -> {
                int row1 = coordinates.get(0).getRow();
                int col1 = coordinates.get(0).getCol();

                if (row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0) {
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You selected an invalid row/col, please try again"), currentPlayer.getID());
                    return;
                }
            }
            case 2 -> {
                int row1 = coordinates.get(0).getRow();
                int col1 = coordinates.get(0).getCol();
                int row2 = coordinates.get(1).getRow();
                int col2 = coordinates.get(1).getCol();

                int diffRows = Math.abs(row1 - row2);
                int diffCol = Math.abs(col1 - col2);

                if (row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0 ||
                        row2 > MAXBOARDDIM || col2 > MAXBOARDDIM || row2 < 0 || col2 < 0) {
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You selected an invalid row/col, please try again"), currentPlayer.getID());
                    return;

                } else if (col1 != col2 && row1 != row2) {
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You have to select tiles that lie on the board in a straight line!"), currentPlayer.getID());
                    return;

                } else if (!((diffRows == 1 && diffCol == 0) || (diffRows == 0 && diffCol == 1))) {
                    gameHandler.sendToPlayer(new CustomAnswer(false, "The tiles have to be adjacent!"), currentPlayer.getID());
                    return;
                }
            }
            case 3 -> {
                int row1 = coordinates.get(0).getRow();
                int col1 = coordinates.get(0).getCol();
                int row2 = coordinates.get(1).getRow();
                int col2 = coordinates.get(1).getCol();
                int row3 = coordinates.get(2).getRow();
                int col3 = coordinates.get(2).getCol();

                int diffRows12 = Math.abs(row1 - row2);
                int diffCol12 = Math.abs(col1 - col2);
                int diffRows13 = Math.abs(row1 - row3);
                int diffCol13 = Math.abs(col1 - col3);
                int diffRows23 = Math.abs(row2 - row3);
                int diffCol23 = Math.abs(col2 - col3);

                if (row1 > MAXBOARDDIM || col1 > MAXBOARDDIM || row1 < 0 || col1 < 0 ||
                        row2 > MAXBOARDDIM || col2 > MAXBOARDDIM || row2 < 0 || col2 < 0 ||
                        row3 > MAXBOARDDIM || col3 > MAXBOARDDIM || row3 < 0 || col3 < 0) {
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You selected an invalid row/col, please try again"), currentPlayer.getID());
                    return;

                } else if (col1 != col2 && col2 != col3 && col1 != col3 && row1 != row2 && row2 != row3 && row1 != row3) {
                    gameHandler.sendToPlayer(new CustomAnswer(false, "You have to select tiles that lie on the board in a straight line!"), currentPlayer.getID());
                    return;

                } else if (!((((diffRows12 == 1 && diffCol12 == 0) || (diffRows12 == 0 && diffCol12 == 1)) && (((diffRows13 == 1 && diffCol13 == 0) || (diffRows13 == 0 && diffCol13 == 1)) || ((diffRows23 == 1 && diffCol23 == 0) || (diffRows23 == 0 && diffCol23 == 1))))
                        || (((diffRows13 == 1 && diffCol13 == 0) || (diffRows13 == 0 && diffCol13 == 1)) && ((diffRows12 == 1 && diffCol12 == 0) || (diffRows12 == 0 && diffCol12 == 1)) || ((diffRows23 == 1 && diffCol23 == 0) || (diffRows23 == 0 && diffCol23 == 1))))) {
                    gameHandler.sendToPlayer(new CustomAnswer(false, "The tiles have to be adjacent!"), currentPlayer.getID());
                    return;
                }
            }
        }

        // Remove tiles from the board
        removeTilesFromBoard(coordinates);
    }


    /**
     * Method used to place the selected tiles in the current player's Bookshelf. It also checks if the selected column has enough free spaces.
     *
     * @param action it contains the coordinates of the bookshelf on where to place the tiles and tiles also
     */
    public void placeTilesAction(PlaceTilesAction action) {
        List<Tile> tiles = action.getTiles();
        int col = action.getCol();

        // Check if the phase is correct.
        if(!(phase == Phase.TILESPLACING)){
            gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());
            return;
        }

        // Check if the selected tiles correspond with the picked tiles
        if(!tiles.containsAll(pickedTiles) || !pickedTiles.containsAll(tiles)){
            gameHandler.sendToPlayer(new CustomAnswer(false, "Wrong tiles selected, please try again!"), currentPlayer.getID());
            return;
        }

        // Check the selected column
        try{
            game.getCurrentPlayer().getBookShelf().checkColumn(col, tiles.toArray().length);
        } catch (InvalidParameterException e) {
            gameHandler.sendToPlayer(new CustomAnswer(false, "Invalid parameters!"), currentPlayer.getID());
            return;
        } catch (NotEmptyColumnException e) {
            gameHandler.sendToPlayer(new CustomAnswer(false, "Not enough space in this column! Please select another one!"), currentPlayer.getID());
            return;
        }

        // Place tiles in bookshelf
        game.getCurrentPlayer().getBookShelf().placeTiles(col, tiles);

        // Notify the player
        gameHandler.sendToEveryone(new GameReplica(game));
        gameHandler.sendToPlayer(new BookShelfFilledWithTiles(), currentPlayer.getID());

        // Check scheme of personal and common cards. Also update points.
        checkScheme();
        gameHandler.sendToPlayer(new CustomAnswer(false, "Total points earned until now: " + game.getCurrentPlayer().getTotalPoints()), currentPlayer.getID());

        // Update game board
        game.getBoard().updateBoard();

        // Check if a player has completed his bookshelf, otherwise it sets lastTurn to true, in order to start the last turns for the remaining players.
        if (!checkEndGame()) {
            setPhase(Phase.TILESPICKING);
            nextPlayer();
        } else{
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
            case "PickTilesAction" -> pickTilesAction((PickTilesAction) evt.getNewValue());
            case "PlaceTilesAction" -> placeTilesAction((PlaceTilesAction) evt.getNewValue());
            case "PrintCardsAction" -> checkPrintAction();
        }

    }
}