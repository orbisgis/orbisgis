package org.orbisgis.docking.impl.internals.actions;

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
