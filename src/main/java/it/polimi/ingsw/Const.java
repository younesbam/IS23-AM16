package it.polimi.ingsw;

/**
 * This class contains constants common for different classes.
 */

public final class Const {
    /**
     * Maximum number of card for each type.
     */
    public static final int MAXCARDS = 12;


    /**
     * Maximum number of rows in bookshelf.
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
     * Minimum number of players.
     */
    public static final int MINPLAYERS = 2;

    /**
     * Maximum number of players.
     */
    public static final int MAXPLAYERS = 4;

    /**
     * CLI input error message.
     */
    public static final String CLI_INPUT_ERROR = "Input error, please try again!\n>";
    /**
     * CLI incomprehensible input message.
     */
    public static final String CLI_INCOMPR_INPUT = "Incomprehensible input. Please try again\n>";


    /**
     * Delay time [s] between two pings to the clients.
     */
    public static final int SERVER_PING_DELAY = 5;

    /**
     * Maximum period [s] without receiving ping signal. After that the client will shut down.
     */
    public static final int CLIENT_PING_TIMEOUT = 10;

    /**
     * Maximum period [s] the client have to connect to the server.
     */
    public static final int CLIENT_CONNECTION_TIMEOUT = 15;

    // Regular colors.
    public static final String RESET_COLOR = "\033[0m";
    public static final String BLACK_COLOR = "\033[40m";
    public static final String RED_COLOR = "\033[41m";
    public static final String GREEN_COLOR = "\033[42m";
    public static final String YELLOW_COLOR = "\033[43m";
    public static final String BLUE_COLOR = "\033[44m";
    public static final String PURPLE_COLOR = "\033[45m";
    public static final String CYAN_COLOR = "\033[46m";
    public static final String WHITE_COLOR = "\033[47m";

    // Bold colors.
    public static final String BLACK_BOLD_COLOR = "\033[1;30m";
    public static final String RED_BOLD_COLOR = "\033[1;31m";
    public static final String GREEN_BOLD_COLOR = "\033[1;32m";
    public static final String YELLOW_BOLD_COLOR = "\033[1;33m";
    public static final String BLUE_BOLD_COLOR = "\033[1;34m";
    public static final String PURPLE_BOLD_COLOR = "\033[1;35m";
    public static final String CYAN_BOLD_COLOR = "\033[1;36m";
    public static final String WHITE_BOLD_COLOR = "\033[1;37m";

    // Background colors.
    public static final String BLACK_BACKGROUND = "\u001B[40m";
    public static final String RED_BACKGROUND = "\u001B[41m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String WHITE_BACKGROUND = "\u001B[47m";
}
