package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.start.CountDown;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.network.ConnectionOutcome;
import it.polimi.ingsw.model.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Class GUIManager controls the GUI class. When the server reports an event change,
 * it triggers the changes to be made by the GUI class.
 *
 * @author Younes Bamhaoud
 * @see UI
 * @see Application
 */
public class GUIManager extends UI {
    /**
     * GUI object to control.
     */
    private GUI gui;
    private static final String MAIN_GUI = "mainScene.fxml";
    private static final String LOADER = "loadingScene.fxml";
    private static final String GOALS = "goalCardScene.fxml";
    private static final String BOOKSHELFS1 = "bookshelf1PlayerScene.fxml";
    private static final String BOOKSHELFS2 = "bookshelf2PlayerScene.fxml";
    private static final String BOOKSHELFS3 = "bookshelf3PlayerScene.fxml";
    private static final String SETUP = "joinScene.fxml";
    private static final String COUNTDOWN = "countDown.fxml";
    private static final String GAME_OVER = "gameOverScene.fxml";
    private static final String MULTI_GAME = "multiGameScene.fxml";
    private static final String PERSONAL_GOAL_CARD_PATH = "/graphics/personal_goal_cards/";
    private static final String COMMON_GOAL_CARD_PATH = "/graphics/common_goal_cards/";
    private static final String GOAL_CARD_TOKEN_PATH = "/graphics/tokens/scoring";
    private static final int MAX_TOKEN_POINTS = 8;
    private static final int TOKEN_POINTS_RANGE = 2;
    private final Logger logger = Logger.getLogger(getClass().getName());
    /**
     * Attribute playing game is true if the game is on, false if still in setup mode.
     */
    private boolean playingGame = false;

    /**
     * Maps each scene name to its scene object. To easily target the correct scene when changing them.
     */
    private final HashMap<String, Scene> nameMapScene = new HashMap<>();

    /**
     * Maps each scene name to its controller object. To easily target the correct controller when editing the scene.
     *
     * @see it.polimi.ingsw.client.gui.controllers for more details.
     */
    private final HashMap<String, GUIController> nameMapController = new HashMap<>();

    /**
     * GUIManager constructor.
     */
    public GUIManager(GUI gui) {
        this.pcsDispatcher = new PropertyChangeSupport(this);
        this.gui = gui;
        this.modelView = new ModelView(gui);
        this.actionHandler = new ActionHandler(this, this.modelView);
        setActiveGame(true);
    }

    /**
     * Method firePC to report to the server that a property change event occurred on client side.
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    public void firePC(String propertyName, Object oldValue,Object newValue){
        modelView.setIsYourTurn(true);
        if (isActiveGame()) {
            pcsDispatcher.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Method setup creates all the stage phases which will be updated in other methods, their related
     *  controllers, then stores them using the correct hashmaps to link them with the fxml file and controller.
     *
     * @return nameMapScene (Type HashMap<String, Scene>)
     */
    public HashMap<String, Scene> setup() {
        List<String> fxmList = new ArrayList<>(Arrays.asList(SETUP, MULTI_GAME, LOADER, MAIN_GUI, GOALS, BOOKSHELFS1, BOOKSHELFS2,
                                                            BOOKSHELFS3, COUNTDOWN, GAME_OVER));
        try {
            for (String path : fxmList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + path));
                nameMapScene.put(path, new Scene(loader.load()));
                GUIController controller = loader.getController();
                controller.setGuiManger(this);
                nameMapController.put(path, controller);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        gui.setCurrentScene(nameMapScene.get(SETUP));
        return nameMapScene;
    }

    /**
     * Method getControllerFromName gets a scene controller based on the name from the hashmap.
     *
     * @param name of type String - the player's name.
     * @return GUIController - the scene controller.
     */
    public GUIController getControllerFromName(String name) {
        return nameMapController.get(name);
    }

    /**
     *  Method getModelView returns the modelView of this GUIManager object.
     * @return modelView (Type ModelView)
     * @see UI
     */
    public ModelView getModelView(){
        return this.modelView;
    }

    /**
     * Method propertyChange triggers the methods based on the event received from the server
     * @param event A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event){
        switch (event.getPropertyName()){
            case "ErrorAnswer" -> errorMessage((ErrorAnswer) event.getNewValue());
            case "ConnectionOutcome" -> connectionOutcome((ConnectionOutcome) event.getNewValue());
            case "CountDown" -> countDown((CountDown) event.getNewValue());
            case "HowManyPlayersRequest" -> howManyPlayerRequest((String) event.getNewValue());
            case "UpdateTurn" -> updateTurn((Boolean) event.getNewValue());
            case "PickTilesRequest" -> initialPhaseOfTheTurn((String) event.getNewValue());
            case "RequestToPlaceTiles" -> requestWhereToPlaceTiles((String) event.getNewValue());
            case "BookShelfFilledWithTiles" -> tilesPlaced((String) event.getNewValue());
            case "ItsYourTurn" -> updateTurn(true);
            case "EndOfYourTurn" -> updateTurn(false);
            case "PlayerNumberChosen" -> playerNumberChosen((String) event.getNewValue());
            case "FirstPlayerSelected" -> firstPlayerSelected((String) event.getNewValue());
            case "ChairAssigned" -> chairAssigned((String) event.getNewValue());
            case "GameReady" -> gameReady((String) event.getNewValue());
            case "UpdatePlayerPoints" -> updatePlayerPoints((String) event.getNewValue());
            case "BookShelfCompleted" -> bookShelfCompleted((String) event.getNewValue());
            case "PlayerFinalPoints" -> playerFinalPoints((String) event.getNewValue());
            case "Ranking" -> ranking((String) event.getNewValue());
            case "PlayerFinalResult" -> playerFinalResult((String) event.getNewValue());
            case "RestorePlayer" -> restorePlayer((String) event.getNewValue());
            case "PlayerDisconnection" -> playerDisconnection((String) event.getNewValue());
        }
    }

    /**
     * Method loadGame sets the stage scene to the loading scene.
     */
    public void loadGame(){
        Platform.runLater(() -> {
            gui.changeStage(LOADER);
        });
    }

    /**
     * Method howManyPlayersRequest handles the display of number of players of the game.
     * @param s of type String  - the answer received from the server.
     */
    private void howManyPlayerRequest(String s){
        modelView.setIsYourTurn(true);
        LoadingController loadingController = (LoadingController) getControllerFromName(LOADER);
        Platform.runLater(() ->{
            loadingController.resetLoading();
            loadingController.updateStatus(s);
        });
    }

    /**
     * Method connectionOutcome handles the display of a successful connection to the server.
     * It sets the stage scene to the multi-game scene
     * @param a of type ConnectionOutcome  - the answer received from the server.
     */
    private void connectionOutcome(ConnectionOutcome a){
        System.out.println(a.getAnswer());
        client.setID(a.getID());
        Platform.runLater(() -> {
            gui.changeStage(MULTI_GAME);
        });
    }

    /**
     * Method playerNumberChosen handles the display after the player has chosen the number of players in the game.
     * @param s of type String  - the answer received from the server.
     */
    public void playerNumberChosen(String s){
        updateTurn(false);
        Platform.runLater(() -> {
            LoadingController loadingController = (LoadingController)getControllerFromName(LOADER);
            loadingController.updateStatus(s);
            loadingController.waitingMode();
        });
    }

    /**
     * Method popupStage calls GUI object method newStage to pop up a new stage.
     * @param newScene of type String to retreive the scene from the hash map.
     * @param title of type String to set the title of the window.
     */
    public void popupStage(String newScene, String title){
        Platform.runLater(() ->{
            gui.newStage(newScene, title);
        });
    }

    /**
     * Method getGui returns the GUI object which it controls.
     * @return gui (Type GUI)
     */
    public GUI getGui(){return this.gui;}

    /**
     * Method tilesPlaced prints the updated bookshelf once the tiles are placed.
     * @param string of type String - answer received from the server.
     */
    private void tilesPlaced(String string) {
        MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
        Platform.runLater(()->{
            mainSceneController.printBookShelf();
        });
    }

    /**
     * Method updateTurn handles the updates of components of the scene and actions allowed on the interface
     * based on the turn.
     * @param yourTurn of type Boolean - true if it's the player's turn, false otherwise
     */
    private void updateTurn(Boolean yourTurn) {
        if(playingGame){
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            if(yourTurn){
                Platform.runLater(()->{
                    mainSceneController.updateTurn("It's now your turn!");
                    mainSceneController.printBoard();
                    mainSceneController.allowPickTiles();
                    mainSceneController.printBookShelf();
                    mainSceneController.printBoard();
                });
            }
            else {
                Platform.runLater(()->{
                    mainSceneController.printBoard();
                    mainSceneController.disablePickTiles();
                    mainSceneController.updateTurn(modelView.getGame().getCurrentPlayer().getUsername());
                });
            }
            GoalCardSceneController goalCardSceneController = (GoalCardSceneController) getControllerFromName(GOALS);
            Platform.runLater(()->{
                int countGoal1Completed = 0;
                int countGoal2Completed = 0;
                for (Player p: modelView.getGame().getPlayers()) {
                    if(p.getCommonCardPoints(0)>0)
                        countGoal1Completed++;
                    if(p.getCommonCardPoints(1)>0)
                        countGoal2Completed++;
                }
                mainSceneController.setTokens(1 , GOAL_CARD_TOKEN_PATH +
                        modelView.getGame().getPlayerByID(getPlayerID()).getCommonCardPoints(0)
                        +".png");
                mainSceneController.setTokens(2, GOAL_CARD_TOKEN_PATH +
                        modelView.getGame().getPlayerByID(getPlayerID()).getCommonCardPoints(1)
                        +".png");
                goalCardSceneController.setTokens(0,GOAL_CARD_TOKEN_PATH +
                        (MAX_TOKEN_POINTS - TOKEN_POINTS_RANGE * countGoal1Completed)
                        + ".png");
                goalCardSceneController.setTokens(1,GOAL_CARD_TOKEN_PATH +
                        (MAX_TOKEN_POINTS - TOKEN_POINTS_RANGE * countGoal2Completed)
                        + ".png");
            });
        }

        modelView.setIsYourTurn(yourTurn);
    }

    /**
     * method initialPhaseOfTheTurn handles the setup if player is the current playing player
     * @param request of type String - answer from the server.
     */
    private void initialPhaseOfTheTurn(String request){

        modelView.setIsYourTurn(true);
        Platform.runLater(()->{
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.printBoard();
            mainSceneController.printBookShelf();
            mainSceneController.allowPickTiles();
        });
    }

    /**
     * Method requestWhereToPlaceTiles allows the player to place tiles in the interface.
     * @param request of type String - answer from the server.
     */
    public void requestWhereToPlaceTiles(String request){
        MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
        Platform.runLater(()->{
            mainSceneController.allowPlaceTiles();
        });
    }
    /**
     * Method countDown displays the countdown before a game starts
     * @param a of type CountDown - answer from the server.
     */
    public void countDown(CountDown a){
        CountDownController countDownController = (CountDownController) getControllerFromName(COUNTDOWN);
        Platform.runLater(()->{
            gui.changeStage(COUNTDOWN);
            countDownController.updateTime(a.getAnswer().substring(a.getAnswer().length() - 1));
        });
    }

    /**
     * Method firstPlayerSelected shows the first player chosen if it is not this player.
     * @param s of type String - answer from the server.
     */
    public void firstPlayerSelected(String s){
        Platform.runLater(()->{
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.updateTurn(s);
        });
    }

    /**
     * Method chairAssigned gives the first player the chair.
     * @param s of type String - answer from the server.
     */
    public void chairAssigned(String s){
        Platform.runLater(()->{
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.updateTurn(s);
            mainSceneController.showChair();
        });
    }

    /**
     * Method gameReady handles the setup when the Game is started in the beginning.
     * @param s of type String - answer from the server.
     */
    public void gameReady(String s){
        playingGame = true;
        Platform.runLater(()->{
            gui.changeStage(MAIN_GUI);
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.printBoard();
            if(!modelView.getIsYourTurn())
                mainSceneController.disablePickTiles();
            mainSceneController.printBookShelf();
            mainSceneController.setUsername(client.getUsername());
        });
    }

    /**
     * Method updatePlayerPoints updates the view of points of the player.
     * @param points of type String - answer from the server.
     */
    public void updatePlayerPoints(String points){
        Platform.runLater(()->{
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.updatePoints(points);
        });
    }

    /**
     * Method printGoalCards sets the view of goal cards
     */
    public void printGoalCards(){
        GoalCardSceneController goalCardSceneController = (GoalCardSceneController) getControllerFromName(GOALS);
        Platform.runLater(()->{
        goalCardSceneController.setCommonGoalCard1(COMMON_GOAL_CARD_PATH
                        + modelView.getGame().getCommonGoalCards().get(0).getCardNumber()
                        + ".jpg");
        goalCardSceneController.setCommonGoalCard2(COMMON_GOAL_CARD_PATH
                + modelView.getGame().getCommonGoalCards().get(1).getCardNumber()
                + ".jpg");
        goalCardSceneController.setPersonalGoalCard(PERSONAL_GOAL_CARD_PATH
                + modelView.getGame().getPlayerByID(getPlayerID()).getPersonalGoalCard().getCardNumber()
                + ".png");
        });
    }

    /**
     * Method printBookShelfs handles the display of bookshelfs of other players, based on the number of players.
     */
    public void printBookShelfs(){
        int numOfOtherPlayers = modelView.getGame().getNumOfPlayers() - 1 ;
        String title = "Bookselfs";
        switch (numOfOtherPlayers){
            case 1:
                BookShelf1PlayerController controller1 = (BookShelf1PlayerController) getControllerFromName(BOOKSHELFS1);
                Platform.runLater(()->{
                    controller1.setView();
                });
                popupStage(BOOKSHELFS1, title); break;
            case 2:
                BookShelf2PlayerController controller2 = (BookShelf2PlayerController) getControllerFromName(BOOKSHELFS2);
                Platform.runLater(()->{
                    controller2.setView();
                });
                popupStage(BOOKSHELFS2, title); break;
            case 3:
                BookShelf3PlayerController controller3 = (BookShelf3PlayerController) getControllerFromName(BOOKSHELFS3);
                Platform.runLater(()->{
                    controller3.setView();
                });
                popupStage(BOOKSHELFS3, title); break;
        }
    }

    /**
     * Method errorMessage handles the display of an Error and based on the error, it makes the correct view changes.
     * @param a of type String - answer from the server.
     */
    private void errorMessage(ErrorAnswer a) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Something went wrong!");
            if(a.getAnswer()!=null)
                alert.setContentText(a.getAnswer().toString());
            alert.showAndWait();
        });
        switch (a.getError()){
            case NOT_ENOUGH_SPACE, TILES_NOT_PICKABLE,
                    INVALID_ROW_COL,
                    TILES_NOT_STRAIGHT,
                    TILES_NOT_ADJACENT -> Platform.runLater(()->{
                MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
                mainSceneController.cancelTilesChoise();
            });
            case LOBBY_NOT_READY, MAX_PLAYERS_REACHED, TAKEN_USERNAME ->{
                    Client.LOGGER.log(Level.INFO, a.getError().name());
                    endGame();
            }
            case MATCH_NOT_FOUND, INVALID_MATCH_NAME -> {
                Platform.runLater(()->gui.changeStage(MULTI_GAME));
            }
        }

    }

    /**
     * method endGame shuts down the gui.
     */
    public void endGame() {
        System.out.println("Thanks for playing MyShelfie! Shutting down...");
        Platform.exit();
        System.exit(0);
    }

    /**
     * Method bookShelfCompleted handles the changes when a player completed its bookshelf.
     * @param message of type String - answer from the server.
     */
    private void bookShelfCompleted(String message){
        Platform.runLater(() -> {
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.hideEndGameToken();
            if(message.contains("You have completed your Bookshelf"))
                mainSceneController.showEndGameToken();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("GAME");
            alert.setHeaderText("Last turn!");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Method playerFinalPoints sets the stage scene to the game over one
     * @param message of type String - answer from the server.
     */
    private void playerFinalPoints(String message){
        GameOverController gameOverController = (GameOverController) getControllerFromName(GAME_OVER);
        Platform.runLater(()->{
            gui.changeStage(GAME_OVER);
            gameOverController.setPointsMsg(message);
        });
    }

    /**
     * Method ranking displays the final ranking of the players.
     * @param ranking of type String - answer from the server.
     */
    private void ranking(String ranking){
        GameOverController gameOverController = (GameOverController) getControllerFromName(GAME_OVER);
        Platform.runLater(()->{
            gameOverController.setRanking(ranking);
        });
    }

    /**
     * Method playerFinalResult displays the final result (win or lose) of the players.
     * @param message of type String - answer from the server.
     */
    private void playerFinalResult(String message){
        GameOverController gameOverController = (GameOverController) getControllerFromName(GAME_OVER);
        Platform.runLater(()->{
            gameOverController.setPlayerResult(message);
        });
    }

    /**
     * Method restorePlayer handles the restore of player
     * @param message
     */
    private void restorePlayer(String message){
        if(isActiveGame()){
            gameReady(message);
            ((MainSceneController)getControllerFromName(MAIN_GUI)).updateTurn(modelView.getGame().getCurrentPlayer().getUsername());
        }
    }

    /**
     * Method playerDisconnection handles the disconnection of player
     * @param message
     */
    private void playerDisconnection(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("INFORMATION");
            alert.setHeaderText("A player disconnected.");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Method getPlayerID returns the id of the player
     * @return client.getID (Type int)
     */
    public int getPlayerID(){
        return client.getID();
    }
}
