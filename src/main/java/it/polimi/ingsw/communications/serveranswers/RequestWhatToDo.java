package it.polimi.ingsw.communications.serveranswers;

public class RequestWhatToDo implements Answer{

    public String request;

    public RequestWhatToDo(){
        this.request = "What do you want to do?\n1. Pick tiles from the board.\n2. See your personal goal card.\n3. See the common goal card.\n\nPlease enter the number corresponding to the action that you want to perform.\n>\n";
    }


    /**
     * Request getter.
     * @return
     */
    public String getAnswer() {
        return request;
    }
}
