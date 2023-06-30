package it.polimi.ingsw.communications.serveranswers;

import java.io.Serializable;

/**
 * Represents the interface for server's answers.
 */
public interface Answer extends Serializable {

    /**
     * Answer getter.
     * @return answer from the server.
     */
    Object getAnswer();
}
