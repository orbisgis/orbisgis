package org.orbisgis.core.api;

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
}
