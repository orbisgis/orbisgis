/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import org.h2gis.utilities.SpatialResultSet;
import org.orbisgis.commons.progress.ProgressMonitor;

import javax.sql.rowset.JdbcRowSet;
import java.sql.SQLException;
import java.util.SortedSet;
import java.util.concurrent.locks.Lock;

/**
 * A ReadRowSet can be initialized using {@link JdbcRowSet#setCommand(String)}
 * The rowset is state-full then it is advised to use {@link #getReadLock()} with
 * {@link Lock#tryLock(long, java.util.concurrent.TimeUnit)} in order to avoid dead locks.
 * @author Nicolas Fortin
 */
public interface ReadRowSet extends JdbcRowSet , SpatialResultSet {
    /**
     * @return Number of rows inside the table
     * @throws java.sql.SQLException
     */
    long getRowCount() throws SQLException;

    /**
     * Initialize this row set. Same code as {@link #execute()}.
     * @param tableIdentifier Table identifier [[catalog.]schema.]table]
     * @param pk_name Primary key name to use with
     * @param pm Progress monitor Progression of primary key caching
     */
    public void initialize(String tableIdentifier,String pk_name, ProgressMonitor pm) throws SQLException;

    /**
     * @param excludeGeomFields True if the geometric fields should be excluded, false otherwise.
     */
    void setExcludeGeomFields(boolean excludeGeomFields);

    /**
     * Call this after {@link #setCommand(String)}. Cache the default primary key values then execute the command.
     * @param pm Progress monitor Progression of primary key caching
     */
    public void execute(ProgressMonitor pm) throws SQLException;

    /**
     * @return The table identifier [[catalog.]schema.]table
     */
    public String getTable();

    /**
     * @return The primary key column name used to map filtered row to row line number. Empty string if there is no primary key.
     */
    public String getPkName();

    /**
     * @return The read lock on this result set
     */
    Lock getReadLock();

    /**
     * Set the close delay of releasing of resources (0 ms by default)
     * @param milliseconds Time in milliseconds
     */
    void setCloseDelay(int milliseconds);

    /**
     * @return The numeric, simple primary key of the current row. Used to identify a row.
     */
    long getPk() throws SQLException;

    /**
     * Fetch row number using primary key values.
     * @param pkSet Primary key set {@link #getPk()}
     * @return Row identifier set {@link #getRow()}
     */
    SortedSet<Integer> getRowNumberFromRowPk(SortedSet<Long> pkSet) throws SQLException;

    /**
     * Clear cache for theses rows
     * @param rowsIndex Row numbers
     * @throws SQLException
     */
    public void refreshRows(SortedSet<Integer> rowsIndex) throws SQLException;
}
