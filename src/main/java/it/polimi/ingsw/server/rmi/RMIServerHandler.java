package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.client.rmi.IRMIClient;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.messages.CreateGameMessage;
import it.polimi.ingsw.communications.clientmessages.messages.JoinGameMessage;
import it.polimi.ingsw.communications.clientmessages.messages.UsernameSetup;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.RMICSConnection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class that implements all the methods that the server shows to the client (through RMI).
 */
public class RMIServerHandler extends UnicastRemoteObject implements IRMIServer {
    /**
     * Server reference.
     */
    private final Server server;

    /**
     * Generic connection reference.
     */
    private transient CSConnection connection;

    /**
     * Constructor.
     * @param server server reference
     * @throws RemoteException error during communication.
     */
    public RMIServerHandler(Server server) throws RemoteException {
        this.server = server;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void login(String username, IRMIClient client) {
        connection = new RMICSConnection(server, client);
        server.tryToConnect(username, connection);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void logout() {
        connection.disconnect();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessageToServer(SerializedMessage mess) throws RemoteException, InterruptedException {
        if (mess.message instanceof UsernameSetup) {
            server.tryToConnect(((UsernameSetup) mess.message).getUsername(), connection);
        } else if (mess.message instanceof CreateGameMessage) {
            server.createNewMatch(connection, ((CreateGameMessage) mess.message).getMatchName());
        } else if (mess.message instanceof JoinGameMessage) {
            server.joinMatch(((JoinGameMessage) mess.message).getMatchName(), connection);
        } else {
            server.onClientMessage(mess);
        }
    }
}
