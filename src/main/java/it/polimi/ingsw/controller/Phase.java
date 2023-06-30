package it.polimi.ingsw.controller;

/**
 * Phase of the game.
 */
public enum Phase {
    /**
     * Represent that the first player doesn't answer to the number of players question.
     * <p></p>
     * During this mode, new players can't connect, until the server knows the maximum number of players.
     */
    SETUP,

    /**
     * Server waits for new players in order to start the game.
     */
    LOBBY,

    /**
     * Server waits for a tile picking command from the client.
     */
    TILESPICKING,

    /**
     * Server waits for a tile placing command from the client.
     */
    TILESPLACING,

    /**
     * Game is ending. Points calculations and final stuffs.
     */
    ENDGAME,

    /**
     * No players connected. The game sleeps until at least two player are connected to continue the game.
     */
    STANDBY

}
