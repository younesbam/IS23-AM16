package it.polimi.ingsw.controller;
import it.polimi.ingsw.model.*;

public class Controller {
    private Game game;
    private TurnController turnController;

    public Controller(){
        this.game = new Game();
        this.turnController = new TurnController(game, this);
    }

    public Game getGame() {
        return game;
    }

    public TurnController getTurnController() {
        return turnController;
    }

    private void setup(){

    }

    public void propertyChange(){

    }
}
