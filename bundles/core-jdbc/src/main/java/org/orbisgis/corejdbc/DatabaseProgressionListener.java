package org.orbisgis.corejdbc;

import java.util.EventListener;

/**
 * @author Nicolas Fortin
 */
public interface DatabaseProgressionListener extends EventListener {
    /**
     * Called on database update
     * @param state
     */
    void progressionUpdate(StateEvent state);
}
