package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.ConnectionOutcome;
import it.polimi.ingsw.communications.serveranswers.PlayerDisconnected;
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
        List<String> fxmList = new ArrayList<>(Arrays.asList(SETUP,LOADER, MAIN_GUI, GOALS, CHAT));
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

    public void changeStage(String newScene){
        //Platform.runLater(() -> {
            gui.changeStage(newScene);
        //});
    }

    public GUI getGui(){return this.gui;}
    private void tilesPlaced(String string) {
        System.out.println(string);

        System.out.println("\n\nHere is your new Bookshelf:\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();
    }

    private void updateTurn(Boolean yourTurn) {
        if(yourTurn){
            System.out.println("\nIt's now your turn!");
        }
        else
            System.out.println("\nWait for your next turn now!");
        modelView.setIsYourTurn(yourTurn);

        if(yourTurn)
            System.out.print(">");
    }
    private void initialPhaseOfTheTurn(String request){
        modelView.setIsYourTurn(true);


        System.out.println("\n\nHere is your Bookshelf:\n");
        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();

        System.out.println("\n\nAnd here is the game board:\n");

        modelView.getGame().getBoard().printBoard();
        System.out.println("\n");

        //printMAN();
        System.out.println(request + "\n>");
    }
    public void requestWhereToPlaceTiles(String request){

        modelView.getGame().getCurrentPlayer().getBookShelf().printBookShelf();
        System.out.println("\n");
        //printMAN();

        System.out.println("\n" + request + "\n");

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

        }
    }


}
