package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.GameReplica;
import it.polimi.ingsw.communications.serveranswers.RequestTiles;

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
        view.addPropertyChangeListener(gui);
    }



    public void answerManager(Answer a){

        if(a instanceof RequestTiles){
            view.firePropertyChange("RequestTiles");
        }
        if(a instanceof GameReplica){
            modelView.updateGame(((GameReplica) a).getGameReplica());
        }



    }


}
