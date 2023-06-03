package it.polimi.ingsw.client.utils;

import java.util.TimerTask;

public class PingClientTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("Il server non è riuscito a pingare questo client. Ciao");
        /*
        Quui va avviato il disconnection listener per poter fare cose se si disconnette il client. Per ora lo spengo.
         */
    }
}
