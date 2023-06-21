package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Player;

import java.io.Serializable;

/**
 * Abstract class that represent a generic Card type
 */
public abstract class Card implements Serializable {
    /**
     * ID of the card
     */
    protected int cardNumber;

    public int getCardNumber(){
        return this.cardNumber;
    }
    /**
     * Check the scheme to observe in order to get points.
     * @param player actual player
     * @return points achieved
     */
    public abstract Integer checkScheme(Player player);


    /**
     * Print the card.
     */
    public abstract void printCard();
}
