package it.polimi.ingsw.model;

import it.polimi.ingsw.Utils;
import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.model.cards.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static it.polimi.ingsw.model.BookShelf.MAXBOOKSHELFCOL;
import static it.polimi.ingsw.model.BookShelf.MAXBOOKSHELFROW;

/**
 * Represent the black bag with all the shuffled cards inside.
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
        // Create Sets to insert random cards.
        initCommSet = new HashSet<>();
        initPersSet = new HashSet<>();

        /*
        Common card initialization
         */
        // Add common cards in the Set
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
        Personal card read from json
         */
        JSONParser jsonParser = new JSONParser("json/initSetup.json");
        initPersSet = jsonParser.getPersonalGoalCards();

        /*
        Final operations
         */
        // Transform the Set into priority queue. Useful to poll the first element.
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
