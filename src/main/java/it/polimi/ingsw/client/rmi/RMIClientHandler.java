package it.polimi.ingsw.client.rmi;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.ModelView;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.communications.clientmessages.messages.Message;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.rmi.IRMIServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;

/**
 * Class that implements all the methods that the client shows to the server (through RMI).
 */
public class RMIClientHandler extends Client implements IRMIClient {
    /**
     * Server interface.
     */
    private transient IRMIServer server;

    /**
     * Parameters from JSON file.
     */
    private final JSONParser jsonParser;


    /**
     * Class constructor.
     * @param address IP address of the client.
     * @param port of the client.
     * @param username of the client.
     * @param modelView representation of the model.
     * @param actionHandler server answer handler.
     * @throws RemoteException
     */
    public RMIClientHandler(String address, int port, String username, ModelView modelView, ActionHandler actionHandler) throws RemoteException {
        super(address, port, username, modelView, actionHandler);
        jsonParser = new JSONParser("network.json");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws RemoteException, NotBoundException {
        System.setProperty("java.rmi.server.hostname", getAddress());
        Registry registry = LocateRegistry.getRegistry(getAddress(), getPort());
        server = (IRMIServer) registry.lookup(jsonParser.getServerName());
        server.login(getUsername(), this);
        modelView.setConnected(true);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() throws RemoteException {
        server.logout();
        disconnectMe();
        deactivatePingTimeout();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendToServer(Message message) {
        SerializedMessage serializedMessage = new SerializedMessage(getID() ,message);
        try{
            server.sendMessageToServer(serializedMessage);
        } catch (RemoteException e){
            Client.LOGGER.log(Level.SEVERE, "Failed to send message to server", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void sendToServer(GameAction gameAction) {
        SerializedMessage serializedMessage = new SerializedMessage(getID(), gameAction);
        try{
            server.sendMessageToServer(serializedMessage);
        } catch (RemoteException e){
            Client.LOGGER.log(Level.SEVERE, "Failed to send message to server", e);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() throws RemoteException {
        activatePingTimeout();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectMe() throws RemoteException {
        server = null;
        modelView.setConnected(false);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onServerAnswer(SerializedAnswer answer) throws RemoteException {
        modelView.setAnswerFromServer(answer.getAnswer());
        actionHandler.answerManager(modelView.getAnswerFromServer());
    }
}
