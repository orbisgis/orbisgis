/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.corejdbc;

import javax.sql.DataSource;
import javax.sql.rowset.RowSetFactory;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.net.URI;
import java.sql.SQLException;

/**
 * DataManager has been created in order to minimize the usage of JDBC transaction when the ResultSet is
 * frequently read or updated. It can also manage a local modification history, in order to listen modifications.
 */
public interface DataManager extends RowSetFactory {
    /**
     * Free DataManager instance resources
     */
    void dispose();

    /**
     * This method use the URI in order to upload or link a data source.
     * It can take time if the data has to be uploaded.
     * @param uri Source path
     * @return Table reference (can include schema and/or database)
     * @throws SQLException Error while transaction with JDBC
     */
    String registerDataSource(URI uri) throws SQLException;

    /**
     * @param tableReference Table reference [[catalog.]schema.]table
     * @return True if this table exists
     */
    boolean isTableExists(String tableReference) throws SQLException;

    /**
     * @return DataSource of this DataManager
     */
    DataSource getDataSource();

    /**
     * Same as {@link javax.sql.rowset.RowSetFactory#createJdbcRowSet()}
     * @return A RowSet that manage {@link UndoableEditListener}
     * @throws SQLException
     */
    public ReversibleRowSet createReversibleRowSet() throws SQLException;

    /**
     * @return A read only RowSet
     * @throws SQLException
     */
    public ReadRowSet createReadRowSet() throws SQLException;

    /**
     * Table update done through ReversibleRowSet will be fire through theses listeners
     * @param table Table identifier [[catalog.]schema.]table
     * @param listener Listener instance
     */
    void addUndoableEditListener(String table, UndoableEditListener listener);

    /**
     * Remove registered listener
     * @param table Table identifier [[catalog.]schema.]table
     * @param listener Listener instance to remove
     */
    void removeUndoableEditListener(String table, UndoableEditListener listener);

    /**
     * @param e Event to fire
     */
    void fireUndoableEditHappened(UndoableEditEvent e);

    /**
     * @param originalTableName Table name if not exists
     * @return Table name concatenated with "_1" if originalTableName already exists
     * @throws SQLException Exception while querying database
     */
    public String findUniqueTableName(String originalTableName) throws SQLException;
}
