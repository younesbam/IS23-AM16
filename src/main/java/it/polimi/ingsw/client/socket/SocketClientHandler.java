package it.polimi.ingsw.client.socket;

import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.communications.clientmessages.messages.Message;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.messages.UsernameSetup;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.communications.serveranswers.network.PingRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * Socket handler.
 */
public class SocketClientHandler extends Client implements Runnable {
    /**
     * Output stream.
     */
    private transient ObjectOutputStream outputStream;

    /**
     * Input stream.
     */
    private transient ObjectInputStream inputStream;

    /**
     * Socket representation.
     */
    private transient Socket socket;

    /**
     * Message receiver thread.
     */
    private transient Thread messageReceiver;


    /**
     * Class constructor.
     * @param address IP address of the client.
     * @param port of the client.
     * @param username of the client.
     * @param modelView representation of the model.
     * @param actionHandler server answer handler.
     * @throws RemoteException
     */
    public SocketClientHandler(String address, int port, String username, ModelView modelView, ActionHandler actionHandler) throws RemoteException {
        super(address, port, username, modelView, actionHandler);
    }


    /**
     * {@inheritDoc}
     */
    public void connect(){
        try {
            setup();
        } catch (IOException e) {
            System.err.println("Connection establishment error! Check the parameters and try again. Shutting down...");
            System.exit(-1);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        messageReceiver.interrupt();
        modelView.setConnected(false);
        deactivatePingTimeout();
        try{
            inputStream.close();
            socket.close();
        }catch (IOException e){
            System.out.println("Error occurred while closing connection.");
        }
        System.out.println("Ending connection...");
    }


    /**
     * Instantiates a new socket on client's side, establishing a connection with the server.
     */
    private void setup() throws IOException {
        System.out.println("Establishing a connection...\n");
        socket = new Socket(getAddress(), getPort());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());

        sendToServer(new UsernameSetup(username));

        modelView.setConnected(true);
        messageReceiver = new Thread(this);
        messageReceiver.start();
    }


    /**
     * {@inheritDoc}
     */
    public void sendToServer(Message c) {
        SerializedMessage userInput = new SerializedMessage(getID(), c);
        try {
            outputStream.reset();
            outputStream.writeObject(userInput);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error during send process.");
            System.err.println(e.getMessage());
        }
    }


    /**
     * {@inheritDoc}
     */
    public void sendToServer(GameAction a) {
        SerializedMessage userInput = new SerializedMessage(getID(), a);
        try {
            outputStream.reset();
            outputStream.writeObject(userInput);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error during send process.");
        }
    }


    /**
     * Listen for new socket messages through the stream.
     */
    public void run() {
        try {
            while (modelView.isConnected()) {
                SerializedAnswer serializedAnswer = (SerializedAnswer) inputStream.readObject();
                // If the request is a ping request
                if(serializedAnswer.getAnswer() instanceof PingRequest){
                    activatePingTimeout();
                } else {
                    modelView.setAnswerFromServer(serializedAnswer.getAnswer());
                    actionHandler.answerManager(modelView.getAnswerFromServer());
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("A connection error occurred. Shutting down... ");
            disconnect();
        }
    }

}
