package it.polimi.ingsw.communications.serveranswers;

public class ErrorAnswer implements Answer{

    private String answer;
    private final ErrorClassification errorClassification;


    /**
     * Constructor with only ErrorClassification object.
     * @param errorClassification
     */
    public ErrorAnswer(ErrorClassification errorClassification){
        this.errorClassification = errorClassification;
        this.answer = null;
    }


    /**
     * Constructor with both ErrorClassification and answer.
     * @param errorClassification
     */
    public ErrorAnswer(String answer, ErrorClassification errorClassification){
        this.errorClassification = errorClassification;
        this.answer = answer;
    }


    /**
     * Answer getter.
     * @return
     */
    public Object getAnswer(){
        return this.answer;
    }

    /**
     * Error classification getter.
     * @return
     */
    public ErrorClassification getError(){
        return this.errorClassification;
    }
}
