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
package org.orbisgis.editorjdbc.jobs;

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.common.LongUnion;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.SortedSet;

/**
 * Delete rows using Primary keys
 * @author Nicolas Fortin
 */
public class DeleteSelectedRows extends SwingWorkerPM {
    private final LongUnion rowPkToDelete;
    private final String tableName;
    private final DataSource dataSource;
    private final static I18n I18N = I18nFactory.getI18n(DeleteSelectedRows.class);

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
}
