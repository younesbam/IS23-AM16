package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;

import java.util.*;

/**
 * Represent the black bag with all the shuffled cards and tiles inside.
 * @author Nicolo' Gandini
 */
public class Bag {
    public static final int MAXCOMMCARD = 12;
    public static final int MAXPERSCARD = 12;

    private Set<CommonGoalCard> initCommSet;
    private Set<PersonalGoalCard> initPersSet;
    private Queue<CommonGoalCard> commCards;
    private Queue<PersonalGoalCard> persCards;

    /**
     * Initialize all the tiles inside the bag into a Set.
     */
    public Bag(int playerNum) {
        initCommSet = new HashSet<>();
        initPersSet = new HashSet<>();

        /*
        Inizializzazione dei vari Set con elementi sparsi.
         */
        for(int i=0; i<MAXPERSCARD; i++){
            initPersSet.add(new PersonalGoalCard(i));
        }
        initCommSet.add(new EqualCross(playerNum, 1));
        initCommSet.add(new EqualCross(playerNum, 10));
        initCommSet.add(new DiffAligned(playerNum, 2));
        initCommSet.add(new DiffAligned(playerNum, 6));
        initCommSet.add(new EqualInCol(playerNum, 3));
        initCommSet.add(new EqualInCol(playerNum, 4));
        initCommSet.add(new MaxDiffGroup(playerNum, 5));
        initCommSet.add(new MaxDiffGroup(playerNum, 7));
        initCommSet.add(new EqualCorners(playerNum, 8));
        initCommSet.add(new EqualRand(playerNum, 9));
        initCommSet.add(new EqualDiag(playerNum, 11));
        initCommSet.add(new SubMatrix(playerNum, 12));

        /*
        Trasformazione del Set in una coda con priorità, disordinata.
         */
        commCards = new PriorityQueue<>(initCommSet);
        persCards = new PriorityQueue<>(initPersSet);
    }

    public CommonGoalCard pickCommonGoalCard(){
        return commCards.poll();
    }

    public PersonalGoalCard pickPersonalGoalCard(){
        return persCards.poll();
    }
}
