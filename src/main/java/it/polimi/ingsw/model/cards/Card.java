package it.polimi.ingsw.model.cards;

import java.io.Serializable;

/**
 * Abstract class that represent a generic Card type
 * @author Nicolo' Gandini
 */
public abstract class Card implements Serializable {
    /**
     * Id of the card
     */
    protected int cardNumber;
}
