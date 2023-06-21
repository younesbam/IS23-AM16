package it.polimi.ingsw.controller;
import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.common.exceptions.*;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorClassification;
import it.polimi.ingsw.communications.serveranswers.info.*;
import it.polimi.ingsw.communications.serveranswers.requests.DisconnectPlayer;
import it.polimi.ingsw.communications.serveranswers.requests.PickTilesRequest;
import it.polimi.ingsw.communications.serveranswers.requests.PlaceTilesRequest;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import it.polimi.ingsw.server.GameHandler;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.connection.CSConnection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.polimi.ingsw.Const.*;
import static it.polimi.ingsw.controller.Phase.SETUP;
import static it.polimi.ingsw.controller.Phase.STANDBY;

public class Controller implements PropertyChangeListener {
    /**
     * Game reference
     */
    private final Game game;

    /**
     * Game handler reference
     */
    private final GameHandler gameHandler;

    /**
     * Current player
     */
    private Player currentPlayer;

    /**
     * Represent picked tiles from the board, and coordinate of the board from which the tiles were taken
     */
    private final Map<Tile, Coordinate> pickedTiles = new LinkedHashMap<>();

    /**
     * Phase of the game.
     */
    private Phase phase;

    /**
     * Last turn
     */
    private boolean lastTurn = false;

    /**
     * Left players that have to complete the turn when a player filled its board
     */
    private int leftPlayers = 0;



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
        this.leftPlayers = game.getNumOfPlayers() - 1;

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
            pickedTiles.put(removedTile, c);
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
        try {
            game.nextPlayer();
            this.currentPlayer = game.getCurrentPlayer();
            gameHandler.sendToPlayer(new ItsYourTurn(), currentPlayer.getID());
            gameHandler.sendToEveryoneExcept(new UpdateTurn(), currentPlayer.getID());
        }catch (NoNextPlayerException e){
            // No players connected
            System.out.println(RED_COLOR + "Not enough players connected. Standby mode activated. I will resume the game when there are at least 2 connected players. zzz..." + RESET_COLOR);
            gameHandler.sendToEveryone(new CustomAnswer(false, "Not enough players connected. Standby mode activated. You cannot play until at least one more player is connected"));
            setPhase(STANDBY);
        }
    }


//    /**
//     * Method that after every player's turn checks if he has completely filled its Bookshelf, and so the game has reached an end.
//     * @return
//     */
//    public boolean checkEndGame() {
//        if(!lastTurn) {
//            if (game.getCurrentPlayer().getBookShelf().checkEndGame()) {
//                return true;
//            } else
//                return false;
//        }
//        else
//            return false;
//    }


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
                    .map(Coordinate::getCol)
                    .sorted()
                    .toList();
        } else {
            // Sort coordinates (only rows)
            values = coordinates.stream()
                    .map(Coordinate::getRow)
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
        List<Tile> pickedTilesList = new ArrayList<>(pickedTiles.keySet());
        if(!tiles.containsAll(pickedTilesList) || !pickedTilesList.containsAll(tiles)){
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

        // End of the turn handler
        endTurn();
    }

    /**
     * Handle the end of the player's turn
     */
    private void endTurn(){
        // Lists flush
        pickedTiles.clear();
        // Update game board
        game.getBoard().updateBoard();
        // Check if a player has completed his bookshelf, otherwise it sets lastTurn to true, in order to start the last turns for the remaining players.
        if(!lastTurn){
            if(!game.getCurrentPlayer().getBookShelf().checkEndGame()){
                setPhase(Phase.TILESPICKING);
                nextPlayer();
            } else {
                lastTurn = true;
                leftPlayers = leftPlayersCalc();
                gameHandler.sendToPlayer(new CustomAnswer(false, "\nCongratulations, you have completed your Bookshelf! Now let the remaining players complete their turn in order to complete the round, and than we will reward the winner!\n"), currentPlayer.getID());
                gameHandler.sendToPlayer(new BookShelfCompleted(), currentPlayer.getID());
                gameHandler.sendToEveryone(new CustomAnswer(false, "\nPlayer " + currentPlayer.getUsername() + " has completed his Bookshelf!\nNow we will go on with turns until we reach the player that started the match! (The one and only with the majestic chair!)\n"));
                gameHandler.sendToEveryoneExcept(new BookShelfCompleted(currentPlayer.getUsername()), currentPlayer.getID());
            }
        }

//        if (!checkEndGame()) {
//            setPhase(Phase.TILESPICKING);
//            nextPlayer();
//        } else{
//            if(!lastTurn) {
//                lastTurn = true;
//                counter = counterCalculator();
//                gameHandler.sendToPlayer(new CustomAnswer(false, "\nCongratulations, you have completed your Bookshelf! Now let the remaining players complete their turn in order to complete the round, and than we will reward the winner!\n"), currentPlayer.getID());
//                gameHandler.sendToEveryone(new CustomAnswer(false, "\nPlayer " + currentPlayer.getUsername() + " has completed his Bookshelf!\nNow we will go on with turns until we reach the player that started the match! (The one and only with the majestic chair!)\n"));
//            }
//        }

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
    private void lastTurnHandler(){
        if(leftPlayers > 0){
            leftPlayers--;
            setPhase(Phase.TILESPICKING);
            nextPlayer();
            askToPickTiles();
        }
        else
            endGame();
    }


    /**
     * Method that computes and returns the right counter, which represents the number of players that still have to take their turn after the first player completed his bookshelf.
     * @return
     */
    private int leftPlayersCalc(){
        if(game.getFirstPlayer().getID() > currentPlayer.getID()){
            return game.getFirstPlayer().getID() - currentPlayer.getID() - 1;
        }
        if(game.getFirstPlayer().getID() < currentPlayer.getID()){
            return  (game.getNumOfPlayers() - currentPlayer.getID() - 1) + game.getFirstPlayer().getID();
        }
        return game.getNumOfPlayers() - 1;
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
    private void checkPrintAction(){
        if(phase == Phase.TILESPLACING || phase == Phase.TILESPICKING){
            gameHandler.sendToPlayer(new PrintCardsAnswer(), currentPlayer.getID());
        }
        else
            gameHandler.sendToPlayer(new ErrorAnswer("You cannot play this command in this game phase!", ErrorClassification.INCORRECT_PHASE), currentPlayer.getID());

    }


    /**
     * Method that terminates the game, crowning the winner.
     */
    private void endGame(){
        //calcolare i punteggi di tutti e assegnare il vincitore!
        setPhase(Phase.ENDGAME);

        List<Player> rightPointsOrder;
        String s = "This is the final ranking:\n";
        int i = 1;

        //Players being ordered by descending points.
        rightPointsOrder = game.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getTotalPoints).reversed())
                .collect(Collectors.toList());

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
        for(Player p  : game.getPlayers()) {
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


    /**
     * Suspend a client after failed ping request
     * @see Server#suspendClient(CSConnection) suspendClient
     * @param ID of the client
     */
    public synchronized void suspendClient(int ID){
        // Check if the players is already suspended
        for(Player p : game.getPlayers())
            if(p.getID() == ID && !p.isActive())
                return;

        // Set player as not active
        game.setActivePlayer(ID, false);
        // Disconnected player is the current player.
        if(currentPlayer.getID() == ID){
            // The player has picked tiles but never placed them
            if(!pickedTiles.isEmpty()){
                try{
                    game.getBoard().restoreTiles(pickedTiles);
                }catch (WrongTilesException | WrongCoordinateException | CellNotEmptyException e){
                    Server.LOGGER.log(Level.SEVERE, "Error during restore tiles on the board. The cell is not empty! Game is shutting down to avoid tiles' loss", e);
                    System.exit(-1);
                }
            }
            // End of the turn
            endTurn();
        }

        gameHandler.sendToEveryone(new CustomAnswer(false, game.getPlayerByID(ID).getUsername() + " is disconnected. Every potential tile picked from the board will be replaced on the board\n" +
                "The game proceeds anyway. Turns of " + game.getPlayerByID(ID).getUsername() + " will be skipped until it connects again"));
    }


    /**
     * Restore a suspended client after the client reconnects to the server
     * @see Server#restoreClient(CSConnection) suspendClient
     * @param ID of the client
     */
    public synchronized void restoreClient(int ID){
        // Set player as not active
        game.setActivePlayer(ID, true);

        // Check if in standby mode and try to switch to the next player
        if(getPhase() == STANDBY){
            gameHandler.sendToEveryone(new CustomAnswer(false, "We are still waiting for another player to restart the game"));
            endTurn();
        }

        gameHandler.sendToEveryone(new CustomAnswer(false, game.getPlayerByID(ID).getUsername() + " reconnects! Now the turns will consider his/her presence"));
    }
}