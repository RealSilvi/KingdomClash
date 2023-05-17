package it.unibo.view.battle.panels.api;

import it.unibo.view.battle.Troop;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * This interface show how to use southPanel and northPanel of the Gui.
 * <br>
 * Show how to use the TroopButtons which the user can choose before passing the round.
 */
public interface PlayerPanel {


    /**
     * Display new Random TroopButtons and block those which are already chosen.
     */
    void update(Map<Integer, Troop> troops);

    /**
     * Disable all the buttons.
     */
    void disableAllSlots();

    /**
     * Enable all the buttons.
     */
    void enableAllSlots();

    /**
     *
     * @param actionListener gives instruction at all the TroopButtons.
     */
    void setActionListenersSlot(ActionListener actionListener);

    JPanel getPanel();
}
