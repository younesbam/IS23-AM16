package it.polimi.ingsw.communications.serveranswers.errors;

/**
 * Specific errors' list that describe the problem themselves.
 */
public enum ErrorClassification {
    // Setup errors
    LOBBY_NOT_READY,
    TAKEN_USERNAME,
    MAX_PLAYERS_REACHED,

    // Generic in-game errors
    INCORRECT_PHASE,
    INVALID_PARAMETERS,

    // Picking errors
    NOT_ENOUGH_SPACE,
    TILES_NOT_PICKABLE,
    INVALID_ROW_COL,
    TILES_NOT_STRAIGHT,
    TILES_NOT_ADJACENT,

    // Placing errors
    WRONG_TILES_SELECTED,
    FULL_COLUMN
}
