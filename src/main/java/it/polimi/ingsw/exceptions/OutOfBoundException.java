package it.polimi.ingsw.exceptions;

/**
 * This exception is thrown when a limit of a range is exceeded.
 */
public class OutOfBoundException extends Exception{

    public String getMessage(){
        return ("An error has occurred: ");
    }
}
