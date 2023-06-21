package it.polimi.ingsw.communications.serveranswers;

import java.io.Serializable;

/**
 * Server's answer to the client, as a serializable type of object.
 */
public class SerializedAnswer implements Serializable {
    private Answer answer;

    /**
     * Answer setter.
     * @param a answer to be sent to client.
     */
    public void setAnswer(Answer a) {
        this.answer = a;
    }

    /**
     * Answer getter.
     * @return answer from the server.
     */
    public Answer getAnswer() {
        return this.answer;
    }
}
