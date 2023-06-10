package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.communications.serveranswers.*;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.info.*;
import it.polimi.ingsw.communications.serveranswers.requests.DisconnectPlayer;
import it.polimi.ingsw.communications.serveranswers.requests.HowManyPlayersRequest;
import it.polimi.ingsw.communications.serveranswers.requests.PickTilesRequest;
import it.polimi.ingsw.communications.serveranswers.requests.PlaceTilesRequest;

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
        //pcsView.addPropertyChangeListener(gui);
    }


    /**
     * Manage the answer from the server
     * @param a Answer, received from the server
     */
    public void answerManager(Answer a){

        if(a instanceof PlayerNumberChosen){
            pcsView.firePropertyChange("PlayerNumberChosen", null, a.getAnswer());
            return;
        }

        if(a instanceof ConnectionOutcome){
            pcsView.firePropertyChange("ConnectionOutcome", null, a);
            return;
        }

        if(a instanceof HowManyPlayersRequest){
            pcsView.firePropertyChange("HowManyPlayersRequest", null, a.getAnswer());
            return;
        }

        if(a instanceof UpdateTurn){
            pcsView.firePropertyChange("UpdateTurn", null, ((UpdateTurn) a).isYourTurn());
            return;
        }

        if(a instanceof ItsYourTurn){
            pcsView.firePropertyChange("ItsYourTurn", null, a.getAnswer());
            return;
        }

        if(a instanceof EndOfYourTurn){
            pcsView.firePropertyChange("EndOfYourTurn", null, a.getAnswer());
            return;
        }

        if(a instanceof GameReplica){
            modelView.updateGame(((GameReplica) a).getAnswer());
            return;
        }

        if(a instanceof CustomAnswer){
            pcsView.firePropertyChange("CustomAnswer", null, a.getAnswer());
            return;
        }

        if(a instanceof PlaceTilesRequest){
            pcsView.firePropertyChange("RequestToPlaceTiles", null, ((PlaceTilesRequest) a).getAnswer());
            return;
        }

        if(a instanceof PickTilesRequest){
            pcsView.firePropertyChange("PickTilesRequest", null, ((PickTilesRequest) a).getAnswer());
            return;
        }

        if(a instanceof BookShelfFilledWithTiles){
            pcsView.firePropertyChange("BookShelfFilledWithTiles", null, ((BookShelfFilledWithTiles) a).getAnswer());
            return;
        }
        if(a instanceof PrintCardsAnswer){
            pcsView.firePropertyChange("PrintCardsAnswer", null, a);
            return;
        }

        if(a instanceof ErrorAnswer){
            pcsView.firePropertyChange("ErrorAnswer", null, a);
            return;
        }

        if(a instanceof DisconnectPlayer){
            if(gui != null) {
            } else if(cli != null) {
                cli.endGameMessage();
            }
        }
    }


}
