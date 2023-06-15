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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.polimi.ingsw.Const.MAXBOARDDIM;
import static it.polimi.ingsw.Const.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.controller.Phase.SETUP;

public class Controller implements PropertyChangeListener {
    private final Game game;
    private final GameHandler gameHandler;
    private Player currentPlayer;
    private final List<Tile> pickedTiles = new ArrayList<>();
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
        Tile removedTile;
        for(Coordinate c : coordinates){
            removedTile = game.getBoard().removeTile(c.getRow(), c.getCol());
            pickedTiles.add(removedTile);
            gameHandler.sendToPlayer(new CustomAnswer(false, "\nYou picked the following tile:" + removedTile.name()), currentPlayer.getID());
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
    private void pickTilesAction(PickTilesAction action) {
        List<Coordinate> coordinates = action.getCoordinates();
        boolean canPick;

        // Check if the phase is correct.
        if(!(phase == Phase.TILESPICKING)) {
            gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());
            return;
        }

        // Check if at least one column in bookshelf has enough free space
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
            gameHandler.sendToPlayer(new ErrorAnswer("You don't have enough free spaces to place these tiles!. Choose less tiles.", ErrorClassification.NOT_ENOUGH_SPACE), currentPlayer.getID());
            return;
        }

        // Check if the tiles are pickable (so if it has at least one free side and at least one occupied side).
        // Check also the validity of the parameters
        for(Coordinate c : coordinates){
            try{
                canPick = c.getRow()>=0 && c.getCol()>=0 && game.getBoard().isPickable(c.getRow(), c.getCol());
            } catch (InvalidParameterException e){
                gameHandler.sendToPlayer(new ErrorAnswer("You selected an invalid row/col, please try again", ErrorClassification.INVALID_ROW_COL), currentPlayer.getID());
                return;
            }

            if(!canPick){
                gameHandler.sendToPlayer(new ErrorAnswer("You cannot pick one of the selected tiles. Please choose other tiles.", ErrorClassification.TILES_NOT_PICKABLE), currentPlayer.getID());
                return;
            }
        }

        // Check if the tiles are in a straight row/column
        boolean straightRow, straightCol;
        straightRow = coordinates.stream()
                .allMatch(c -> c.getRow() == coordinates.get(0).getRow());
        straightCol = coordinates.stream()
                .allMatch(c -> c.getCol() == coordinates.get(0).getCol());
        // If the tiles are not straight in a row and column, return error
        if(!straightRow && !straightCol){
            gameHandler.sendToPlayer(new ErrorAnswer("You have to select tiles that lie on the board in a straight line!", ErrorClassification.TILES_NOT_STRAIGHT), currentPlayer.getID());
            return;
        }

        // Check if the tiles are close together
        List<Integer> values, orderedInts;
        if(straightRow){
            // Sort coordinates (only columns)
            values = coordinates.stream()
                    .map(c -> {
                        return c.getCol();
                    })
                    .sorted()
                    .toList();
        } else {
            // Sort coordinates (only rows)
            values = coordinates.stream()
                    .map(c -> {
                        return c.getRow();
                    })
                    .sorted()
                    .toList();
        }
        // Create a sequence of n, n+1, n+2... ints from the first element of the previous ordered list (that is n) and check if are equals
        orderedInts = IntStream.iterate(values.get(0), i -> i + 1)
                .limit(values.size())
                .boxed()
                .toList();
        if(!values.equals(orderedInts)){
            gameHandler.sendToPlayer(new ErrorAnswer("The tiles have to be adjacent!", ErrorClassification.TILES_NOT_ADJACENT), currentPlayer.getID());
            return;
        }

        // Remove tiles from the board
        removeTilesFromBoard(coordinates);
    }


    /**
     * Method used to place the selected tiles in the current player's Bookshelf. It also checks if the selected column has enough free spaces.
     *
     * @param action it contains the coordinates of the bookshelf on where to place the tiles and tiles also
     */
    private void placeTilesAction(PlaceTilesAction action) {
        List<Tile> tiles = action.getTiles();
        int col = action.getCol();

        // Check if the phase is correct.
        if(!(phase == Phase.TILESPLACING)){
            gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());
            return;
        }

        // Check if the selected tiles correspond with the picked tiles
        if(!tiles.containsAll(pickedTiles) || !pickedTiles.containsAll(tiles)){
            gameHandler.sendToPlayer(new ErrorAnswer("Wrong tiles selected, please try again!", ErrorClassification.WRONG_TILES_SELECTED), currentPlayer.getID());
            return;
        }

        // Check the selected column
        try{
            game.getCurrentPlayer().getBookShelf().checkColumn(col, tiles.toArray().length);
        } catch (InvalidParameterException e) {
            gameHandler.sendToPlayer(new ErrorAnswer("Invalid parameters!", ErrorClassification.INVALID_PARAMETERS), currentPlayer.getID());
            return;
        } catch (NotEmptyColumnException e) {
            gameHandler.sendToPlayer(new ErrorAnswer("Not enough space in this column! Please select another one!", ErrorClassification.FULL_COLUMN), currentPlayer.getID());
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
        gameHandler.sendToPlayer(new UpdatePlayerPoints(game.getCurrentPlayer().getTotalPoints()), currentPlayer.getID());

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
                gameHandler.sendToPlayer(new BookShelfCompleted(), currentPlayer.getID());
                gameHandler.sendToEveryone(new CustomAnswer(false, "\nPlayer " + currentPlayer.getUsername() + " has completed his Bookshelf!\nNow we will go on with turns until we reach the player that started the match! (The one and only with the majestic chair!)\n"));
                gameHandler.sendToEveryoneExcept(new BookShelfCompleted(currentPlayer.getUsername()), currentPlayer.getID());
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

        List<Player> rightPointsOrder;
        String s = "This is the final ranking:\n";
        int i = 1;

        //Players being ordered by descending points.
        rightPointsOrder = game.getActivePlayers().stream().sorted(Comparator.comparingInt(Player::getTotalPoints).reversed()).collect(Collectors.toList());

        //TODO gestire il caso di parità.
        //Ranking creation
        for (Player p : rightPointsOrder){
            gameHandler.sendToPlayer(new CustomAnswer(false, "You have collected " + p.getTotalPoints() + " points! Congratulations!\n"), p.getID());
            gameHandler.sendToPlayer(new PlayerFinalPoints("You have collected " + p.getTotalPoints() + " points! Congratulations!\n"), p.getID());
            s = s + "\n" + i + ". " + p.getUsername() + "    " + p.getTotalPoints();
            i++;
        }

        //Sending the ranking and the final messages to everyone.
        gameHandler.sendToEveryone(new CustomAnswer(false, s));
        gameHandler.sendToEveryone(new Ranking(s));
        gameHandler.sendToEveryone(new CustomAnswer(false, "And the winner is... " + rightPointsOrder.get(0).getUsername() + "!!\nCongratulations!"));
        gameHandler.sendToEveryoneExcept(new CustomAnswer(false, "\nUnfortunately you have not won this game, but better luck next time!"), rightPointsOrder.get(0).getID());
        gameHandler.sendToEveryoneExcept(new PlayerFinalResult("\nUnfortunately you have not won this game, but better luck next time!"), rightPointsOrder.get(0).getID());
        gameHandler.sendToPlayer(new CustomAnswer(false, "\nYou are the undisputed winner! Congratulations again!"), rightPointsOrder.get(0).getID());
        gameHandler.sendToPlayer(new PlayerFinalResult("\nYou are the undisputed winner! Congratulations again!"), rightPointsOrder.get(0).getID());
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