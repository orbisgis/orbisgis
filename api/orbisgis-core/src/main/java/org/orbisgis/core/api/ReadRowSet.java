package org.orbisgis.core.api;

import org.orbisgis.progress.ProgressMonitor;

import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;

/**
 * A ReadRowSet can be initialized using {@link JdbcRowSet#setCommand(String)}
 * @author Nicolas Fortin
 */
public interface ReadRowSet extends JdbcRowSet {
    /**
     * @return Number of rows inside the table
     */
    long getRowCount() throws SQLException;

    /**
     * Initialize this row set.
     * @param tableIdentifier Table identifier [[catalog.]schema.]table]
     * @param pk_name Primary key name to use with
     * @param pm Progress monitor Progression of primary key caching
     */
    public void initialize(String tableIdentifier,String pk_name, ProgressMonitor pm) throws SQLException;

    /**
     * @return The table identifier [[catalog.]schema.]table
     */
    public String getTable();
}
