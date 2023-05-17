package it.polimi.ingsw.server.rmi;

import it.polimi.ingsw.client.rmi.IRMIClient;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.messages.UsernameSetup;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.connection.CSConnection;
import it.polimi.ingsw.server.connection.RMICSConnection;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;

public class RMIServerHandler extends UnicastRemoteObject implements IRMIServer {
    private final Server server;
    private CSConnection connection;
    public RMIServerHandler(Server server) throws RemoteException {
        this.server = server;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void login(String username, IRMIClient client) {
        connection = new RMICSConnection(server, client);
        SerializedMessage serializedMessage = new SerializedMessage(new UsernameSetup(username));
        Server.LOGGER.log(Level.INFO, "Connection with " + username + " established");
        try{
            sendMessageToServer(serializedMessage);
            //server.newClientRegistration(username, connection);
            Server.LOGGER.log(Level.INFO, username + " has successfully registered in the server");
        }catch (IOException e){
            Server.LOGGER.log(Level.SEVERE, "RMI: failed to login", e);
        }
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
        connection.onClientMessage(message);
    }
}
