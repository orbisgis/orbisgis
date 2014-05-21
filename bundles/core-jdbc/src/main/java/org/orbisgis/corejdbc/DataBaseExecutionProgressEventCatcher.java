package org.orbisgis.corejdbc;

/**
 * Define a component able to catch DataBase events. Used by DataManager that fetch this component in time interval.
 * {@see org.h2.api.DatabaseEventListener}
 * @author Nicolas Fortin
 */
public interface DataBaseExecutionProgressEventCatcher {
    /**
     * @param stateId State identifier
     * @return State info or null if this state has not be cached
     */
    DatabaseProgressionListener.StateInfo getStateInformations(StateEvent.DB_STATES stateId);
}
