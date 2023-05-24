package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.client.rmi.IRMIClient;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.RMICSConnection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerHandler extends UnicastRemoteObject implements IRMIServer {
    private final Server server;
    private transient CSConnection connection;
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
//        SerializedMessage serializedMessage = new SerializedMessage(username, new UsernameSetup(username));
//        Server.LOGGER.log(Level.INFO, "Connection with " + username + " established");
//        try{
//            sendMessageToServer(serializedMessage);
//            //server.newClientRegistration(username, connection);
//        }catch (IOException e){
//            Server.LOGGER.log(Level.SEVERE, "RMI: failed to login", e);
//        }
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
    public void sendMessageToServer(SerializedMessage message) throws RemoteException {
        server.onClientMessage(message);
    }
}
