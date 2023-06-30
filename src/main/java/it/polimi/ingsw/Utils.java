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

import static it.polimi.ingsw.Const.*;

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

        // Read json file located in resources
        try{
            Reader reader = new InputStreamReader(MyShelfie.class.getResourceAsStream(fileName));
            Scanner s = new Scanner(reader).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
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

    /**
     * This method prints the board, the bookshelf and the bookshelf on the personal goal card.
     * @param rows number of rows of the considered grid
     * @param cols number of columns of the considered grid
     */
    public static void printGrids(int rows, int cols, Cell[][] grid){
        for(int i=0; i<rows; i++){
            // Print rows numbers
            System.out.print(CYAN_BOLD_COLOR + i + RESET_COLOR);
            System.out.print(" [  ");

            for(int j=0; j<cols; j++){
                switch(grid[i][j].getTile().name()) {
                    case "BLANK" -> System.out.print(BLACK_COLOR + " " + RESET_COLOR + "  ");
                    case "WHITE" -> System.out.print(WHITE_COLOR + " " + RESET_COLOR + "  ");
                    case "LIGHTBLUE" -> System.out.print(CYAN_COLOR + " " + RESET_COLOR + "  ");
                    case "BLUE" -> System.out.print(BLUE_COLOR + " " + RESET_COLOR + "  ");
                    case "YELLOW" -> System.out.print(YELLOW_COLOR + " " + RESET_COLOR + "  ");
                    case "GREEN" -> System.out.print(GREEN_COLOR + " " + RESET_COLOR + "  ");
                    case "PINK" -> System.out.print(PURPLE_COLOR + " " + RESET_COLOR + "  ");
                    case "RED" -> System.out.print(RED_COLOR + " " + RESET_COLOR + "  ");
                    case "UNAVAILABLE" -> System.out.print("x  ");
                }
            }
            System.out.print("]");
            System.out.println();
        }

        // Print column numbers
        System.out.print("     ");
        for(int k=0; k<cols; k++){
            System.out.print(CYAN_BOLD_COLOR + k + RESET_COLOR);
            System.out.print("  ");
        }

        System.out.println();
    }
}
