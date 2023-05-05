package it.polimi.ingsw.client;

import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.common.Connection;

/**
 * This class is used to check if the action performed by the user is actually feasible and, if so, returns the action to the dispatcher.
 */
public class InputChecker {

    private final CLI cli;
    private final ModelView modelView;
    private final Connection clientConnection;


    /**
     * Class constructor, only for CLI use.
     * @param cli
     * @param modelView
     * @param clientConnection
     */
    public InputChecker(CLI cli, ModelView modelView, Connection clientConnection) {
        this.cli = cli;
        this.modelView = modelView;
        this.clientConnection = clientConnection;
    }
}
