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
package org.orbisgis.editorjdbc.jobs;

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.ReversibleRowSet;
import org.orbisgis.corejdbc.common.LongUnion;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Delete rows using Primary keys
 * @author Nicolas Fortin
 */
public class DeleteSelectedRows extends SwingWorkerPM {
    private final LongUnion rowPkToDelete;
    private final String tableName;
    private final DataSource dataSource;
    private final static I18n I18N = I18nFactory.getI18n(DeleteSelectedRows.class);
    private final static long TRY_LOCK_TIME  = 10;

    public DeleteSelectedRows(SortedSet<Long> rowPkToDelete, String tableName, DataSource dataSource) {
        this.rowPkToDelete = new LongUnion(rowPkToDelete);
        this.tableName = tableName;
        this.dataSource = dataSource;
        setTaskName(I18N.tr("Delete selected rows"));
    }

    @Override
    protected Object doInBackground() throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            boolean isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
            int columnId = JDBCUtilities.getIntegerPrimaryKey(connection, tableName);
            if(columnId > 0) {
                String pkColumnName = JDBCUtilities.getFieldName(connection.getMetaData(), tableName, columnId);
                // A deletion batch is an ordered set of pk values to remove (a complete serial of integer without holes)
                List<Long> deletionBatch = rowPkToDelete.getValueRanges();
                ProgressMonitor pmBatch = getProgressMonitor().startTask(deletionBatch.size() / 2);
                try(PreparedStatement st = connection.prepareStatement(
                        String.format("DELETE FROM %s WHERE %s >= ? AND %s <= ?",
                                TableLocation.parse(tableName).toString(isH2),pkColumnName, pkColumnName))) {
                    for (int idDelBatch = 0; idDelBatch < deletionBatch.size() - 1; idDelBatch += 2) {
                        long startBatch = deletionBatch.get(idDelBatch);
                        long endBatch = deletionBatch.get(idDelBatch + 1);
                        st.setLong(1, startBatch);
                        st.setLong(2, endBatch);
                        st.execute();
                        pmBatch.endTask();
                    }
                }
            }
        }
        return null;
    }

    public static void deleteUsingRowSet(ReversibleRowSet reversibleRowSet, SortedSet<Long> rowPkToDelete) throws SQLException, InterruptedException {
        TreeSet<Integer> rowNumberToDelete = new TreeSet<>(reversibleRowSet.getRowNumberFromRowPk(rowPkToDelete));
        Lock lock = reversibleRowSet.getReadLock();
        if(lock.tryLock(TRY_LOCK_TIME, TimeUnit.SECONDS)) {
            try {
                // Rows must be deleted in descending order in order to not introduce shift
                for (int rowNumber : rowNumberToDelete.descendingSet()) {
                    reversibleRowSet.absolute(rowNumber);
                    reversibleRowSet.deleteRow();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
