package it.polimi.ingsw.model;

import it.polimi.ingsw.common.Coordinate;
import it.polimi.ingsw.common.Graph;
import it.polimi.ingsw.model.cards.*;

import java.io.Serializable;
import java.util.*;

import static it.polimi.ingsw.Const.*;

/**
 * This class represent the PLAYER model.
 * @author Younes Bamhaoud
 */
public class Player implements Serializable {
    private String username;
    private final int ID;
    private int totalPoints;
    private BookShelf bookShelf;
    private PersonalGoalCard personalGoalCard;

    /**
     * Specify if the player is the FIRST of the round in all the game
     */
    private boolean chair;

    /**
     * Map that contains the points earned during the game from the related common card.
     */
    private final List<Integer> commonCardPoints;

    /**
     * Points earned during the game from the related personal card.
     */
    private int personalGoalCardPoints = 0;

    private int adjacentTilesPoints = 0;

    private int firstCompletedPoints = 0;

    /**
     * Saves number of turns played. Useful to save the state of the game.
     */
    private int numOfTurns;

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Player constructor.
     * @param username
     * @param ID
     */
    public Player(String username, Integer ID){
        this.bookShelf = new BookShelf();
        this.username = username;
        this.ID = ID;
        this.commonCardPoints = new ArrayList<>();
        // Add 2 common goal card points.
        this.commonCardPoints.add(0);
        this.commonCardPoints.add(0);
        this.active = true;
        this.numOfTurns = 0;
    }

    /**
     * Player's username getter.
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Player's username setter.
     * @param nickname
     */
    public void setUsername(String nickname) {
        this.username = nickname;
    }

    /**
     * This method returns the boolean value representing if a player has the first player chair or not.
     * @return
     */
    public boolean hasChair() {
        return this.chair;
    }

    /**
     * CHair setter.
     * @param chair
     */
    public void setChair(boolean chair){
        this.chair = chair;
    }

    /**
     * BookShelf getter.
     * @return
     */
    public BookShelf getBookShelf() {
        return bookShelf;
    }

    /**
     * Number of turns getter.
     * @return
     */
    public int getNumOfTurns() {
        return numOfTurns;
    }

    /**
     * Method used to set the number of turns played.
     */
    public void updateNumOfTurns() {
        this.numOfTurns++;
    }

    /**
     * Player points getter.
     * @return
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * Updates total points based on points earned from personal and common cards.
     */
    public void updateTotalPoints() {
        int total=0;
        for(int i = 0; i< commonCardPoints.size(); i++){
            total += commonCardPoints.get(i);
        }
        total += personalGoalCardPoints;
        total += adjacentTilesPoints;
        total += firstCompletedPoints;
        this.totalPoints = total;
    }


    /**
     * Get points earned form the common goal card.
     * @param i index of the card.  This is strictly related to the index of the common goal card in Game class.
     * @return points earned from that common goal card.
     */
    public int getCommonCardPoints(int i) {
        return this.commonCardPoints.get(i);
    }


    /**
     * Set points earned form the common goal card.
     * @param i index of the card.  This is strictly related to the index of the common goal card in Game class.
     * @param value points earned from that common goal card.
     */
    public void setCommonCardPoints(int i, int value) {
        this.commonCardPoints.set(i, value);
    }


    /**
     * Check personal goal card scheme and automatically update points.
     */
    public void checkPersonalGoalCardScheme(){
        personalGoalCardPoints = personalGoalCard.checkScheme(this);
    }

    /**
     * Check adjacent tiles in order to assign extra points.
     */
    public void checkAdjacentTiles(){
        List<Integer> totalMatches = new ArrayList<>();  // Total matches
        totalMatches.add(0);
        int partialCount = 0;
        int totalColorMatches = 0;
        Map<Integer, Integer> assignablePoints = new HashMap<>();

        // Assign points based on the number of color matches.
        assignablePoints.put(0, 0);  // group of 0 adjacent tiles, 0 points.
        assignablePoints.put(1, 0);  // group of 1 adjacent tiles, 0 points.
        assignablePoints.put(2, 0);  // group of 2 adjacent tiles, 0 points.
        assignablePoints.put(3, 2);  // group of 3 adjacent tiles, 2 points.
        assignablePoints.put(4, 3);  // group of 4 adjacent tiles, 3 points.
        assignablePoints.put(5, 5);  // group of 5 adjacent tiles, 5 points.
        assignablePoints.put(6, 8);  // group of 3 adjacent tiles, 8 points.
        for(int i=7; i<=MAXTILES; i++){  // group of 6+ tiles, 8 points.
            assignablePoints.put(i, 8);
        }

        // Initialize map for each tile's type. Value of each key represent a list of coordinates of where that color is in the bookshelf.
        Map<Tile, List<Coordinate>> map = new HashMap<>();
        for(Tile tile : Tile.values()){
            if(tile != Tile.UNAVAILABLE && tile != Tile.BLANK){
                map.put(tile, new ArrayList<>());
            }
        }

        // Insert all the coordinates of where each color is in the bookshelf.
        for(int i = 0; i< MAXBOOKSHELFROW; i++) {
            for(int j = 0; j< MAXBOOKSHELFCOL; j++) {
                Tile tile = bookShelf.getGrid()[i][j].getTile();
                if(tile != Tile.UNAVAILABLE && tile != Tile.BLANK){
                    List<Coordinate> coordinateList = map.get(tile);
                    coordinateList.add(new Coordinate(i, j));
                }
            }
        }

        // Create an undirected graph.
        Graph<Coordinate> graph = new Graph<>();

        // For each color get the list of coordinates in the bookshelf.
        // Iterate all the coordinates. Given 2 pointer compare one coordinate with the others (from i+1 to the end of the list) and check if they are "close" together.
        // Add to the graph only the tiles that are close together.
        for(Tile tile : map.keySet()) {
            List<Coordinate> coordinates = map.get(tile);
            for(int i=0; i<coordinates.size()-1; i++){
                for(int j=i+1; j<coordinates.size(); j++){
                    Coordinate c1 = coordinates.get(i);
                    Coordinate c2 = coordinates.get(j);
                    if(checkAdjacentCoordinates(c1, c2))
                        graph.addEdge(c1, c2, true);
                }
            }

            // Count all connected vertex. If they are connected it means that they follow a path in the bookshelf (that means they are close).
            for(Coordinate c : coordinates){
                partialCount = 0;
                try{
                    partialCount = graph.DFS(c);
                }catch (NullPointerException e){
                    // Do noting if the vertex doesn't exist. Maybe there are no available edges.
                }

                // Consider the group with the highest number of close tiles (of the same color).
                if(partialCount > totalColorMatches)
                    totalColorMatches = partialCount;
            }
            // Add to the list of maximum matches for each color.
            totalMatches.add(totalColorMatches);
            totalColorMatches = 0;
        }

        partialCount = 0;
        // Iterate all the matches of each color and replace the previous adjacent tiles' points.S
        for(Integer i : totalMatches){
            partialCount += assignablePoints.get(i);
        }
        adjacentTilesPoints = partialCount;
    }

    private boolean checkAdjacentCoordinates(Coordinate c1, Coordinate c2){
        return Math.abs(c1.getRow() - c2.getRow()) == 0 && Math.abs(c1.getCol() - c2.getCol()) == 1 || Math.abs(c1.getCol() - c2.getCol()) == 0 && Math.abs(c1.getRow() - c2.getRow()) == 1;
    }

    public void checkFullBookshelf(){
        if(bookShelf.checkEndGame())
            firstCompletedPoints = 1;
        else
            firstCompletedPoints = 0;
    }


    /**
     * Personal goal card setter.
     * @param card Personal goal card, picked from the bag
     * @return
     */
    public void setPersonalGoalCard(PersonalGoalCard card) {
        this.personalGoalCard = card;
    }


    /**
     * Personal goal card getter.
     * @return
     */
    public PersonalGoalCard getPersonalGoalCard() {
        return this.personalGoalCard;
    }


    /**
     * ID getter.
     * @return
     */
    public int getID(){
        return this.ID;
    }

}
