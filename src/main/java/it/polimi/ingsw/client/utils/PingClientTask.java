package it.polimi.ingsw.client.utils;

import java.util.TimerTask;

public class PingClientTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("Network error ì, you will be disconnected");
        //System.exit(0);
    }
}
