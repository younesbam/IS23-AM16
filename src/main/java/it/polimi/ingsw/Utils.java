package it.polimi.ingsw;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Tile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utilities class used to keep most used methods that don't have a specific class
 */
public final class Utils {
    public static final int MAXCARDS = 12;
    /**
     * Maximum numbers of rows in bookshelf.
     */
    public static final int MAXBOOKSHELFROW = 6;
    /**
     * Maximum number of columns in bookshelf.
     */
    public static final int MAXBOOKSHELFCOL = 5;
    /**
     * Maximum number of picked tiles from the player.
     */
    public static final int MAXPICKEDTILES = 3;
    /**
     * Maximum number of players during the game.
     */
    private static final int MAXPLAYERS = 4;
    /**
     * Maximum number of common goal cards during the game.
     */
    private static final int COMGOALCARDS = 2;
    /**
     * Maximum number for any tile's type.
     */
    public static final int MAXTILES = 22;
    /**
     * Maximum number of columns and rows for the board.
     */
    public static final int MAXBOARDDIM = 9;


    /**
     * Check if two lists are equal, based on the size and same elements
     * @param l1 first list
     * @param l2 second list
     * @return Boolean
     */
    public static Boolean equalLists(List<?> l1, List<?> l2){
        if(l1.size() != l2.size()){
            return false;
        }
        for(int i=0; i<l1.size(); i++){
            if(! l1.get(i).equals(l2.get(i)))
                return false;
        }
        return true;
    }

    /**
     * Convert file into a string. Note: the file must be in src/main/resources/
     * @param fileName name of the file. If there are subdirectories, specifies them in the string
     * @return String
     * @author Nicolo' Gandini
     */
    public static String convertFileIntoString(String fileName) {
        String result=null;
        /*
        Insert the relative path where the files are located (usually in resources). Only one slash is permitted.
        We use the get() method of Paths to get the file data.
        We use readAllBytes() method of Files to read byted data from the files.
         */
        File directory = new File("src/main/resources/" + fileName);
        try{
            result = new String(Files.readAllBytes(Paths.get(directory.getAbsolutePath())));
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        return result;
    }

    /**
     * Create an empty grid, with all the Cells filled with BLANK tiles
     * @param rows numbers of rows
     * @param columns number of columns
     * @return empty bi-dimensional array of Cell
     */
    public static Cell[][] createBlankGrid(int rows, int columns) {
        Cell[][] grid;

        grid = new Cell[rows][columns];
        for(int i=0; i<rows; i++){
            for (int j=0; j<columns; j++){
                grid[i][j] = new Cell();
                grid[i][j].setTile(Tile.BLANK);
            }
        }

        return grid;
    }
}
