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

import java.io.Serial;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.logging.Level;

import static it.polimi.ingsw.Const.RED_COLOR;
import static it.polimi.ingsw.Const.RESET_COLOR;

/**
 * Class that implements all the methods that the client shows to the server (through RMI).
 */
public class RMIClientHandler extends Client implements IRMIClient {
    @Serial
    private static final long serialVersionUID = -6701767031892802332L;
    /**
     * Server interface.
     */
    private transient IRMIServer serverInterface;


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
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() throws RemoteException, NotBoundException {
        try {
            System.setProperty("java.rmi.server.hostname", getAddress());
            Registry registry = LocateRegistry.getRegistry(getAddress(), getPort());
            serverInterface = (IRMIServer) registry.lookup("MyShelfieServer");
            serverInterface.login(getUsername(), this);
            modelView.setConnected(true);
        }catch(IllegalArgumentException e){
            System.out.println(RED_COLOR + "Number of port out of bound. Please try again. Shutting down..." + RESET_COLOR);
            System.exit(0);
        }catch (ConnectException e){
            System.out.println(RED_COLOR + "Can't connect to server. Please try again. Shutting down..." + RESET_COLOR);
            System.exit(0);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() throws RemoteException {
        serverInterface.logout();
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
            serverInterface.sendMessageToServer(serializedMessage);
        } catch (RemoteException | InterruptedException e){
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
            serverInterface.sendMessageToServer(serializedMessage);
        } catch (RemoteException | InterruptedException e){
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
        serverInterface = null;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RMIClientHandler clientRMI = (RMIClientHandler) o;
        return Objects.equals(serverInterface, clientRMI.serverInterface);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), serverInterface);
    }
}
