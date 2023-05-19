//package it.polimi.ingsw.client.socket;
//
//import it.polimi.ingsw.client.ActionHandler;
//import it.polimi.ingsw.client.socket.AnswerListener;
//import it.polimi.ingsw.client.ModelView;
//import it.polimi.ingsw.communications.clientmessages.messages.UsernameSetup;
//import it.polimi.ingsw.communications.clientmessages.actions.GameAction;
//import it.polimi.ingsw.communications.clientmessages.messages.Message;
//import it.polimi.ingsw.communications.clientmessages.SerializedMessage;
//import it.polimi.ingsw.communications.serveranswers.ConnectionOutcome;
//import it.polimi.ingsw.communications.serveranswers.ErrorAnswer;
//import it.polimi.ingsw.communications.serveranswers.ErrorClassification;
//import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
//import it.polimi.ingsw.exceptions.TakenUsername;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//
//
////STA CLASSE è DIVENTATA INUTILE, è DA TOGLIERE!!! SI FA TUTTO IN SOCKETCLIENTHANDLER!
//
//public class SocketClass {
//    private final int numOfPort;
//    private final String IPAddress;
//    AnswerListener answerListener;
//    private ObjectOutputStream outputStream;
//
//
//    /**
//     * Class constructor.
//     */
//    public SocketClass(String ipAddress, int numOfPort){
//        this.IPAddress = ipAddress;
//        this.numOfPort = numOfPort;
//    }
//
//
//    /**
//     * This method instantiates a new socket on client's side, establishing a connection with the server.
//     *
//     * @param username
//     * @param modelView
//     * @param actionHandler
//     * @return
//     * @throws TakenUsername
//     */
//    public boolean setup(String username, ModelView modelView, ActionHandler actionHandler) throws TakenUsername{
//        try {
//            System.out.println("Establishing a connection...");
//            Socket socket;
//            try {
//                socket = new Socket(IPAddress, numOfPort);
//            } catch (SocketException | UnknownHostException e) {
//                return false;
//            }
//            outputStream = new ObjectOutputStream(socket.getOutputStream());
//            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
//
//            while (true) {
//                if (readInput(username, input)) {
//                    break;
//                }
//            }
//
//            modelView.setConnected(true);
//            answerListener = new AnswerListener(socket, modelView, input, actionHandler);
//            Thread thread = new Thread(answerListener);
//            thread.start();
//            return true;
//
//        } catch (IOException e) {
//            System.err.println("Connection establishment error! Shutting down...");
//            System.exit(0);
//            return false;
//        }
//    }
//
//    /**
//     * This method calls the sendToServer method to send the username choice to the server. The server will then write an answer on its stream based on the usarname choice.
//     * Then the isUsernameFreeToUse() method is called to react to server's answer.
//     * @param username
//     * @param input
//     * @return
//     * @throws TakenUsername
//     */
//    private boolean readInput(String username, ObjectInputStream input) throws TakenUsername{
//        try {
//            sendToServer(new UsernameSetup(username));
//            if (isUsernameFreeToUse(input.readObject())) {
//                return true;
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            System.err.println(e.getMessage());
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * This method reads server's answer to the player's username choice, and reacts by showing relevant messages on CLI.
//     * @param answerToUsername
//     * @return
//     * @throws TakenUsername
//     */
//    public boolean isUsernameFreeToUse(Object answerToUsername) throws TakenUsername{
//
//        SerializedAnswer answer = (SerializedAnswer) answerToUsername;
//
//        if (answer.getAnswer() instanceof ConnectionOutcome
//                && !((ConnectionOutcome) answer.getAnswer()).isConnected()) {
//            return true;
//        } else if (answer.getAnswer() instanceof ErrorAnswer) {
//            if (((ErrorAnswer) answer.getAnswer()).getError().equals(ErrorClassification.TAKENUSERNAME)) {
//                System.err.println("This nickname is already in use! Please choose one other.");
//                throw new TakenUsername();
//            }
//            } else if (((ErrorAnswer) answer.getAnswer()).getError().equals(ErrorClassification.MAXPLAYERSREACHED)) {
//                System.err.println(
//                        "This match is already full, please try again later!\nApplication will now close...");
//                System.exit(0);
//            }
//
//        return false;
//    }
//
//    /**
//     * Method to put a client -> server communication on the socket output stream (and in doing so send it to the server).
//     * @param c
//     */
//    public void sendToServer(Message c) {
//        SerializedMessage userInput = new SerializedMessage(c);
//        try {
//            outputStream.reset();
//            outputStream.writeObject(userInput);
//            outputStream.flush();
//        } catch (IOException e) {
//            System.err.println("Error during send process.");
//            System.err.println(e.getMessage());
//        }
//    }
//
//
//    /**
//     * Method to put a client -> server action communication on the socket output stream (and in doing so send it to the server).
//     * @param a
//     */
//    public void sendToServer(GameAction a) {
//        SerializedMessage userInput = new SerializedMessage(a);
//        try {
//            outputStream.reset();
//            outputStream.writeObject(userInput);
//            outputStream.flush();
//        } catch (IOException e) {
//            System.err.println("Error during send process.");
//        }
//    }
//
//
//}
