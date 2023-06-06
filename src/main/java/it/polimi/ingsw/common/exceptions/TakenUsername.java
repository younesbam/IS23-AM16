package it.polimi.ingsw.common.exceptions;

/**
 * This exception is thrown when a player tries to connect to a game with an username that is already taken.
 */
public class TakenUsername extends Exception{

    public String getMessage(){
        return "The username you've chosen is already taken!";
    }
}
