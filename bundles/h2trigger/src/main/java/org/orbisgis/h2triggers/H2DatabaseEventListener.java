package org.orbisgis.h2triggers;

import org.h2.api.DatabaseEventListener;
import java.sql.SQLException;

/**
 * Link to H2 database in order to catch database events.
 * @author Nicolas Fortin
 */
public class H2DatabaseEventListener implements DatabaseEventListener {
    private static DatabaseEventListener delegateDatabaseEventListener;

    /**
     * @param delegateDatabaseEventListener DatabaseEventListener instance or null.
     */
    public static void setDelegateDatabaseEventListener(DatabaseEventListener delegateDatabaseEventListener) {
        H2DatabaseEventListener.delegateDatabaseEventListener = delegateDatabaseEventListener;
    }

    @Override
    public void init(String url) {
        DatabaseEventListener listener = delegateDatabaseEventListener;
        if(listener != null) {
            listener.init(url);
        }
    }

    @Override
    public void opened() {
        DatabaseEventListener listener = delegateDatabaseEventListener;
        if(listener != null) {
            listener.opened();
        }
    }

    @Override
    public void exceptionThrown(SQLException e, String sql) {
        DatabaseEventListener listener = delegateDatabaseEventListener;
        if(listener != null) {
            listener.exceptionThrown(e, sql);
        }
    }

    @Override
    public void setProgress(int state, String name, int x, int max) {
        DatabaseEventListener listener = delegateDatabaseEventListener;
        if(listener != null) {
            listener.setProgress(state, name, x, max);
        }
    }

    @Override
    public void closingDatabase() {
        DatabaseEventListener listener = delegateDatabaseEventListener;
        if(listener != null) {
            listener.closingDatabase();
        }
    }
}
