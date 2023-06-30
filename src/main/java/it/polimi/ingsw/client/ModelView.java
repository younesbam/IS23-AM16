package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.model.Game;


/**
 * This class contains details about the current game.
 */
public class ModelView {
    private String username;
    private final GUI gui;
    private final CLI cli;
    private Answer answerFromServer;
    private boolean isYourTurn = false;
    private Game game;
    /**
     * Represent if the client is connected to the server.
     */
    private boolean isConnected;
    private final boolean isGUI;


    /**
     * Class constructor using GUI.
     * @param gui
     */
    public ModelView(GUI gui) {
        this.gui = gui;
        this.cli = null;
        this.isGUI = true;
    }

    /**
     * Class constructor using CLI.
     * @param cli
     */
    public ModelView(CLI cli)  {
        this.cli = cli;
        this.gui = null;
        this.isGUI = false;
    }

    /**
     * Player username setter.
     * @param username
     */
    public void setUsername(String username) {
        this.username  = username;
    }

    /**
     * Setter for isYourTurn parameter.
     * @param isYourTurn
     */
    public void setIsYourTurn(boolean isYourTurn){
        this.isYourTurn = isYourTurn;
    }

    /**
     * Getter for isYourTurn parameter.
     */
    public boolean getIsYourTurn(){
        return this.isYourTurn;
    }

    /**
     * CLI getter.
     * @return cli
     */
    public CLI getCli(){
        return this.cli;
    }

    /**
     * GUI getter.
     * @return
     */
    public GUI getGui(){
        return this.gui;
    }

    /**
     * isGUI getter.
     * @return true if modelView is GUI, false if is CLI
     */
    public boolean isGui(){
        return this.isGUI;
    }

    /**
     * Game updater.
     * @param game
     */
    public void updateGame(Game game) {
        this.game = game;
    }


    /**
     * Game getter.
     * @return
     */
    public Game getGame() {
        return game;
    }

    /**
     * IsConnected getter.
     * @return
     */
    public boolean isConnected(){
        return isConnected;
    }

    /**
     * IsConnected setter.
     * @param isConnected
     */
    public void setConnected(boolean isConnected){
        this.isConnected = isConnected;
    }

    /**
     * Answer setter.
     * @param a
     */
    public void setAnswerFromServer(Answer a){
        this.answerFromServer = a;
    }

    /**
     * Answer getter.
     * @return
     */
    public Answer getAnswerFromServer(){
        return this.answerFromServer;
    }

}
