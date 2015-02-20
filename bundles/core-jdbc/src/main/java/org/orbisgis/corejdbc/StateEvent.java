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
    // Update database view only if query starts with this command. (lowercase)
    private static final String[] updateSourceListQuery = new String[] {"drop", "create","alter"};
    private static final int MAX_LENGTH_QUERY;
    static {
        int maxLen = 0;
        for(String query : updateSourceListQuery) {
            maxLen = Math.max(maxLen, query.length());
        }
        MAX_LENGTH_QUERY = maxLen;
    }

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
     * @return True if this DB event is related to a database structure update.
     */
    public boolean isUpdateDatabaseStructure() {
        if(StateEvent.DB_STATES.STATE_STATEMENT_END.equals(stateIdentifier)) {
            // DataBase update
            if (name != null) {
                String subName = name.substring(0, MAX_LENGTH_QUERY).trim().toLowerCase();
                for (String query : updateSourceListQuery) {
                    if (subName.startsWith(query)) {
                        return true;
                    }
                }
            }
        }
        return false;
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
