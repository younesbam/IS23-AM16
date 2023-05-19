package it.polimi.ingsw;

public final class Const {
    /**
     * Maximum number of card for each type.
     */
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
     * Delay time [s] between two pings to the clients.
     */
    public static final int SERVER_PING_DELAY = 2;


    /**
     * Delay time [s] between two pings. Activated from the server.
     */
    public static final int CLIENT_PING_DELAY = 2;
}
