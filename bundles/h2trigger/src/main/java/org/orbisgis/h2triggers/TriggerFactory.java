package org.orbisgis.h2triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * When H2 load a new trigger using {@link org.orbisgis.h2triggers.H2Trigger} the trigger factory will create a trigger instance.
 * @author Nicolas Fortin
 */
public interface TriggerFactory {
    /**
     * {@link org.h2.api.Trigger#init(java.sql.Connection, String, String, String, boolean, int)}
     */
    Trigger createTrigger(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException;
}
