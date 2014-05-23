package org.orbisgis.h2triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Single trigger class for H2 local connections.
 * @author Nicolas Fortin
 */
public class H2Trigger implements Trigger {
    private String schemaName;
    private String triggerName;
    private String tableName;
    private static TriggerListener listener;

    /**
     * Add a listener linked with connection url
     * @param listener TriggerListener instance or null to unset
     */
    public static void setListener(TriggerListener listener) {
        H2Trigger.listener = listener;
    }

    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
        this.schemaName = schemaName;
        this.triggerName = triggerName;
        this.tableName = tableName;
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        TriggerListener localListener = H2Trigger.listener;
        if(localListener != null) {
            localListener.fire(schemaName, triggerName, tableName);
        }
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void remove() throws SQLException {

    }
}
