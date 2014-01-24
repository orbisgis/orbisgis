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

import com.vividsolutions.jts.geom.Envelope;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.coreapi.api.DataManager;
import org.orbisgis.coreapi.api.ReadRowSet;
import org.orbisgis.progress.ProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * JDBC operations that does not affect database.
 * @author Nicolas Fortin
 */
public class ReadTable {
    /** SQL function to evaluate */
    public enum STATS { COUNT, SUM, AVG, STDDEV_SAMP, MIN, MAX}
    protected final static I18n I18N = I18nFactory.getI18n(ReadTable.class);

    public static Collection<Integer> getSortedColumnRowIndex(Connection connection, String table, String columnName, boolean ascending, ProgressMonitor progressMonitor) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(table);
        Collection<Integer> columnValues;
        try(Statement st = connection.createStatement()) {
            int rowCount = 0;
            try(ResultSet rs = st.executeQuery("SELECT COUNT(*) cpt from "+tableLocation.toString())) {
                if(rs.next()) {
                    rowCount = rs.getInt(1);
                }
            }
            columnValues = new ArrayList<>(rowCount);
            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
            progressMonitor.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                    listener);
            try {
                int pkIndex = JDBCUtilities.getIntegerPrimaryKey(connection.getMetaData(), table.toUpperCase());
                if (pkIndex > 0) {
                    ProgressMonitor jobProgress = progressMonitor.startTask(2);
                    // Do not cache values
                    // Use SQL sort
                    DatabaseMetaData meta = connection.getMetaData();
                    String pkFieldName = JDBCUtilities.getFieldName(meta, table, pkIndex);
                    String desc = "";
                    if(!ascending) {
                        desc = " DESC";
                    }
                    // Create a map of Row Id to Pk Value
                    ProgressMonitor cacheProgress = jobProgress.startTask(I18N.tr("Cache primary key values"), rowCount);
                    Map<Long, Integer> pkValueToRowId = new HashMap<>(rowCount);
                    int rowId=0;
                    try(ResultSet rs = st.executeQuery("select "+pkFieldName+" from "+table)) {
                        while(rs.next()) {
                            rowId++;
                            pkValueToRowId.put(rs.getLong(1), rowId);
                            cacheProgress.endTask();
                        }
                    }
                    // Read ordered pk values
                    ProgressMonitor sortProgress = jobProgress.startTask(I18N.tr("Read sorted keys"), rowCount);
                    try(ResultSet rs = st.executeQuery("select "+pkFieldName+" from "+table+" ORDER BY "+columnName+desc)) {
                        while(rs.next()) {
                            columnValues.add(pkValueToRowId.get(rs.getLong(1)));
                            sortProgress.endTask();
                        }
                    }
                } else {
                    ProgressMonitor jobProgress = progressMonitor.startTask(2);
                    //Cache values
                    ProgressMonitor cacheProgress = jobProgress.startTask(I18N.tr("Cache table values"), rowCount);
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
                    ProgressMonitor sortProgress = jobProgress.startTask(I18N.tr("Sort table values"), rowCount);
                    Comparator<Integer> comparator = new SortValueCachedComparator(cache);
                    if (!ascending) {
                        comparator = Collections.reverseOrder(comparator);
                    }
                    columnValues = new TreeSet<>(comparator);
                    for (int i = 1; i <= rowCount; i++) {
                        columnValues.add(i);
                        sortProgress.endTask();
                    }
                }
            } finally {
                progressMonitor.removePropertyChangeListener(listener);
            }
            return columnValues;
        }
    }




    public static String resultSetToString(String query, Statement st,int maxFieldLength, int maxPrintedRows, boolean addColumns) throws SQLException {
        // Select generate a ResultSet
        ResultSet rs = st.executeQuery(query);
        // Print headers
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        StringBuilder lines = new StringBuilder();
        StringBuilder formatStringBuilder = new StringBuilder();
        String[] header = new String[columnCount];
        for(int idColumn = 1; idColumn <= columnCount; idColumn++) {
            header[idColumn-1] = metaData.getColumnLabel(idColumn)+"("+metaData.getColumnTypeName(idColumn)+")";
            formatStringBuilder.append("%-"+maxFieldLength+"s");
        }
        if(addColumns) {
            lines.append(String.format(formatStringBuilder.toString(), header));
            lines.append("\n");
        }
        int shownLines = 0;
        while(rs.next() && shownLines < maxPrintedRows) {
            String[] row = new String[columnCount];
            for(int idColumn = 1; idColumn <= columnCount; idColumn ++) {
                String value = rs.getString(idColumn);
                if(value != null) {
                    if(value.length() > maxFieldLength) {
                        value = value.substring(0, maxFieldLength-2) + "..";
                    }
                } else {
                    value = "NULL";
                }
                row[idColumn-1] = value;
            }
            shownLines++;
            lines.append(String.format(formatStringBuilder.toString(),row));
            lines.append("\n");
        }
        return lines.toString();
    }

    /**
     * Compute numeric stats of the specified table column.
     * @param connection Available connection
     * @param tableName Table name
     * @param columnName Column name
     * @param pm Progress monitor
     * @return An array of attributes {@link STATS}
     * @throws SQLException
     */
    public static String[] computeStatsSQL(Connection connection, String tableName, String columnName, ProgressMonitor pm) throws SQLException {
        String[] stats = new String[STATS.values().length];
        StringBuilder sb = new StringBuilder();
        for(STATS func : STATS.values()) {
            if(sb.length()!=0) {
                sb.append(", ");
            }
            sb.append(func.name());
            sb.append("(");
            sb.append(columnName);
            sb.append("::double) ");
            sb.append(func.name());
        }
        try(Statement st = connection.createStatement()) {

            // Cancel select
            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                    listener);
            try(ResultSet rs = st.executeQuery(String.format("SELECT %s FROM %s",sb.toString(), tableName ))) {
                if(rs.next()) {
                    for(STATS func : STATS.values()) {
                        stats[func.ordinal()] = rs.getString(func.name());
                    }
                }
            } finally {
                pm.removePropertyChangeListener(listener);
            }
        }
        return stats;
    }

    /**
     * Compute numeric stats of the specified table column using a limited input rows. Stats are not done in the sql side.
     * @param connection Available connection
     * @param tableName Table name
     * @param columnName Column name
     * @param rowNum Row id
     * @param pm Progress monitor
     * @return An array of attributes {@link STATS}
     * @throws SQLException
     */
    public static String[] computeStatsLocal(Connection connection, String tableName, String columnName, SortedSet<Integer> rowNum, ProgressMonitor pm) throws SQLException {
        String[] res = new String[STATS.values().length];
        SummaryStatistics stats = new SummaryStatistics();
        try(Statement st = connection.createStatement()) {
            // Cancel select
            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,
                    listener);
            try (ResultSet rs = st.executeQuery(String.format("SELECT %s FROM %s",columnName, tableName ))) {
                ProgressMonitor fetchProgress = pm.startTask(rowNum.size());
                while(rs.next() && !pm.isCancelled()) {
                    if(rowNum.contains(rs.getRow())) {
                        stats.addValue(rs.getDouble(columnName));
                        fetchProgress.endTask();
                    }
                }
            } finally {
                pm.removePropertyChangeListener(listener);
            }
        }
        res[STATS.SUM.ordinal()] = Double.valueOf(stats.getSum()).toString();
        res[STATS.AVG.ordinal()] = Double.valueOf(stats.getMean()).toString();
        res[STATS.COUNT.ordinal()] = Long.valueOf(stats.getN()).toString();
        res[STATS.MIN.ordinal()] = Double.valueOf(stats.getMin()).toString();
        res[STATS.MAX.ordinal()] = Double.valueOf(stats.getMax()).toString();
        res[STATS.STDDEV_SAMP.ordinal()] = Double.valueOf(stats.getStandardDeviation()).toString();
        return res;
    }

    /**
     * Retrieve the envelope of selection of lines
     * @param manager Data Manager
     * @param tableName Table identifier [[catalog.]schema.]table
     * @param rowsId Line number [1-n]
     * @param pm Progress monitor
     * @return Envelope of rows
     * @throws SQLException
     */
    public static Envelope getTableSelectionEnvelope(DataManager manager, String tableName, SortedSet<Integer> rowsId, ProgressMonitor pm) throws SQLException {
        try( Connection connection = manager.getDataSource().getConnection();
                Statement st = connection.createStatement()) {
            PropertyChangeListener cancelListener =  EventHandler.create(PropertyChangeListener.class, st, "cancel");
            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,cancelListener);
            try {
                Envelope selectionEnvelope = null;
                List<String> geomFields = SFSUtilities.getGeometryFields(connection, TableLocation.parse(tableName));
                if(geomFields.isEmpty()) {
                    throw new SQLException(I18N.tr("Table table {0} does not contain any geometry fields", tableName));
                }
                String geomField = geomFields.get(0);
                String request = "SELECT ST_Envelope(`"+geomField+"`, ST_SRID(`"+geomField+"`)) env_geom FROM "+tableName;
                ProgressMonitor selectPm = pm.startTask(rowsId.size());
                try(ReadRowSet rs = manager.createReadRowSet()) {
                    rs.setCommand(request);
                    rs.execute(pm);
                    //Evaluate the selection bounding box
                    for(int modelId : rowsId) {
                        if(rs.absolute(modelId)) {
                            Envelope rowEnvelope = rs.getGeometry("env_geom").getEnvelopeInternal();
                            if(selectionEnvelope != null) {
                                selectionEnvelope.expandToInclude(rowEnvelope);
                            } else {
                                selectionEnvelope = rowEnvelope;
                            }
                            if(pm.isCancelled()) {
                                throw new SQLException("Operation canceled by user");
                            } else {
                                selectPm.endTask();
                            }
                        }
                    }
                }
                return selectionEnvelope;
            } finally {
                pm.removePropertyChangeListener(cancelListener);
            }
        }
    }
}
