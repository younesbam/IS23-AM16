package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.controllers.GUIManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.*;

/**
 * GUI class starts the graphical user interface. It's controlled by a GUIManager to maintain the correct interface.
 *
 * @author Younes Bamhaoud
 * @see Application
 */
public class GUI extends Application {
    /**
     * Maps each scene name to its scene object. To easily target the correct scene when changing them.
     */
    private HashMap<String, Scene> nameMapScene = new HashMap<>();
    /**
     * Contains current scene displayed.
     */
    private Scene currentScene;
    /**
     * Contains the stage of gui.
     */
    private Stage stage;
    /**
     * Controls the GUI class, in order to set the correct scenes based on the game flow.
     */
    private GUIManager guiManager;

    /**
     * Constructor GUI
     */
    public GUI() {
        guiManager = new GUIManager(this);
    }

    /**
     * Method setCurrentScene changes the current stage scene with the parameter one.
     *
     * @param currentScene of type Scene - the scene displayed.
     */
    public void setCurrentScene(Scene currentScene){
        this.currentScene = currentScene;
    }

    /**
     * @see Application#start(Stage)
     */
    @Override
    public void start(Stage stage) throws IOException {
        nameMapScene = guiManager.setup();
        this.stage = stage;
        this.stage.setResizable(false);
        run();

    }

    /**
     * Method run sets icon and the title of the main stage and launches the window.
     */
    public void run() {
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/graphics/icon.png")));
        stage.setTitle("MyShelfie");
        stage.setScene(currentScene);
        stage.show();
    }

    /**
     * Method main of GUI class, which is called from the "MyShelfie" launcher if user chooses to play with it.
     *
     * @param args of type String[] - parsed arguments.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Method getGuiManager returns the GUIManager of this GUI object.
     *
     * @return the guiManager (type GUIManager) of this GUI object.
     */
    public GUIManager getGuiManager(){
        return this.guiManager;
    }

    /**
     * Method changeStage changes the stage scene choosing from the ones established in the setup phase.
     * The current stage scene is replaced with the parameter one.
     *
     * @param newScene of type String - the scene displayed.
     */
    public void changeStage(String newScene) {
       currentScene = nameMapScene.get(newScene);
       stage.setScene(currentScene);
       stage.show();
    }

    /**
     * Method newStage creates a new stage scene that works as a pop-up stage.
     * The stage scene is chosen from the ones established in the setup phase.
     *
     * @param newScene of type String - the scene displayed.
     */
    public void newStage(String newScene, String title){
        Stage popupStage = new Stage();
        popupStage.setScene(nameMapScene.get(newScene));
        popupStage.setTitle(title);
        popupStage.setResizable(false);
        popupStage.show();
    }




}
