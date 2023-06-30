package it.polimi.ingsw.controller;
import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.common.exceptions.*;
import it.polimi.ingsw.communications.clientmessages.actions.PickTilesAction;
import it.polimi.ingsw.communications.clientmessages.actions.PlaceTilesAction;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.end.*;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorClassification;
import it.polimi.ingsw.communications.serveranswers.info.*;
import it.polimi.ingsw.communications.serveranswers.requests.PickTilesRequest;
import it.polimi.ingsw.communications.serveranswers.requests.PlaceTilesRequest;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.CommonGoalCard;
import it.polimi.ingsw.server.GameHandler;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.VirtualPlayer;
import it.polimi.ingsw.server.connection.CSConnection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static it.polimi.ingsw.Const.*;
import static it.polimi.ingsw.controller.Phase.SETUP;
import static it.polimi.ingsw.controller.Phase.STANDBY;

/**
 * Main controller class. It handles the game progress.
 */
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
     * @return game instance.
     */
    public Game getGame() {
        return game;
    }


    /**
     * Phase getter.
     * @return current game phase.
     */
    public Phase getPhase(){
        return this.phase;
    }


    /**
     * Phase setter.
     * @param phase phase to be set.
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

        gameHandler.sendToEveryoneExcept(new EndOfYourTurn(), game.getCurrentPlayer().getID());
        setPhase(Phase.TILESPICKING);
        askToPickTiles();
        //the game board should be already filled with tiles
    }


    /**
     * Current player getter.
     * @return current player.
     */
    private Player getCurrentPlayer(){
        return this.currentPlayer;
    }


    /**
     * Ask the player to pick the tiles from the board.
     */
    private void askToPickTiles(){
        gameHandler.sendToPlayer(new PickTilesRequest(), currentPlayer.getID());
    }


    /**
     * Method used to remove tiles from the player board.
     * @param coordinates coordinates list representing rows and columns of where pick the tiles from the board.
     */
    private void removeTilesFromBoard(List<Coordinate> coordinates){
        Tile removedTile;
        for(Coordinate c : coordinates){
            removedTile = game.getBoard().removeTile(c.getRow(), c.getCol());
            pickedTiles.put(removedTile, c);
            gameHandler.sendToPlayer(new CustomAnswer("\nYou picked the following tile:" + removedTile.name()), currentPlayer.getID());
        }

        setPhase(Phase.TILESPLACING);
        askToPlaceTiles();
    }


    /**
     * Ask the player to place the tiles in the bookshelf.
     */
    private void askToPlaceTiles(){
        gameHandler.sendToPlayer(new PlaceTilesRequest(), getCurrentPlayer().getID());
    }


    /**
     * Switches the current player to the next one.
     */
    private void nextPlayer(){
        //gameHandler.sendToPlayer(new EndOfYourTurn(), currentPlayer.getID());
        try {
            game.nextPlayer();
            this.currentPlayer = game.getCurrentPlayer();
            if(phase == STANDBY)
                System.out.println(GREEN_COLOR + "Game successfully restored!" + RESET_COLOR);
            setPhase(Phase.TILESPICKING);
            gameHandler.sendToPlayer(new ItsYourTurn(), currentPlayer.getID());
            gameHandler.sendToEveryoneExcept(new UpdateTurn(), currentPlayer.getID());
        }catch (NoNextPlayerException e){
            // No players connected
            System.out.println(RED_COLOR + "Not enough players connected. Standby mode activated. I will resume the game when there are at least 2 connected players. zzz..." + RESET_COLOR);
            gameHandler.sendToEveryone(new CustomAnswer(RED_COLOR + "Not enough players connected. Standby mode activated. You cannot play until at least one more player is connected" + RESET_COLOR));
            setPhase(STANDBY);
        }
    }


    /**
     * Set the current player.
     * @param player current player
     */
    public void setCurrentPlayer(Player player) {
        game.setCurrentPlayer(player);
        this.currentPlayer = player;
    }


    /**
     * Check if the tiles selected by the player are actually pickable. If so, they are removed from the board.
     * @param action pick tiles request message from the client.
     */
    private void pickTilesAction(PickTilesAction action) throws Exception{
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
                canPick = game.getBoard().isPickable(c.getRow(), c.getCol());
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
     * Place the selected tiles in the current player's Bookshelf. It also checks if the selected column has enough free spaces.
     * @param action place tiles action message from the client.
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
        checkSchemes();
        gameHandler.sendToPlayer(new CustomAnswer("Total points earned until now: " + game.getCurrentPlayer().getTotalPoints()), currentPlayer.getID());
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

        // Check if a player has completed his bookshelf. This check is done only if no-one has already completed his bookshelf (lastTurn == false). If the player has not yet completed it, the method nextPlayer() is called, and the turns go on normally.
        // Otherwise, it sets lastTurn to true, in order to start the last turns for the remaining players.
        if(!lastTurn){
            if(!game.getCurrentPlayer().getBookShelf().checkEndGame()){
                if(phase == STANDBY){
                    gameHandler.sendToEveryone(new CustomAnswer("Trying to restore the game..."));
                    System.out.println("Trying to restore the game...");
                }

                nextPlayer();
            } else {
                lastTurn = true;
                game.getPlayerByID(currentPlayer.getID()).updateNumOfTurns();
                leftPlayers = leftPlayersCalc();
                checkFullBookshelf();  // Add one additional point to the first player that completes the bookshelf.
                updateTotalPoints();
                gameHandler.sendToPlayer(new CustomAnswer("\nCongratulations, you have completed your Bookshelf! Now let the remaining players complete their turn in order to complete the round, and than we will reward the winner!\n"), currentPlayer.getID());
                gameHandler.sendToPlayer(new BookShelfCompleted(), currentPlayer.getID());
                gameHandler.sendToEveryone(new CustomAnswer("\nPlayer " + currentPlayer.getUsername() + " has completed his Bookshelf!\nNow we will go on with turns until we reach the player that started the match! (The one and only with the majestic chair!)\n"));
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
     * Check the scheme of both commons and personal goal card. Also update player's points.
     */
    private void checkSchemes(){
        checkCommonGoal();
        checkPersonalGoal();
        checkAdjacentTiles();
        updateTotalPoints();
    }

    /**
     * Called after every turn; it checks if the current player has reached any common goal.
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
            points = currentPlayer.getCommonCardPoints(i);
            if(points <= 0)
                currentPlayer.setCommonCardPoints(i, card.checkScheme(currentPlayer));
        }
    }

    /**
     * Called after every turn; it checks if the current player has reached the personal goal.
     */
    private void checkPersonalGoal() {
        currentPlayer.checkPersonalGoalCardScheme();
    }

    /**
     * Check adjacent tiles for additional points.
     */
    private void checkAdjacentTiles(){
        currentPlayer.checkAdjacentTiles();
    }

    /**
     * Check if the bookshelf is full to assign 1 point to the first player that complete the bookshelf.
     */
    private void checkFullBookshelf(){
        currentPlayer.checkFullBookshelf();
    }

    /**
     * Update current player's points.
     */
    public void updateTotalPoints() {
        game.getCurrentPlayer().updateTotalPoints();
    }


    /**
     * Handles the last turns, after a player has completed his bookshelf.
     */
    private void lastTurnHandler(){

        if(leftPlayers > 0){
            leftPlayers--;
            setPhase(Phase.TILESPICKING);
            nextPlayer();
            gameHandler.sendToEveryone(new GameReplica(game));
            askToPickTiles();
        }
        else
            endGame();
    }


    /**
     * Computes and returns the right counter, which represents the number of players that still have to take their turn after the first player completed his bookshelf.
     * @return
     */
    private int leftPlayersCalc(){

        int i;
        int max = 0;

        for(i = 0; i < game.getNumOfPlayers(); i++){
            if(game.getPlayers().get(i).getNumOfTurns() > max){
                max = game.getPlayers().get(i).getNumOfTurns();
            }
        }

        int count = 0;
        for(i = 0; i < game.getNumOfPlayers(); i++){
            if(game.getPlayers().get(i).getNumOfTurns() != max){
                count++;
            }
        }

        return count;
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
     * Terminate the game, crowning the winner.
     */
    private void endGame(){
        setPhase(Phase.ENDGAME);

        // Points calculator.
        List<Player> rightPointsOrder;
        String finalRanking = "This is the final ranking:\n";
        int i = 1;

        //Players being ordered by descending points.
        rightPointsOrder = game.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getTotalPoints).reversed())
                .collect(Collectors.toList());

        //Ranking creation
        for (Player p : rightPointsOrder){
            gameHandler.sendToPlayer(new PlayerFinalPoints("You have collected " + p.getTotalPoints() + " points! Congratulations!\n"), p.getID());
            finalRanking = finalRanking + "\n" + i + ". " + p.getUsername() + "    " + p.getTotalPoints();
            i++;
        }

        // Sending the ranking and the final messages to everyone.
        gameHandler.sendToEveryone(new Ranking(finalRanking));
        gameHandler.sendToEveryone(new CustomAnswer("And the winner is... " + rightPointsOrder.get(0).getUsername() + "!!\nCongratulations!"));
        gameHandler.sendToEveryoneExcept(new PlayerFinalResult("\nUnfortunately you have not won this game, but better luck next time!"), rightPointsOrder.get(0).getID());
        gameHandler.sendToPlayer(new PlayerFinalResult("\nYou are the undisputed winner! Congratulations again!"), rightPointsOrder.get(0).getID());


        // Disconnect all the players.
        List<VirtualPlayer> players = gameHandler.getPlayersConnected();
        for(VirtualPlayer p: players)
            gameHandler.sendToPlayer(new EndGame(), p.getID());


        System.out.println(RED_COLOR + "Game " + gameHandler.getNameOfTheMatch() + " has come to an end!\n" + RESET_COLOR);

        //remove the players from the server list of connected players.
        for(VirtualPlayer p : gameHandler.getPlayersConnected()){
            gameHandler.getServer().removePlayer(p.getID());
        }

        //remove this gameHandler instance from the map of gameHandlers in the Server.
        gameHandler.getServer().removeGameHandler(gameHandler.getNameOfTheMatch());

    }


    /**
     * Suspend a client after failed ping request.
     * @see Server#suspendClient(CSConnection) suspendClient
     * @param ID unique ID of the client
     */
    public synchronized void suspendClient(int ID){
        // Check if the players is already suspended
        for(Player p : game.getPlayers())
            if(p.getID() == ID && !p.isActive())
                return;


        //set the new first player.
        if(game.getFirstPlayer().equals(game.getPlayerByID(ID))){
            int i = game.getPlayers().indexOf(game.getPlayerByID(ID));

            if(game.getPlayers().get(i + 1) != null){
                game.setFirstPlayer(game.getPlayers().get(i + 1));
            }
            else{
                game.setFirstPlayer(game.getPlayers().get(0));
            }
        }

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


        gameHandler.sendToEveryone(new CustomAnswer(game.getPlayerByID(ID).getUsername() + " is disconnected. Every potential tile picked from the board will be replaced on the board\n" +
                "Turns of " + game.getPlayerByID(ID).getUsername() + " will be skipped until it connects again"));
        gameHandler.sendToEveryone(new PlayerDisconnection(game.getPlayerByID(ID).getUsername() + " is disconnected. Every potential tile picked from the board will be replaced on the board\n" +
                "Turns of " + game.getPlayerByID(ID).getUsername() + " will be skipped until it connects again"));
    }


    /**
     * Restore a suspended client after the client reconnects to the server.
     * @see Server#restoreClient(CSConnection) restoreClient
     * @param ID unique ID of the client
     */
    public synchronized  void restoreClient(int ID){
        // Set player as not active
        game.setActivePlayer(ID, true);
        game.setNewNumOfTurns(ID);
        // Check if in standby mode and try to switch to the next player
        if(getPhase() == STANDBY){
            endTurn();
        }

        gameHandler.sendToEveryone(new CustomAnswer(game.getPlayerByID(ID).getUsername() + " reconnects! Now the turns will consider his/her presence"));
    }


    /**
     * {@inheritDoc}
     * <p></p>
     * This method is called by the {@link  GameHandler#dispatchActions game handler} after a successfully received action from the client.
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()){
            case "PickTilesAction" -> {
                try {
                    pickTilesAction((PickTilesAction) evt.getNewValue());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            case "PlaceTilesAction" -> placeTilesAction((PlaceTilesAction) evt.getNewValue());
            case "PrintCardsAction" -> checkPrintAction();
        }
    }
}