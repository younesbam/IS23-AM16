package it.polimi.ingsw.communications.serveranswers;

import java.io.Serializable;

/**
 * This class represents the interface for server's answers.
 */
public interface Answer extends Serializable {

    /**
     * This method returns the Answer's object attribute.
     * @return
     */
    Object getAnswer();
}
