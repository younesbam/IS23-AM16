package it.polimi.ingsw;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Tile;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Utilities class used to keep most used methods that don't belong to a specific class.
 */
public final class Utils {
    /**
     * Convert file into a string. Note: the file must be in resources/
     * @param fileName name of the file. If there are subdirectories, specifies them in the string. In case of subdirectories, don't add the initial "/"
     * @return String
     */
    public static String convertFileIntoString(String fileName) {
        String result=null;
        /*
        Insert the relative path where the files are located (usually in resources). Only one slash is permitted.
        We use the get() method of Paths to get the file data.
        We use readAllBytes() method of Files to read byted data from the files.
         */
        //File directory = new File("src/main/resources/" + fileName);
        try{
            Reader reader = new InputStreamReader(MyShelfie.class.getResourceAsStream(fileName));
            Scanner s = new Scanner(reader).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
            //result = new String(Files.readAllBytes(Paths.get(directory.getAbsolutePath())));
        } catch (NullPointerException e){
            e.printStackTrace();
            System.err.println("Failed to load " + fileName + " file from filesystem");
            System.exit(-1);
        }
        return result;
    }


    /**
     * Create an empty grid, with all the Cells filled with BLANK tiles
     * @param rows number of rows
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
