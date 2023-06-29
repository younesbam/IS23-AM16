package it.polimi.ingsw.communications.serveranswers.errors;

/**
 * Specific errors' list that describe the problem themselves.
 */
public enum ErrorClassification {
    // Setup errors
    // ----------------------------------------------------------
    /**
     * Server is in setup mode. It doesn't know the number of players in the game yet.
     */
    LOBBY_NOT_READY,
    /**
     * Username already taken by another player.
     */
    TAKEN_USERNAME,
    /**
     * Maximum number of players reached.
     */
    MAX_PLAYERS_REACHED,
    /**
     * Match doesn't exist.
     */
    MATCH_NOT_FOUND,

    // Generic in-game errors
    // ----------------------------------------------------------
    /**
     * A specific command (listed in manual) is sent but the game phase is incorrect to execute that command.
     */
    INCORRECT_PHASE,
    /**
     * Invalid passed parameters.
     */
    INVALID_PARAMETERS,

    // Picking errors
    // ----------------------------------------------------------
    /**
     * Picking error: not enough space in columns. Impossible to pick more tiles than free spaces.
     */
    NOT_ENOUGH_SPACE,
    /**
     * Picking error: tiles not pickable. Rules not respected.
     */
    TILES_NOT_PICKABLE,
    /**
     * Picking error: invalid row or column selected.
     */
    INVALID_ROW_COL,
    /**
     * Picking error: tiles not in a straight line.
     */
    TILES_NOT_STRAIGHT,
    /**
     * Picking error: tiles not adjacent.
     */
    TILES_NOT_ADJACENT,

    // Placing errors
    // ----------------------------------------------------------
    /**
     * Placing error: wrong tiles selected.
     */
    WRONG_TILES_SELECTED,
    /**
     * Placing error: the selected column is full.
     */
    FULL_COLUMN,
    /**
     * Match name error: a match with the chosen match name already exist.
     */
    INVALID_MATCH_NAME
}
