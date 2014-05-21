package org.orbisgis.corejdbc;

import java.util.EventListener;

/**
 * @author Nicolas Fortin
 */
public interface DatabaseProgressionListener extends EventListener {
    void progressionUpdate(StateInfo state);

    /**
     * Stored state information
     */
    public class StateInfo {
        /** Object name */
        public final String name;
        /** State current progression  */
        public final int i;
        /** State max progression */
        public final int max;
        /**
         * @param name Object name
         * @param i State current progression
         * @param max State max progression
         */
        public StateInfo(String name, int i, int max) {
            this.name = name;
            this.i = i;
            this.max = max;
        }
    }
}
