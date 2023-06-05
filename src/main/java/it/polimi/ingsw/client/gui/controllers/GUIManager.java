package it.polimi.ingsw.client.gui.controllers;

import com.sun.tools.javac.Main;
import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.CountDown;
import it.polimi.ingsw.communications.serveranswers.PlayerDisconnected;
import it.polimi.ingsw.communications.serveranswers.requests.HowManyPlayersRequest;
import it.polimi.ingsw.communications.serveranswers.info.ConnectionOutcome;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.polimi.ingsw.client.gui.controllers.LoadingController;

public class GUIManager extends UI {

    private GUI gui;
    private static final String MAIN_GUI = "mainScene.fxml";
    private static final String LOADER = "loadingScene.fxml";
    private static final String GOALS = "goalCardScene.fxml";
    private static final String SETUP = "joinScene.fxml";
    private static final String CHAT = "chatScene.fxml";
    private static final String COUNTDOWN = "countDown.fxml";
    private static final String PERSONAL_GOAL_CARD_PATH = "/fxml/graphics/personal_goal_cards/";
    private static final String COMMON_GOAL_CARD_PATH = "/fxml/graphics/common_goal_cards/";
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
        if (isActiveGame()) {
            pcsDispatcher.firePropertyChange(propertyName, oldValue, newValue);
        }
    }
    public HashMap<String, Scene> setup() {
        List<String> fxmList = new ArrayList<>(Arrays.asList(SETUP,LOADER, MAIN_GUI, GOALS, CHAT, COUNTDOWN));
        try {
            for (String path : fxmList) {
                FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/fxml/" + path));
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

    /**
            * Set unique ID generated by the server.
     * @param s
     */
    private void howManyPlayerRequest(String s){
        System.out.println(s);
        modelView.setIsYourTurn(true);
        LoadingController loadingController = (LoadingController) getControllerFromName(LOADER);
        Platform.runLater(() ->{
            loadingController.resetLoading();
            loadingController.updateStatus(s);
        });
        System.out.println(loadingController);
        System.out.println(loadingController.getStatus());

    }

    private void connectionOutcome(ConnectionOutcome a){
        System.out.println(a.getAnswer());
        client.setID(a.getID());
        Platform.runLater(() -> {
            gui.changeStage(LOADER);
        });

    }

    private void customAnswer(String answer) {
        System.out.println(answer);
    }
    public void wrongNum(String s){
        howManyPlayerRequest(s);
    }
    public void playerNumberChosen(String s){
        System.out.println(s);

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
        System.out.println(string);

        System.out.println("\n\nHere is your new Bookshelf:\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();
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
                });
                System.out.println("It's now your turn!");
            }
            else {
                System.out.println("\nWait for your next turn now!");
                Platform.runLater(()->{
                    mainSceneController.printBoard();
                    mainSceneController.disablePickTiles();
                    mainSceneController.updateTurn("Turn: " + modelView.getGame().getCurrentPlayer().getUsername());
                });
            }
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

        System.out.println("\n\nHere is your Bookshelf:\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();

        System.out.println("\n\nAnd here is the game board:\n");

        modelView.getGame().getBoard().printBoard();
        System.out.println("\n");

        //printMAN();
        System.out.println(request + "\n>");
    }
    public void requestWhereToPlaceTiles(String request){
        MainSceneController mainSceneController = (MainSceneController) getControllerFromName(MAIN_GUI);
        Platform.runLater(()->mainSceneController.allowPlaceTiles());
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();
        System.out.println("\n");
        //printMAN();
        System.out.println("\n" + request + "\n");

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
            mainSceneController.updateTurn("Turn: "+s);
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
    public void goalCardsAssigned(){
        GoalCardSceneController goalCardSceneController = (GoalCardSceneController) getControllerFromName(GOALS);
        Platform.runLater(()->{
        goalCardSceneController.setCommonGoalCard1(COMMON_GOAL_CARD_PATH
                        + modelView.getGame().getCommonGoalCards().get(0).getCardNumber()
                        + ".png");
        goalCardSceneController.setCommonGoalCard2(COMMON_GOAL_CARD_PATH
                + modelView.getGame().getCommonGoalCards().get(1).getCardNumber()
                + ".png");
        goalCardSceneController.setPersonalGoalCard(PERSONAL_GOAL_CARD_PATH
                + modelView.getGame().getPlayerByID(getPlayerID()).getPersonalGoalCard().getCardNumber()
                + ".png");
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
           case "ConnectionOutcome" -> connectionOutcome((ConnectionOutcome) event.getNewValue());
           case "CountDown" -> countDown((CountDown) event.getNewValue());
           case "HowManyPlayersRequest" -> howManyPlayerRequest((String) event.getNewValue());
           case "UpdateTurn" -> updateTurn((Boolean) event.getNewValue());
           case "CustomAnswer" -> customAnswer((String) event.getNewValue());
           case "RequestWhatToDo" -> initialPhaseOfTheTurn((String) event.getNewValue());
           case "RequestToPlaceTiles" -> requestWhereToPlaceTiles((String) event.getNewValue());
           case "WrongNum" -> wrongNum((String) event.getNewValue());
           case "BookShelfFilledWithTiles" -> tilesPlaced((String) event.getNewValue());
           case "ItsYourTurn" -> updateTurn(true);
           case "EndOfYourTurn" -> updateTurn(false);
           case "PlayerNumberChosen" -> playerNumberChosen((String) event.getNewValue());
           case "FirstPlayerSelected" -> firstPlayerSelected((String) event.getNewValue());
           case "ChairAssigned" -> chairAssigned((String) event.getNewValue());
           case "GameReady" -> gameReady((String) event.getNewValue());
           case "GoalCardsAssigned" -> goalCardsAssigned();
        }
    }


}
