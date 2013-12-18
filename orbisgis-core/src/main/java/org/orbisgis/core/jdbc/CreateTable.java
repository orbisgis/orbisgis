/*
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
package org.orbisgis.core.jdbc;

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.progress.ProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Methods that need write rights on database
 * @author Nicolas Fortin
 */
public class CreateTable {
    protected final static I18n I18N = I18nFactory.getI18n(CreateTable.class);

    /**
     * Create a temporary table that contains
     * @param connection JDBC connection
     * @param pm Progress monitor
     * @param selectedRows Integer to add in temp table,elements must be unique as it will be added a primary key
     * @return The temporary table name
     * @throws java.sql.SQLException
     */
    public static String createIndexTempTable(Connection connection, ProgressMonitor pm, Collection<Integer> selectedRows,int insertBatchSize) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ProgressMonitor insertProgress = pm.startTask(selectedRows.size());
        // Populate the new source
        try(Statement st = connection.createStatement()) {
            // Create row id table
            String tempTableName = "CREATE_SOURCE";
            if(MetaData.tableExists(tempTableName, meta)) {
                tempTableName = MetaData.getNewUniqueName(tempTableName, meta, "");
            }
            MetaData.getNewUniqueName(tempTableName, connection.getMetaData(), "");
            st.execute(String.format("CREATE LOCAL TEMPORARY TABLE %s(ROWID integer primary key)", tempTableName));
            // Prepare insert statement
            PreparedStatement insertSt = connection.prepareStatement(String.format("INSERT INTO %s VALUES(?)", tempTableName));
            // Cancel insert
            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, insertSt, "cancel");
            insertProgress.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                    listener);
            try {
                int batchSize = 0;
                for (Integer sel : selectedRows){
                    insertSt.setInt(1, sel);
                    insertSt.addBatch();
                    batchSize++;
                    insertProgress.endTask();
                    if(batchSize >= insertBatchSize) {
                        batchSize = 0;
                        insertSt.executeBatch();
                    }
                    if(insertProgress.isCancelled()) {
                        break;
                    }
                }
                if(batchSize > 0) {
                    insertSt.executeBatch();
                }
            } finally {
                insertProgress.removePropertyChangeListener(listener);
            }
            return tempTableName;
        }
    }

    public static Collection<Integer> getSortedColumnRowIndex(Connection connection, String table, String columnName, boolean ascending, ProgressMonitor progressMonitor) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(table);
        Collection<Integer> columnValues = new ArrayList<>();
        try(Statement st = connection.createStatement()) {
            int rowCount = 0;
            try(ResultSet rs = st.executeQuery("SELECT COUNT(*) cpt from "+tableLocation.toString())) {
                if(rs.next()) {
                    rowCount = rs.getInt(1);
                }
            }
            ProgressMonitor pm = progressMonitor.startTask(rowCount);
            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                    listener);
            try {
                int pkIndex = JDBCUtilities.getIntegerPrimaryKey(connection.getMetaData(), table);
                if (pkIndex > 0) {
                    // Do not cache values
                    // Use SQL sort

                } else {
                    //Cache values
                    ProgressMonitor cacheProgress = pm.startTask(I18N.tr("Cache table values"), rowCount);
                    Comparable[] cache = new Comparable[rowCount];
                    try(ResultSet rs = st.executeQuery("select "+columnName+" from "+table)) {
                        int i = 0;
                        while(rs.next()) {
                            Object obj = rs.getObject(1);
                            if(!(obj instanceof Comparable)) {
                                throw new SQLException(I18N.tr("Could only sort comparable database object type"));
                            }
                            cache[i++] = (Comparable)obj;
                            cacheProgress.endTask();
                        }
                    }
                    Comparator<Integer> comparator = new SortValueCachedComparator(cache);
                    if (!ascending) {
                        comparator = Collections.reverseOrder(comparator);
                    }
                    columnValues = new TreeSet<>(comparator);
                    for (int i = 0; i < rowCount; i++) {
                        columnValues.add(i);
                        pm.endTask();
                    }
                }
            } finally {
                pm.removePropertyChangeListener(listener);
            }
            return columnValues;
        }
    }
}
