package it.polimi.ingsw.model;

import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.model.cards.*;

import java.util.*;

/**
 * Represent the black bag with all the shuffled cards inside.
 * @author Nicolo' Gandini
 */
public class Bag {


    private Set<CommonGoalCard> initCommSet;
    private List<CommonGoalCard> initCommList;
    private Set<PersonalGoalCard> initPersSet;

    /**
     * Queue used to represent common goal cards
     */
    private Queue<CommonGoalCard> commCards;

    /**
     * Queue used to represent the personal goal cards
     */
    private Queue<PersonalGoalCard> persCards;

    /**
     * Initialize all the tiles inside the bag into a Set, in order to have an unordered collection.
     * Then transform the Set to a Queue, in a random order.
     */
    public Bag() {
        // Create Sets to insert random cards.
        initCommSet = new HashSet<>();
        initPersSet = new HashSet<>();

        initCommList = new ArrayList<>();

        /*
        Common card initialization
         */
        // Add common cards in the Set
        initCommList.add(new EqualCross(1));
        initCommList.add(new EqualCross(10));
        initCommList.add(new DiffAligned(2));
        initCommList.add(new DiffAligned(6));
        initCommList.add(new EqualInCol(3));
        initCommList.add(new EqualInCol(4));
        initCommList.add(new MaxDiffGroup(5));
        initCommList.add(new MaxDiffGroup(7));
        initCommList.add(new EqualCorners(8));
        initCommList.add(new EqualRand(9));
        initCommList.add(new EqualDiag(11));
        initCommList.add(new SubMatrix(12));

        /*
        Personal card read from json
         */
        JSONParser jsonParser = new JSONParser("json/initSetup.json");
        initPersSet = jsonParser.getPersonalGoalCards();

        /*
        Final operations
         */
        // Transform the Set into priority queue. Useful to poll the first element.
        // TODO: dà errore quando trasformo il set in una queue perchè rompe qualcosa di Comparable. Non va manco la lista.
        commCards = new PriorityQueue<>();
        persCards = new PriorityQueue<>();
    }


    /**
     * Pick from the deck a random common goal card
     * @param playerNum number of players in the actual game
     * @return common goal card
     * @throws NullPointerException
     */
    public CommonGoalCard pickCommonGoalCard(int playerNum) throws NullPointerException {
        CommonGoalCard comCard;
        comCard = commCards.poll();
        if(comCard == null) throw new NullPointerException("Common card's deck is empty");
        comCard.placePoints(playerNum);
        return comCard;
    }


    /**
     * Pick from the deck a random personal goal card
     * @return personal goal card
     * @throws NullPointerException
     */
    public PersonalGoalCard pickPersonalGoalCard() throws NullPointerException {
        PersonalGoalCard persCard;
        persCard = persCards.poll();
        if(persCard == null) throw new NullPointerException("Personal card's deck is empty");
        return persCard;
    }
}
