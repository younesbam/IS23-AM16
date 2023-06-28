package it.polimi.ingsw.client.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.UI;
import it.polimi.ingsw.client.gui.controllers.GUIController;
import it.polimi.ingsw.client.gui.controllers.GUIManager;
import it.polimi.ingsw.client.gui.controllers.LoadingController;
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
    private HashMap<String, Scene> nameMapScene = new HashMap<>();
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
    private GUIManager guiManager;

    /**
     * Constructor GUI
     */
    public GUI() {
        guiManager = new GUIManager(this);
    }
    /*public void setNameMapScene() {
        List<String> fxmList = new ArrayList<>(Arrays.asList(SETUP,LOADER, MAIN_GUI, GOALS, CHAT));
        try {
            for (String path : fxmList) {
                FXMLLoader loader = new FXMLLoader(GUI.class.getResource("/fxml/" + path));
                nameMapScene.put(path, new Scene(loader.load()));
            }
        } catch (IOException e) {
            System.out.println("Scene not found");
        }

    }*/
    public void setCurrentScene(Scene currentScene){
        this.currentScene = currentScene;
    }
    @Override
    public void start(Stage stage) throws IOException {
        nameMapScene = guiManager.setup();
        this.stage = stage;
        run();

    }

    public void run() {
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/graphics/icon.png")));
        stage.setTitle("MyShelfie");
        stage.setScene(currentScene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }


    public Stage getStage() {
        return this.stage;
    }

    public Scene getCurrentScene(){
        return currentScene;
    }
    /**
     * Method getModelView returns the modelView of this GUI object.
     *
     * @return the modelView (type ModelView) of this GUI object.
     */

    public GUIManager getGuiManager(){
        return this.guiManager;
    }




    public void changeStage(String newScene) {
       currentScene = nameMapScene.get(newScene);
       stage.setScene(currentScene);
       stage.show();
    }

    public void newStage(String newScene, String title){
        Stage popupStage = new Stage();
        popupStage.setScene(nameMapScene.get(newScene));
        popupStage.setTitle(title);
        popupStage.show();
    }




}
