package it.polimi.ingsw.communications.serveranswers;

import java.io.Serializable;

/**
 * This class contains the server's answer to the client, as a serializable type of object.
 */
public class SerializedAnswer implements Serializable {
    private Answer answer;

    /**
     * Answer setter.
     * @param a
     */
    public void setAnswer(Answer a) {
        this.answer = a;
    }

    /**
     * Answer getter.
     */
    public Answer getAnswer() {
        return this.answer;
    }
}
