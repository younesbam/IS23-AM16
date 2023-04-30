package it.polimi.ingsw.client.socket;

import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.common.ClientConnection;
import it.polimi.ingsw.exceptions.TakenUsername;

import java.rmi.RemoteException;
import java.util.logging.Level;

public class SocketClientHandler extends ClientConnection {
    SocketClass socketClass;


    public SocketClientHandler(String address, int port, String username, ModelView modelView, ActionHandler actionHandler) throws RemoteException {
        super(address, port, username, modelView, actionHandler);
        socketClass = new SocketClass(address, port);
    }


    /**
     * {@inheritDoc}
     */
    public void connect(){
        try {
            if(!socketClass.setup(username, modelView, actionHandler)) {
                ClientConnection.LOGGER.log(Level.SEVERE, "The entered IP/port doesn't match any active server or the server is not running. Please try again!");
                CLI.main(null);
            }
            ClientConnection.LOGGER.log(Level.INFO, "Connection established!");
        } catch (TakenUsername e) {
            CLI.main(null);
        }
    }
}
