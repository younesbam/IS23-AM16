package it.polimi.ingsw.client.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.client.gui.controllers.GUIController;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.RMICSConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI extends Application {
    public static final String END_OF_THE_GAME = "End of the game";
    private static final String MAIN_GUI = "mainScene.fxml";
    private static final String LOADER = "loadingScene.fxml";
    private static final String GOALS = "goalCardScene.fxml";
    private static final String SETUP = "joinScene.fxml";
    private static final String CHAT = "chatScene.fxml";
    private CSConnection csConnection = null;
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private final ModelView modelView;
    private final ActionHandler actionHandler;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final HashMap<String, Scene> nameMapScene = new HashMap<>();
    /**
     * Maps each scene controller's name to the effective controller object, in order to get the correct controller
     * for modifying operations.
     *
     * @see it.polimi.ingsw.client.gui.controllers for more details.
     */
    private final HashMap<String, GUIController> nameMapController = new HashMap<>();

    private boolean activeGame;
    private Scene currentScene;
    private Stage stage;
    private boolean[] actionCheckers;

    /**
     * Constructor GUI
     */
    public GUI() {
        this.modelView = new ModelView(this);
        actionHandler = new ActionHandler(this, modelView);
        activeGame = true;

    }

    public void setup() {
        List<String> fxmList = new ArrayList<>(Arrays.asList(SETUP,LOADER, MAIN_GUI, GOALS, CHAT));
        try {
            for (String path : fxmList) {
                FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/fxml/" + path));
                nameMapScene.put(path, new Scene(loader.load()));
                GUIController controller = loader.getController();
                controller.setGui(this);
                nameMapController.put(path, controller);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        currentScene = nameMapScene.get(SETUP);
    }
    @Override
    public void start(Stage stage) throws IOException {
        setup();
        this.stage = stage;
        run();

    }

    public void run() {
        stage.setTitle("MyShelfie");
        stage.setScene(currentScene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

    /**
     * Method getListeners returns the listeners of this GUI object.
     *
     * @return the listeners (type PropertyChangeSupport) of this GUI object.
     */
    public PropertyChangeSupport getListeners() {
        return listeners;
    }

    /**
     * Method changeStage changes the stage scene based on the ones declared during setup phase.
     * On method call the actual stage scene is replaced from the parameter one.
     *
     * @param newScene of type String - the scene displayed.
     */
    public void changeStage(String newScene) {
        currentScene = nameMapScene.get(newScene);
        stage.setScene(currentScene);
        stage.show();
    }
    /**
     * Method getModelView returns the modelView of this GUI object.
     *
     * @return the modelView (type ModelView) of this GUI object.
     */
    public ModelView getModelView() {
        return modelView;
    }

    /**
     * Method getActionHandler returns the actionHandler of this GUI object.
     *
     * @return the actionHandler (type ActionHandler) of this GUI object.
     */
    public ActionHandler getActionHandler() {
        return actionHandler;
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

    /**
     * Method getCSConnection returns the connection of this GUI object.
     *
     * @return the connection of this GUI object.
     */
    public CSConnection getCSConnection() {
        return csConnection;
    }

    /**
     * Method setCSConnection sets the connection of this GUI object.
     *
     * @param csConnection the connection of this GUI object.
     */
    public void setCSConnection(CSConnection csConnection) {
        if (this.csConnection == null) {
            this.csConnection = csConnection;
        }
    }
    /**
     * Method errorDialog displays a generic error.
     *
     * @param error of type String - the error displayed.
     */
    private void errorDialog(String error) {
        Alert errorDialog = new Alert(Alert.AlertType.ERROR);
        errorDialog.setTitle("Game Error");
        errorDialog.setHeaderText("Error!");
        errorDialog.setContentText(error);
        errorDialog.showAndWait();
    }
    /**
     * Method setupHandler handles the setup game phase
     *
     */
    /*public void setupHandler() {
        Platform.runLater(() -> {
                LoaderController controller = (LoaderController) getControllerFromName(LOADER);
                controller.requestPlayerNumber(((RequestPlayersNumber) modelView.getServerAnswer()).getMessage());
            });
        }
    }*/




}
