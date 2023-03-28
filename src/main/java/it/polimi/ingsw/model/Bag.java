package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;

import java.util.*;

/**
 * Represent the black bag with all the shuffled cards and tiles inside.
 * @author Nicolo' Gandini
 */
public class Bag {
    public static final int MAXTILES = 22;
    public static final int MAXCOMMCARD = 12;
    public static final int MAXPERSCARD = 12;

    private Set<Tile> initTileSet;
    private Set<CommonGoalCard> initCommSet;
    private Set<PersonalGoalCard> initPersSet;
    private Queue<Tile> tiles;
    private Queue<CommonGoalCard> commCards;
    private Queue<PersonalGoalCard> persCards;

    /**
     * Initialize all the tiles inside the bag into a Set.
     */
    public Bag(int playerNum) {
        initTileSet = new HashSet<>();
        initCommSet = new HashSet<>();
        initPersSet = new HashSet<>();

        /*
        Inizializzazione dei vari Set con elementi sparsi.
         */
        for(Tile type : Tile.values())
            for(int i=0; i<MAXTILES; i++)
                initTileSet.add(type);
        for(int i=0; i<MAXPERSCARD; i++){
            initPersSet.add(new PersonalGoalCard(i));
        }
        initCommSet.add(new CommonGoalCard1(playerNum, 1));
        initCommSet.add(new CommonGoalCard1(playerNum, 10));
        initCommSet.add(new CommonGoalCard2(playerNum, 2));
        initCommSet.add(new CommonGoalCard2(playerNum, 6));
        initCommSet.add(new CommonGoalCard3(playerNum, 3));
        initCommSet.add(new CommonGoalCard3(playerNum, 4));
        initCommSet.add(new CommonGoalCard4(playerNum, 5));
        initCommSet.add(new CommonGoalCard4(playerNum, 7));
        initCommSet.add(new CommonGoalCard5(playerNum, 8));
        initCommSet.add(new CommonGoalCard6(playerNum, 9));
        initCommSet.add(new CommonGoalCard7(playerNum, 11));
        initCommSet.add(new CommonGoalCard8(playerNum, 12));

        /*
        Trasformazione del Set in una coda con priorità, disordinata.
         */
        tiles = new PriorityQueue<>(initTileSet);
        commCards = new PriorityQueue<>(initCommSet);
        persCards = new PriorityQueue<>(initPersSet);
    }

    /**
     * This method is used to pick a tile in order to refill the game board.
     * Note that this method removes the Object from the queue.
     * @return Enumeration of type Tile
     */
    public Tile pickTile() {
        return tiles.poll();
    }

    public CommonGoalCard pickCommonGoalCard(){
        return commCards.poll();
    }

    public PersonalGoalCard pickPersonalGoalCard(){
        return persCards.poll();
    }
}
