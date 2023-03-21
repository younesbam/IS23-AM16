package it.polimi.ingsw.model;

import java.util.*;

public class Bag {
    public static final int MAXBLUE = 22;
    public static final int MAXPINK = 22;
    public static final int MAXWHITE = 22;
    public static final int MAXGREEN = 22;
    public static final int MAXYELLOW = 22;
    public static final int MAXLIGHTBLUE = 22;

    private Set<ObjectTile> initSet;
    private Queue<ObjectTile> objectTiles;

    /**
     * Initialize all the tiles inside the bag into a Set.
     */
    public Bag {
        initSet = new HashSet<>();

        for(int i=0; i<MAXBLUE; i++){
            initSet.add(new ObjectTile(BLUE));
        }
        for(int i=0; i<MAXPINK; i++){
            initSet.add(new ObjectTile(PINK));
        }
        for(int i=0; i<MAXWHITE; i++){
            initSet.add(new ObjectTile(WHITE));
        }
        for(int i=0; i<MAXGREEN; i++){
            initSet.add(new ObjectTile(GREEN));
        }
        for(int i=0; i<MAXYELLOW; i++){
            initSet.add(new ObjectTile(YELLOW));
        }
        for(int i=0; i<MAXLIGHTBLUE; i++){
            initSet.add(new ObjectTile(LIGHTBLUE));
        }

        objectTiles = new PriorityQueue<>(initSet);
    }

    /**
     * This method re-initialize the tiles' queue. Used when a new game is starting to refill the virtual bag.
     */
    public void init(){
        objectTiles = new PriorityQueue<>(initSet);
    }

    /**
     * This method is used to pick a tile in order to refill the game board.
     * Note that this method remove from the Object from the queue.
     * @return ObjectTile
     */
    public ObjectTile pickObjectTile() {
        return objectTiles.poll();
    }
}
