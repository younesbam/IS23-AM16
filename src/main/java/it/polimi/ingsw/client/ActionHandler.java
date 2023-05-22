package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.*;

import java.beans.PropertyChangeSupport;

/**
 * Handle the answer from the server.
 */
public class ActionHandler {

    private final ModelView modelView;
    private CLI cli;
    private GUI gui;

    private final PropertyChangeSupport pcsView = new PropertyChangeSupport(this);


    /**
     * Constructor in case of a CLI match
     * @param cli CLI instance
     * @param modelView model view instance
     */
    public ActionHandler(CLI cli, ModelView modelView) {
        this.cli = cli;
        this.modelView = modelView;
        pcsView.addPropertyChangeListener(cli);
    }

    /**
     * Constructor in case of a GUI match
     * @param gui GUI instance
     * @param modelView modelView instance
     */
    public ActionHandler(GUI gui, ModelView modelView) {
        this.gui = gui;
        this.modelView = modelView;
        //view.addPropertyChangeListener(gui);
    }


    /**
     * Manage the answer from the server
     * @param a Answer, received from the server
     */
    public void answerManager(Answer a){

        if(a instanceof HowManyPlayersRequest){
            pcsView.firePropertyChange("HowManyPlayersRequest", null, a.getAnswer());
            return;
        }

        if(a instanceof SetupCompleted){
            pcsView.firePropertyChange("SetupCompleted", null, a.getAnswer());
            return;
        }

        if(a instanceof WrongNum){
            pcsView.firePropertyChange("WrongNum", null, a.getAnswer());
            return;
        }

        if(a instanceof UpdateTurn){
            // modelView.setIsYourTurn(((UpdateTurn) a).getInputEnabled());  Non so se va qui o nella cli
            pcsView.firePropertyChange("UpdateTurn", null, ((UpdateTurn) a).isYourTurn());
            return;
        }

        if(a instanceof RequestTiles){
            pcsView.firePropertyChange("RequestTiles", null, ((RequestTiles) a).getAnswer());
            return;
        }

        if(a instanceof GameReplica){
            modelView.updateGame(((GameReplica) a).getAnswer());
            return;
        }

        if(a instanceof PersonalizedAnswer){
            pcsView.firePropertyChange("PersonalizedAnswer", null, a.getAnswer());
            return;
        }

        if(a instanceof RequestWhereToPlaceTiles){
            pcsView.firePropertyChange("RequestToPlaceTiles", null, ((RequestWhereToPlaceTiles) a).getAnswer());
            return;
        }

        if(a instanceof RequestWhatToDo){
            pcsView.firePropertyChange("RequestWhatToDo", null, ((RequestWhatToDo) a).getAnswer());
            return;
        }

        if(a instanceof PlayerDisconnected){
            if(gui != null) {
            } else if(cli != null) {
                cli.endGameMessage();
            }
            return;
        }
    }


}
