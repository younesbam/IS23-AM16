package it.polimi.ingsw.server.connection;

import it.polimi.ingsw.client.rmi.IRMIClient;
import it.polimi.ingsw.communications.serveranswers.Answer;
import it.polimi.ingsw.communications.serveranswers.HowManyPlayersRequest;
import it.polimi.ingsw.communications.serveranswers.SerializedAnswer;
import it.polimi.ingsw.server.Server;

import java.rmi.RemoteException;
import java.util.logging.Level;

/**
 * Represent RMI connection to a client
 * @author Nicolo' Gandini
 */
public class RMICSConnection extends CSConnection {
    /**
     * Client reference.
     */
    private final IRMIClient client;

    public RMICSConnection(Server server, IRMIClient client) {
        this.server = server;
        this.client = client;
        this.alive = true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void ping() {
        try {
            client.ping();
        }catch (RemoteException e){
            Server.LOGGER.log(Level.WARNING, "Disconnect directive. No response from client", e);
            disconnect();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        if(this.alive){
            try {
                client.disconnectMe();
                Server.LOGGER.log(Level.INFO, "Client successfully disconnected");
            }catch (RemoteException e){
                Server.LOGGER.log(Level.WARNING, "Failed to disconnect the client", e);
            }
            this.alive = false;
            server.removePlayer(getID());
        }
    }


    /**
     * {@inheritDoc}
     */
    public void sendAnswerToClient(SerializedAnswer answer) {
        try{
            client.onServerAnswer(answer);
        } catch (RemoteException e) {
            Server.LOGGER.log(Level.SEVERE, "Failed to send message to the client: ", e);
            disconnect();
        }
    }

//    @Override
//    public void setupPlayers(HowManyPlayersRequest request) {
//        SerializedAnswer answer = new SerializedAnswer();
//        answer.setAnswer(request);
//        /*
//        Send the answer to the client
//         */
//        try{
//            sendAnswerToClient(answer);
//        } catch (RemoteException e){
//            Server.LOGGER.log(Level.SEVERE, "Failed to request the client the number of players in the game", e);
//            disconnect();
//        }
//
//        /*
//        Wait for the client response. The server must have the numbers of players.
//         */
//
//
//    }
}
