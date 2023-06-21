package it.polimi.ingsw.model;

import it.polimi.ingsw.common.JSONParser;
import it.polimi.ingsw.model.cards.*;

import java.io.Serializable;
import java.util.*;

import static java.util.Collections.shuffle;

/**
 * Represent the black bag with all the shuffled cards inside.
 * @author Nicolo' Gandini
 */
public class Bag implements Serializable {
    /**
     * List of common goal cards.
     */
    private final LinkedList<CommonGoalCard> commList;
    /**
     * List of personal goal cards.
     */
    private LinkedList<PersonalGoalCard> persList;


    /**
     * Initialize all the tiles inside the bag to have an unordered collection.
     */
    public Bag() {

        commList = new LinkedList<>();
        persList = new LinkedList<>();

        /*
        Common card initialization
         */
        // Add common cards in the Set
        commList.add(new EqualCross(1));
        commList.add(new EqualCross(10));
        commList.add(new DiffAligned(2));
        commList.add(new DiffAligned(6));
        commList.add(new EqualInCol(3));
        commList.add(new EqualInCol(4));
        commList.add(new MaxDiffGroup(5));
        commList.add(new MaxDiffGroup(7));
        commList.add(new EqualCorners(8));
        commList.add(new EqualRand(9));
        commList.add(new EqualDiag(11));
        commList.add(new SubMatrix(12));

        /*
        Personal card read from json
         */
        JSONParser jsonParser = new JSONParser("json/initSetup.json");
        persList = jsonParser.getPersonalGoalCards();

        // Shuffle the cards
        shuffle(commList);
        shuffle(persList);

    }


    /**
     * Pick from the deck a random common goal card
     * @param playerNum number of players in the actual game
     * @return common goal card
     * @throws NullPointerException no cards available.
     */
    public CommonGoalCard pickCommonGoalCard(int playerNum) throws NullPointerException {
        CommonGoalCard comCard;
        comCard = commList.poll();
        if(comCard == null) throw new NullPointerException("Common card's deck is empty");
        comCard.placePoints(playerNum);
        return comCard;
    }


    /**
     * Pick from the deck a random personal goal card
     * @return personal goal card
     * @throws NullPointerException no cards available.
     */
    public PersonalGoalCard pickPersonalGoalCard() throws NullPointerException {
        PersonalGoalCard persCard;
        persCard = persList.poll();
        if(persCard == null) throw new NullPointerException("Personal card's deck is empty");
        return persCard;
    }
}
