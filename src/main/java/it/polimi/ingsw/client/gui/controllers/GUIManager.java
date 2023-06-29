package it.polimi.ingsw.client.gui.controllers;

import com.sun.tools.javac.Main;
import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.start.CountDown;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.network.ConnectionOutcome;
import it.polimi.ingsw.model.Player;
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

public class GUIManager extends UI {

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
    private boolean playingGame = false;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final HashMap<String, Scene> nameMapScene = new HashMap<>();
    /**
     * Maps each scene controller's name to the effective controller object, in order to get the correct controller
     * for modifying operations.
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
    public void firePC(String propertyName, Object oldValue,Object newValue){
        modelView.setIsYourTurn(true);
        if (isActiveGame()) {
            pcsDispatcher.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
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

    public ModelView getModelView(){
        return this.modelView;
    }

    public void loadGame(){
        Platform.runLater(() -> {
            gui.changeStage(LOADER);
        });
    }

    private void howManyPlayerRequest(String s){
        modelView.setIsYourTurn(true);
        LoadingController loadingController = (LoadingController) getControllerFromName(LOADER);
        Platform.runLater(() ->{
            loadingController.resetLoading();
            loadingController.updateStatus(s);
        });
    }

    private void connectionOutcome(ConnectionOutcome a){
        System.out.println(a.getAnswer());
        client.setID(a.getID());
        Platform.runLater(() -> {
            gui.changeStage(MULTI_GAME);
        });

    }
    public void playerNumberChosen(String s){
        updateTurn(false);
        Platform.runLater(() -> {
            LoadingController loadingController = (LoadingController)getControllerFromName(LOADER);
            loadingController.updateStatus(s);
            loadingController.waitingMode();
        });
    }

    public void popupStage(String newScene, String title){
        Platform.runLater(() ->{
            gui.newStage(newScene, title);
        });
    }

    public GUI getGui(){return this.gui;}
    private void tilesPlaced(String string) {
        MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
        Platform.runLater(()->{
            mainSceneController.printBookShelf();
        });
    }

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
    private void initialPhaseOfTheTurn(String request){

        modelView.setIsYourTurn(true);
        Platform.runLater(()->{
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.printBoard();
            mainSceneController.printBookShelf();
            mainSceneController.allowPickTiles();
        });
    }

    public void requestWhereToPlaceTiles(String request){
        MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
        Platform.runLater(()->{
            mainSceneController.allowPlaceTiles();
        });
    }

    public void countDown(CountDown a){
        CountDownController countDownController = (CountDownController) getControllerFromName(COUNTDOWN);
        Platform.runLater(()->{
            gui.changeStage(COUNTDOWN);
            countDownController.updateTime(a.getAnswer().substring(a.getAnswer().length() - 1));
        });
    }

    public void firstPlayerSelected(String s){
        Platform.runLater(()->{
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.updateTurn(s);
        });
    }

    public void chairAssigned(String s){
        Platform.runLater(()->{
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.updateTurn(s);
            mainSceneController.showChair();
        });
    }

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
    public void updatePlayerPoints(String points){
        Platform.runLater(()->{
            MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
            mainSceneController.updatePoints(points);
        });
    }
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

    public void endGame() {
        System.out.println("Thanks for playing MyShelfie! Shutting down...");
        Platform.exit();
        System.exit(0);
    }

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

    private void playerFinalPoints(String message){
        GameOverController gameOverController = (GameOverController) getControllerFromName(GAME_OVER);
        Platform.runLater(()->{
            gui.changeStage(GAME_OVER);
            gameOverController.setPointsMsg(message);
        });
    }

    private void ranking(String ranking){
        GameOverController gameOverController = (GameOverController) getControllerFromName(GAME_OVER);
        Platform.runLater(()->{
            gameOverController.setRanking(ranking);
        });
    }

    private void playerFinalResult(String message){
        GameOverController gameOverController = (GameOverController) getControllerFromName(GAME_OVER);
        Platform.runLater(()->{
            gameOverController.setPlayerResult(message);
        });
    }

    private void restorePlayer(String message){
        if(isActiveGame()){
            gameReady(message);
            ((MainSceneController)getControllerFromName(MAIN_GUI)).updateTurn(modelView.getGame().getCurrentPlayer().getUsername());
        }
    }

    private void playerDisconnection(String message){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("INFORMATION");
            alert.setHeaderText("A player disconnected.");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    public int getPlayerID(){
        return client.getID();
    }
    /**
     * Method getControllerFromName gets a scene controller based on inserted name from the dedicated hashmap.
     *
     * @param name of type String - the player's name.
     * @return GUIController - the scene controller.
     */
    public GUIController getControllerFromName(String name) {
        return nameMapController.get(name);
    }
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
           //case "PrintCardsAnswer" -> printGoalCards();
        }
    }


}
