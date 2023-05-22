package it.polimi.ingsw.communications.serveranswers;

public class WrongNum implements Answer{

    private String a;

    public WrongNum(){
        this.a = "numero sbagliatooooo";
    }

    @Override
    public Object getAnswer() {
        return a;
    }
}
