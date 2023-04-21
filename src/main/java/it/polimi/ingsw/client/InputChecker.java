package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;

/**
 * This class is used to check if the action performed by the user is actually feasible and, if so, returns the action to the dispatcher.
 */
public class InputChecker {

    private final CLI cli;
    private final ModelView modelView;
    private final SocketClass socketClass;


    /**
     * Class constructor, only for CLI use.
     * @param modelView
     * @param cli
     * @param socketClass
     */
    public InputChecker(CLI cli, ModelView modelView, SocketClass socketClass) {
        this.cli = cli;
        this.modelView = modelView;
        this.socketClass = socketClass;
    }





}
