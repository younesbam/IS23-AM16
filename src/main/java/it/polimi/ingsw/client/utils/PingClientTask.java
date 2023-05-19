package it.polimi.ingsw.client.utils;

import java.util.TimerTask;

public class PingClientTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("client disconnesso, è stato avviato il PinkClientTask");
        /*
        Quui va avviato il disconnection listener per poter fare cose se si disconnette il client
         */
    }
}
