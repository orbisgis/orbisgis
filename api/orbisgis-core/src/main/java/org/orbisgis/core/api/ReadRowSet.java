package org.orbisgis.core.api;

import org.orbisgis.progress.ProgressMonitor;

import javax.sql.RowSet;
import java.sql.SQLException;

/**
 * @author Nicolas Fortin
 */
public interface ReadRowSet extends RowSet {
    /**
     * @return Number of rows inside the table
     */
    long getRowCount() throws SQLException;

    /**
     * Init the Row set
     * @param pm Progress monitor
     * @throws SQLException If initialisation failed
     */
    void init(ProgressMonitor pm) throws SQLException;
}
