package org.orbisgis.corejdbc;

/**
 * State event data
 * @author Nicolas Fortin
 */
public class StateEvent {
    /**
     * States id {@see org.h2.api.DatabaseEventListener}
     */
    public enum DB_STATES {STATE_SCAN_FILE,STATE_CREATE_INDEX,STATE_RECOVER,STATE_BACKUP_FILE,STATE_RECONNECTED,
        STATE_STATEMENT_START,STATE_STATEMENT_END,STATE_STATEMENT_PROGRESS}
    private final DB_STATES stateIdentifier;
    private final String name;
    private final int i;
    private final int max;
    /**
     * @param stateIdentifier State id
     * @param name Object name
     * @param i State current progression
     * @param max State max progression
     */
    public StateEvent(DB_STATES stateIdentifier, String name, int i, int max) {
        this.stateIdentifier = stateIdentifier;
        this.name = name;
        this.i = i;
        this.max = max;
    }

    /**
     * @return State max progression
     */
    public int getMax() {
        return max;
    }

    /**
     * @return State current progression
     */
    public DB_STATES getStateIdentifier() {
        return stateIdentifier;
    }

    /**
     * @return Object name
     */
    public String getName() {
        return name;
    }

    /**
     * @return State current progression
     */
    public int getI() {
        return i;
    }
}
