package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;

import java.util.*;

/**
 * Represent the black bag with all the shuffled cards and tiles inside.
 * @author Nicolo' Gandini
 */
public class Bag {
    public static final int MAXCARDS = 12;

    private Set<CommonGoalCard> initCommSet;
    private Set<PersonalGoalCard> initPersSet;
    private Queue<CommonGoalCard> commCards;
    private Queue<PersonalGoalCard> persCards;

    /**
     * Initialize all the tiles inside the bag into a Set.
     */
    public Bag() {
        initCommSet = new HashSet<>();
        initPersSet = new HashSet<>();

        /*
        Inizializzazione dei vari Set con elementi sparsi.
         */
        for(int i=0; i<MAXCARDS; i++){
            initPersSet.add(new PersonalGoalCard(i));
        }
        initCommSet.add(new EqualCross(1));
        initCommSet.add(new EqualCross(10));
        initCommSet.add(new DiffAligned(2));
        initCommSet.add(new DiffAligned(6));
        initCommSet.add(new EqualInCol(3));
        initCommSet.add(new EqualInCol(4));
        initCommSet.add(new MaxDiffGroup(5));
        initCommSet.add(new MaxDiffGroup(7));
        initCommSet.add(new EqualCorners(8));
        initCommSet.add(new EqualRand(9));
        initCommSet.add(new EqualDiag(11));
        initCommSet.add(new SubMatrix(12));

        /*
        Trasformazione del Set in una coda con priorità, disordinata.
         */
        commCards = new PriorityQueue<>(initCommSet);
        persCards = new PriorityQueue<>(initPersSet);
    }

    public CommonGoalCard pickCommonGoalCard(int playerNum) throws NullPointerException {
        CommonGoalCard comCard;
        comCard = commCards.poll();
        if(comCard == null) throw new NullPointerException("Common card's deck is empty");
        comCard.placePoints(playerNum);
        return comCard;
    }

    public PersonalGoalCard pickPersonalGoalCard(){
        return persCards.poll();
    }
}
