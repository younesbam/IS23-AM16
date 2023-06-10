//package it.polimi.ingsw.client.socket;
//
//import it.polimi.ingsw.client.ActionHandler;
//import it.polimi.ingsw.client.ModelView;
//import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
//import it.polimi.ingsw.communications.serveranswers.requests.PingRequest;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.net.Socket;
//
///**
// * This class always listens for messages from server to client.
// */
//public class AnswerListener implements Runnable{
//
//    private final ObjectInputStream inputStream;
//    private final Socket socket;
//    private final ModelView modelView;
//    private final ActionHandler actionHandler;
//
//
//    /**
//     * Class constructor.
//     * @param socket
//     * @param modelView
//     * @param inputStream
//     * @param actionHandler
//     */
//    public AnswerListener(Socket socket, ModelView modelView, ObjectInputStream inputStream, ActionHandler actionHandler) {
//        this.modelView = modelView;
//        this.socket = socket;
//        this.inputStream = inputStream;
//        this.actionHandler = actionHandler;
//    }
//
//
//    /**
//     * This method ends the connection to the server.
//     */
//    public synchronized void endConnection(){
//        modelView.setConnected(false);
//        try{
//            inputStream.close();
//            socket.close();
//            System.exit(1);
//        }catch (IOException e){
//            System.out.println("Error occurred while closing connection.");
//            e.printStackTrace();
//        }
//        System.out.println("Ending connection...");
//    }
//
//
//    /**
//     * Override of run() method.
//     */
//    public void run() {
//        try {
//            while (modelView.isConnected()) {
//                SerializedAnswer serializedAnswer = (SerializedAnswer) inputStream.readObject();
//                // If the request is a ping request
//                if(serializedAnswer.getAnswer() instanceof PingRequest){
//                    hanlePingRequest();
//                } else {
//                    modelView.setAnswerFromServer(serializedAnswer.getAnswer());
//                    actionHandler.answerManager(modelView.getAnswerFromServer());
//                }
//            }
//        } catch (ClassNotFoundException | IOException e) {
//            System.out.println("A connection error occurred. Shutting down... ");
//            endConnection();
//        }
//    }
//}
