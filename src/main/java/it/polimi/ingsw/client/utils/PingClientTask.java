package it.polimi.ingsw.client.utils;

import it.polimi.ingsw.client.common.Client;

import java.util.TimerTask;

import static it.polimi.ingsw.Const.RED_COLOR;
import static it.polimi.ingsw.Const.RESET_COLOR;

/**
 * Run this task if the server can't ping this client.
 */
public class PingClientTask extends TimerTask {
    @Override
    public void run() {
        System.out.println(RED_COLOR + "Network error, you will be disconnected in a second. \n" +
                "No panic: you can connect again with the same username and continue the game without loosing earned points" + RESET_COLOR);
        System.exit(0);
    }
}
