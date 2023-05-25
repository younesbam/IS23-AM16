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
import it.polimi.ingsw.exceptions.TakenUsername;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Level;

public class SocketClientHandler extends Client {
//    SocketClass socketClass;
    AnswerListener answerListener;
    private ObjectOutputStream outputStream;


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
    public void disconnect() throws RemoteException {
        answerListener.endConnection();
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
            Socket socket;
            try {
                socket = new Socket(getAddress(), getPort());
            } catch (SocketException | UnknownHostException e) {
                return false;
            }
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // TODO: Perchè aspettare qui che l'username sia libero? Il controllo viene già fatto nel server. Invio direttamente al server il messaggio che gli interessa di UsernameSetup
            sendToServer(new UsernameSetup(username));
//            while (true) {
//                if (readInput(username, input)) {
//                    break;
//                }
//            }

            modelView.setConnected(true);
            answerListener = new AnswerListener(socket, modelView, input, actionHandler);
            Thread thread = new Thread(answerListener);
            thread.start();
            return true;

        } catch (IOException e) {
            System.err.println("Connection establishment error! Shutting down...");
            System.exit(0);
            return false;
        }
    }


    /**
     * This method calls the sendToServer method to send the username choice to the server. The server will then write an answer on its stream based on the usarname choice.
     * Then the isUsernameFreeToUse() method is called to react to server's answer.
     * @param username
     * @param input
     * @return
     * @throws TakenUsername
     */
    private boolean readInput(String username, ObjectInputStream input) throws TakenUsername{
        try {
            sendToServer(new UsernameSetup(username));
            if (isUsernameFreeToUse(input.readObject())) {
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * This method reads server's answer to the player's username choice, and reacts by showing relevant messages on CLI.
     * @param answerToUsername
     * @return
     * @throws TakenUsername
     */
    public boolean isUsernameFreeToUse(Object answerToUsername) throws TakenUsername{

        SerializedAnswer answer = (SerializedAnswer) answerToUsername;

        if (answer.getAnswer() instanceof ConnectionOutcome
                && ((ConnectionOutcome) answer.getAnswer()).isConnected()) {
            return true;
        } else if (answer.getAnswer() instanceof ErrorAnswer) {
            if (((ErrorAnswer) answer.getAnswer()).getError().equals(ErrorClassification.TAKEN_USERNAME)) {
                System.err.println("This nickname is already in use! Please choose one other.");
                throw new TakenUsername();
            } else if (((ErrorAnswer) answer.getAnswer()).getError().equals(ErrorClassification.MAX_PLAYERS_REACHED)) {
                System.err.println(
                        "This match is already full, please try again later!\nApplication will now close...");
                System.exit(0);
            }
        }

        return false;
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

}
