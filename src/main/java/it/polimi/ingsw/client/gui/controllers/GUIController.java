package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;

/**
 * GUIController class defines an interface representing a single GUI controller, which is different
 * from phase to phase.
 *
 * @author Younes Bamhaoud
 */
public interface GUIController {
    /**
     * Method setGui sets the GUI reference to the local controller.
     *
     * @param gui of type GUI - the main GUI class.
     */
    void setGui(GUI gui);
}
