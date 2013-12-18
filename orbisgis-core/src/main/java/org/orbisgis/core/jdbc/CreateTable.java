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

import org.orbisgis.progress.ProgressMonitor;

import javax.sql.DataSource;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * @author Nicolas Fortin
 */
public class CreateTable {

    /**
     * Create a temporary table that contains
     * @param dataSource JDBC data source
     * @param pm Progress monitor
     * @param selectedRows Integer to add in temp table
     * @return The temporary table name
     * @throws java.sql.SQLException
     */
    public static String createIndexTempTable(DataSource dataSource, ProgressMonitor pm, Set<Integer> selectedRows,int insertBatchSize) throws SQLException {
        ProgressMonitor insertProgress = pm.startTask(selectedRows.size());
        // Populate the new source
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            // Create row id table
            String tempTableName = "CREATE_SOURCE";
            if(MetaData.tableExists(tempTableName, connection.getMetaData())) {
                tempTableName = MetaData.getNewUniqueName(tempTableName, dataSource, "");
            }
            MetaData.getNewUniqueName(tempTableName, dataSource, "");
            st.execute(String.format("CREATE LOCAL TEMPORARY TABLE %s(ROWID integer primary key)", tempTableName));
            // Prepare insert statement
            PreparedStatement insertSt = connection.prepareStatement(String.format("INSERT INTO %s VALUES(?)", tempTableName));
            // Cancel insert
            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, insertSt, "cancel");
            insertProgress.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                    listener);
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
            insertProgress.removePropertyChangeListener(listener);
            return tempTableName;
        }
    }
}
