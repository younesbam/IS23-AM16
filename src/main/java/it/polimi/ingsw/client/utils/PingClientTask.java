package it.polimi.ingsw.client.utils;

import java.util.TimerTask;

public class PingClientTask extends TimerTask {
    @Override
    public void run() {
        System.out.println("Network error, you will be disconnected in a second. \n" +
                "No panic: you can connect again with the same username and continue the game without loosing earned points");
        System.exit(0);
    }
}
