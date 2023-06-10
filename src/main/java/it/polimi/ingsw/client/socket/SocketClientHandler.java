package it.polimi.ingsw.client.socket;

import it.polimi.ingsw.client.*;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.common.Client;
import it.polimi.ingsw.communications.clientmessages.messages.Message;
import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
import it.polimi.ingsw.communications.clientmessages.messages.UsernameSetup;
import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
import it.polimi.ingsw.communications.serveranswers.info.ConnectionOutcome;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorAnswer;
import it.polimi.ingsw.communications.serveranswers.errors.ErrorClassification;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.common.exceptions.TakenUsername;
import it.polimi.ingsw.communications.serveranswers.requests.PingRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Level;

public class SocketClientHandler extends Client implements Runnable {
    private transient ObjectOutputStream outputStream;
    private transient ObjectInputStream inputStream;
    private transient Socket socket;
    private transient Thread messageReceiver;


    public SocketClientHandler(String address, int port, String username, ModelView modelView, ActionHandler actionHandler) throws RemoteException {
        super(address, port, username, modelView, actionHandler);
    }


    /**
     * {@inheritDoc}
     */
    public void connect(){
        try {
            if(!setup(username, modelView, actionHandler)) {
                Client.LOGGER.log(Level.SEVERE, "The entered IP/port doesn't match any active server or the server is not running. Please try again!");
                CLI.main(null);
            }
            //Client.LOGGER.log(Level.INFO, "Connection established!");
        } catch (TakenUsername e) {
            CLI.main(null);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        messageReceiver.interrupt();
        modelView.setConnected(false);
        try{
            inputStream.close();
            socket.close();
            System.exit(1);
        }catch (IOException e){
            System.out.println("Error occurred while closing connection.");
            e.printStackTrace();
        }
        System.out.println("Ending connection...");
    }


    /**
     * This method instantiates a new socket on client's side, establishing a connection with the server.
     *
     * @param username
     * @param modelView
     * @param actionHandler
     * @return
     * @throws TakenUsername
     */
    public boolean setup(String username, ModelView modelView, ActionHandler actionHandler) throws TakenUsername{
        try {
            System.out.println("Establishing a connection...\n");
            try {
                socket = new Socket(getAddress(), getPort());
            } catch (SocketException | UnknownHostException e) {
                return false;
            }
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            sendToServer(new UsernameSetup(username));

            modelView.setConnected(true);
            messageReceiver = new Thread(this);
            messageReceiver.start();
            return true;

        } catch (IOException e) {
            System.err.println("Connection establishment error! Shutting down...");
            System.exit(0);
            return false;
        }
    }


    /**
     * Method to put a client -> server communication on the socket output stream (and in doing so send it to the server).
     * @param c
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
     * Method to put a client -> server action communication on the socket output stream (and in doing so send it to the server).
     * @param a
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
     * Override of run() method.
     */
    public void run() {
        try {
            while (modelView.isConnected()) {
                SerializedAnswer serializedAnswer = (SerializedAnswer) inputStream.readObject();
                // If the request is a ping request
                if(serializedAnswer.getAnswer() instanceof PingRequest){
                    handlePingRequest();
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
