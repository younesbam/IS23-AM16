package it.polimi.ingsw.client.utils;

import java.util.TimerTask;

import static it.polimi.ingsw.Const.RED_COLOR;
import static it.polimi.ingsw.Const.RESET_COLOR;

/**
 * Run this task if the server can't ping this client.
 */
public class ConnectionTimeoutTask extends TimerTask {
    @Override
    public void run() {
        System.out.println(RED_COLOR + "Can't connect to server. Shut down... \n" + RESET_COLOR);
        System.exit(0);
    }
}
