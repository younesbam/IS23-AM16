package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.client.rmi.IRMIClient;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.messages.CreateGameMessage;
import it.polimi.ingsw.communications.clientmessages.messages.JoinGameMessage;
import it.polimi.ingsw.communications.clientmessages.messages.UsernameSetup;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.RMICSConnection;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that implements all the methods that the server shows to the client (through RMI).
 */
public class RMIServerHandler extends UnicastRemoteObject implements IRMIServer {
    @Serial
    private static final long serialVersionUID = 7973004963846163594L;
    /**
     * Server reference.
     */
    private final Server server;

    /**
     * List of rmi connection reference.
     */
    private transient final List<RMICSConnection> connections;

    /**
     * Constructor.
     * @param server server reference
     * @throws RemoteException error during communication.
     */
    public RMIServerHandler(Server server) throws RemoteException {
        this.server = server;
        this.connections = new ArrayList<>();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void login(String username, IRMIClient client) {
        RMICSConnection rmiConnection = new RMICSConnection(server, client);
        connections.add(rmiConnection);
        server.tryToConnect(username, rmiConnection);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void logout(IRMIClient client) {
        RMICSConnection rmiConnection = getRMICSConnection(client);
        rmiConnection.disconnect();
        connections.remove(rmiConnection);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMessageToServer(IRMIClient client, SerializedMessage mess) throws RemoteException, InterruptedException {
        RMICSConnection rmiConnection = getRMICSConnection(client);
        if (mess.message instanceof UsernameSetup) {
            server.tryToConnect(((UsernameSetup) mess.message).getUsername(), rmiConnection);
        } else if (mess.message instanceof CreateGameMessage) {
            server.createNewMatch(rmiConnection, ((CreateGameMessage) mess.message).getMatchName());
        } else if (mess.message instanceof JoinGameMessage) {
            server.joinMatch(((JoinGameMessage) mess.message).getMatchName(), rmiConnection);
        } else {
            server.onClientMessage(mess);
        }
    }


    /**
     * Get RMI client-server connection by the client interface.
     * @param client client interface.
     * @return RMI client-server connection.
     */
    private RMICSConnection getRMICSConnection(IRMIClient client){
        List<RMICSConnection> connectionsCopy;
        synchronized (this){
            connectionsCopy = new ArrayList<>(connections);
        }
        for(RMICSConnection c : connectionsCopy){
            if(c.getClient().equals(client))
                return c;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RMIServerHandler that = (RMIServerHandler) o;
        return Objects.equals(server, that.server) &&
                Objects.equals(connections, that.connections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), server, connections);
    }
}
