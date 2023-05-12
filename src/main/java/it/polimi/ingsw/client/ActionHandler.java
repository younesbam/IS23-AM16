package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.*;

import java.beans.PropertyChangeSupport;

public class ActionHandler {

    private final ModelView modelView;
    private CLI cli;
    private GUI gui;

    private final PropertyChangeSupport view = new PropertyChangeSupport(this);


    /**
     * Constructor in case of a CLI match
     * @param cli CLI instance
     * @param modelView model view instance
     */
    public ActionHandler(CLI cli, ModelView modelView) {
        this.cli = cli;
        this.modelView = modelView;
        view.addPropertyChangeListener(cli);
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



    public void answerManager(Answer a){

        if(a instanceof RequestTiles){
            view.firePropertyChange("RequestTiles", null, ((RequestTiles) a).getAnswer());
        }
        if(a instanceof GameReplica){
            modelView.updateGame(((GameReplica) a).getGameReplica());
        }
        if(a instanceof PersonalizedAnswer){
            view.firePropertyChange("PersonalizedAnswer", null, a.getAnswer());
        }
        if(a instanceof RequestWhereToPlaceTiles){
            view.firePropertyChange("RequestToPlaceTiles", null, ((RequestWhereToPlaceTiles) a).getAnswer());
        }
        if(a instanceof RequestWhatToDo){
            view.firePropertyChange("RequestWhatToDo", null, ((RequestWhatToDo) a).getAnswer());
        }
        if(a instanceof PlayerDisconnected){
            if(gui != null) {
            } else if(cli != null) {
                cli.endGameMessage();
            }
        }
    }


}
