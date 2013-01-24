package org.orbisgis.view.docking.internals.actions;

import javax.swing.*;

/**
 * Contain an Action.
 * @author Nicolas Fortin
 */
public interface CActionHolder {

    /**
     * Get the Swing Action linked with this CAction.
     * @return
     */
    public Action getAction();
}
