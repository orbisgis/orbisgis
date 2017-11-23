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

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.SpatialResultSet;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.corejdbc.common.LongUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;


/**
 * JDBC operations that does not affect database.
 * @author Nicolas Fortin
 */
public class ReadTable {
    /** SQL function to evaluate */
    public enum STATS { COUNT, SUM, AVG, STDDEV_SAMP, MIN, MAX}
    protected final static I18n I18N = I18nFactory.getI18n(ReadTable.class, Locale.getDefault(), I18nFactory.FALLBACK);
    private static Logger LOGGER = LoggerFactory.getLogger(ReadTable.class);
    private static final int INSERT_BATCH_SIZE = 30;

    public static Collection<Integer> getSortedColumnRowIndex(Connection connection,ReadRowSet originalOrder, String table, String originalColumnName, boolean ascending, ProgressMonitor progressMonitor) throws SQLException {
        String quoteIdentifier = TableLocation.quoteIdentifier(originalColumnName);
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
            progressMonitor.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,listener);
            try {
                int pkIndex = JDBCUtilities.getIntegerPrimaryKey(connection, tableLocation.toString());
                DatabaseMetaData meta = connection.getMetaData();
                String pkFieldName = TableLocation.quoteIdentifier(JDBCUtilities.getFieldName(meta, table, pkIndex));
                //If this condition is true, it's mean that the original order is the order of the PK.
                if (pkIndex > 0 && !pkFieldName.equals(MetaData.POSTGRE_ROW_IDENTIFIER)) {
                    ProgressMonitor jobProgress = progressMonitor.startTask(4);
                    // Do not cache values
                    // Use SQL sort
                    String desc = "";
                    if(!ascending) {
                        desc = " DESC";
                    }
                    // Create a map of Row Id to Pk Value
                    ProgressMonitor pkProgress = jobProgress.startTask(I18N.tr("Build the primary key request"), 1);
                    Map<Long, Integer> pkValueToRowId = new HashMap<>(rowCount);
                    int rowId=0;
                    try(ResultSet rs = st.executeQuery("select "+pkFieldName+" from "+table+" ORDER BY "+pkFieldName)){
                        if(JDBCUtilities.isH2DataBase(connection.getMetaData())) {
                            rs.beforeFirst();
                        }
                        pkProgress.endTask();
                        ProgressMonitor cacheProgress = jobProgress.startTask(I18N.tr("Cache primary key values"), rowCount);
                        while (rs.next() && !progressMonitor.isCancelled()) {
                            rowId++;
                            pkValueToRowId.put(rs.getLong(1), rowId);
                            cacheProgress.endTask();
                        }
                        rs.close();
                    }
                    if(progressMonitor.isCancelled()){
                        return new ArrayList<>(rowCount);
                    }
                    // Read ordered pk values
                    ProgressMonitor sortedPkProgress = jobProgress.startTask(I18N.tr("Build the sorted key request"), 1);

                    try(ResultSet rs = st.executeQuery("select "+pkFieldName+" from "+table+" ORDER BY "+quoteIdentifier+desc)) {
                        sortedPkProgress.endTask();
                        ProgressMonitor sortProgress = jobProgress.startTask(I18N.tr("Read sorted keys"), rowCount);
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
                    Lock lock = originalOrder.getReadLock();
                    lock.tryLock();
                    try{
                        originalOrder.beforeFirst();
                        int i = 0;
                        final int fieldIndex = originalOrder.findColumn(originalColumnName);
                        while(originalOrder.next() && !progressMonitor.isCancelled()) {
                            Object obj = originalOrder.getObject(fieldIndex);
                            if(obj != null && !(obj instanceof Comparable)) {
                                throw new SQLException(I18N.tr("Could only sort comparable database object type"));
                            }
                            cache[i++] = (Comparable)obj;
                            cacheProgress.endTask();
                        }
                    } finally {
                        lock.unlock();
                    }
                    if(progressMonitor.isCancelled()){
                        return new ArrayList<>(rowCount);
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

    public static SortedSet<Long> getRowPkFromRowNumber(ReadRowSet rowSet, SortedSet<Integer> rowNumber) throws SQLException {
        SortedSet<Long> modelRows = new LongUnion();
        for (int rowNum : rowNumber) {
            rowSet.absolute(rowNum);
            modelRows.add(rowSet.getPk());
        }
        return modelRows;
    }

    public static long getRowCount(Connection connection, String tableReference) throws SQLException {
        TableLocation tableLocation = TableLocation.parse(tableReference);
        if(JDBCUtilities.isH2DataBase(connection.getMetaData())) {
            try(PreparedStatement st = SFSUtilities.prepareInformationSchemaStatement(connection,tableLocation.getCatalog(),
                    tableLocation.getSchema(), tableLocation.getTable(), "INFORMATION_SCHEMA.TABLES", "",
                    "TABLE_CATALOG","TABLE_SCHEMA","TABLE_NAME");
                ResultSet rs = st.executeQuery()) {
                if(rs.next()) {
                    long estimatedRowCount = rs.getLong("ROW_COUNT_ESTIMATE");
                    // 100 because H2 views est
                    if(estimatedRowCount > 0 && !"VIEW".equalsIgnoreCase(rs.getString("TABLE_TYPE"))) {
                        return estimatedRowCount;
                    }
                }
            } catch (Exception ex) {
                // This method failed, will use standard one
                LOGGER.debug(ex.getLocalizedMessage(), ex);
            }
        }
        // Use  precise row count
        try(Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) cpt FROM "+tableReference)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    /**
     * Return a concatened and human readable format of provided result set
     * @param rs result set to read
     * @param maxFieldLength Maximum field length to print
     * @param maxPrintedRows Maximum printed rows
     * @param addColumns Add column header
     * @param alignColumns Align columns by using padding
     * @return human readable format of provided result set
     * @throws SQLException
     */
    public static String resultSetToString(ResultSet rs,int maxFieldLength, int maxPrintedRows, boolean addColumns,
                                           boolean alignColumns) throws SQLException {
        return resultSetToString(rs, maxFieldLength, maxPrintedRows, addColumns, alignColumns, new AcceptAllFilter());
    }

    /**
     * Return a concatened and human readable format of provided result set
     * @param rs result set to read
     * @param maxFieldLength Maximum field length to print
     * @param maxPrintedRows Maximum printed rows
     * @param addColumns Add column header
     * @param alignColumns Align columns by using padding
     * @param resultSetFilter Accept or refuse rows by implementing this interface
     * @return human readable format of provided result set
     * @throws SQLException
     */
    public static String resultSetToString(ResultSet rs,int maxFieldLength, int maxPrintedRows, boolean addColumns, boolean alignColumns, ResultSetFilter resultSetFilter) throws SQLException {
        // Print headers
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        StringBuilder lines = new StringBuilder();
        StringBuilder formatStringBuilder = new StringBuilder();
        String[] header = new String[columnCount];
        for(int idColumn = 1; idColumn <= columnCount; idColumn++) {
            header[idColumn-1] = metaData.getColumnLabel(idColumn)+"("+metaData.getColumnTypeName(idColumn)+")";
            if(alignColumns) {
                formatStringBuilder.append("%-");
                formatStringBuilder.append(maxFieldLength);
                formatStringBuilder.append("s ");
            } else {
                formatStringBuilder.append("%s ");
            }
        }
        if(addColumns) {
            lines.append(String.format(formatStringBuilder.toString(), header));
            lines.append("\n");
        }
        int shownLines = 0;
        NumberFormat decimalFormat = NumberFormat.getInstance(Locale.getDefault());
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setMaximumFractionDigits(16);
        while(rs.next() && shownLines < maxPrintedRows) {
            if(resultSetFilter.printRow(rs)) {
                String[] row = new String[columnCount];
                for (int idColumn = 1; idColumn <= columnCount; idColumn++) {
                    Object valObj = rs.getObject(idColumn);
                    String value;
                    if (valObj instanceof Number) {
                        value = decimalFormat.format(valObj);
                    } else {
                        value = rs.getString(idColumn);
                    }
                    if (value != null) {
                        if (columnCount > 1 && value.length() > maxFieldLength) {
                            value = value.substring(0, maxFieldLength - 2) + "..";
                        }
                    } else {
                        value = "NULL";
                    }
                    row[idColumn - 1] = value;
                }
                shownLines++;
                lines.append(String.format(formatStringBuilder.toString(), row));
                lines.append("\n");
            }
        }
        if(lines.length() != 0) {
            return lines.toString();
        } else {
            return I18N.tr("No attributes to show");
        }
    }

    private static class AcceptAllFilter implements ResultSetFilter {
        @Override
        public boolean printRow(ResultSet rs) {
            return true;
        }
    }

    public static interface ResultSetFilter {
        /**
         * @param rs Result set in valid row
         * @return True if the row should be printed
         */
        boolean printRow(ResultSet rs) throws SQLException;
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
            sb.append("::double precision) ");
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
        res[STATS.SUM.ordinal()] = Double.toString(stats.getSum());
        res[STATS.AVG.ordinal()] = Double.toString(stats.getMean());
        res[STATS.COUNT.ordinal()] = Long.toString(stats.getN());
        res[STATS.MIN.ordinal()] = Double.toString(stats.getMin());
        res[STATS.MAX.ordinal()] = Double.toString(stats.getMax());
        res[STATS.STDDEV_SAMP.ordinal()] = Double.toString(stats.getStandardDeviation());
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
    public static Envelope getTableSelectionEnvelope(DataManager manager, String tableName, SortedSet<Long> rowsId, ProgressMonitor pm) throws SQLException {
        try( Connection connection = manager.getDataSource().getConnection()) {
                Envelope selectionEnvelope = null;
                List<String> geomFields = SFSUtilities.getGeometryFields(connection, TableLocation.parse(tableName));
                if(geomFields.isEmpty()) {
                    throw new SQLException(I18N.tr("Table table {0} does not contain any geometry fields", tableName));
                }
            String fieldQuery = "ST_EXTENT(" + TableLocation.quoteIdentifier(geomFields.get(0)) + ")";
            try(FilteredResultSet fRs = new FilteredResultSet(connection, tableName, rowsId, pm, false, fieldQuery)) {
                SpatialResultSet rs = fRs.getResultSet();
                if (rs.next() && rs.getGeometry() != null) {
                    selectionEnvelope = rs.getGeometry().getEnvelopeInternal();
                }
                return selectionEnvelope;
            } catch (IOException ex) {
                throw new SQLException(ex.getLocalizedMessage(), ex);
            }
        }
    }

    /**
     *
     * @param dataManager RowSet factory
     * @param table Table identifier
     * @param geometryColumn Name of the geometry column
     * @param selection Selection polygon
     * @param contains If true selection is used with contains, else this is intersects.
     * @return List of row id.
     * @throws SQLException
     */
    public static Set<Long> getTablePkByEnvelope(DataManager dataManager, String table,String geometryColumn,
                                                    Geometry selection, boolean contains) throws SQLException {
        Set<Long> newSelection = new HashSet<>(50);
        TableLocation tableLocation = TableLocation.parse(table);
        // There is a where condition then system row index can't be used
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            String pkName = MetaData.getPkName(connection, tableLocation.toString(), true);
            boolean isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
            if(!pkName.isEmpty()) {
                String from = tableLocation.toString();
                String sqlFunction = contains ? "ST_CONTAINS(?, %s)" : "ST_INTERSECTS(?, %s)";
                try(PreparedStatement st = connection.prepareStatement(String.format("SELECT %s FROM %s WHERE %s && ? AND " + sqlFunction,
                        TableLocation.quoteIdentifier(pkName), from,
                        TableLocation.quoteIdentifier(geometryColumn), TableLocation.quoteIdentifier(geometryColumn)))) {
                    st.setObject(1, selection);
                    st.setObject(2, selection);
                    try(SpatialResultSet rs = st.executeQuery().unwrap(SpatialResultSet.class)) {
                        while (rs.next()) {
                            newSelection.add(rs.getLong(1));
                        }
                    }
                }
            } else {
                throw new SQLException(I18N.tr("Table "+table+" do not contain any information in order to identify row"));
            }
        }
        return newSelection;
    }

    public static class FilteredResultSet implements Closeable {
        private SpatialResultSet resultSet;
        private String selectionTable;
        private Statement st;
        private Connection connection;

        /**
         * @param connection Active connection
         * @param tableName Table to query
         * @param rowsId Primary keys to filter
         * @param pm Progress information and cancel
         * @param geometryOnly Retrieve only the first geometry field
         * @param customFields Query the table with custom field selection
         * @throws SQLException If an error occurred
         */
        public FilteredResultSet(Connection connection, String tableName, SortedSet<Long> rowsId, ProgressMonitor pm,boolean geometryOnly ,String customFields) throws SQLException {
            this.connection = connection;
            st = connection.createStatement();
            PropertyChangeListener cancelListener =  EventHandler.create(PropertyChangeListener.class, st, "cancel");
            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL,cancelListener);
            try {
                String fields = "t1.*";
                if(geometryOnly) {
                    List<String> geomFields = SFSUtilities.getGeometryFields(connection, TableLocation.parse(tableName));
                    if (geomFields.isEmpty()) {
                        throw new SQLException(I18N.tr("Table table {0} does not contain any geometry fields", tableName));
                    }
                    fields = TableLocation.quoteIdentifier(geomFields.get(0));
                } else if(customFields != null && !customFields.isEmpty()) {
                    fields = customFields;
                }
                // Create a temporary table that contain selected pk
                selectionTable = CreateTable.createIndexTempTable(connection, pm,rowsId,"pk", INSERT_BATCH_SIZE);
                String pkName = MetaData.getPkName(connection, tableName, true);
                StringBuilder pkEquality = new StringBuilder("t1." + pkName + " = ");
                if (!pkName.equals(MetaData.POSTGRE_ROW_IDENTIFIER)) {
                    pkEquality.append("t2.pk");
                } else {
                    pkEquality.append(MetaData.castLongToTid("t2.pk"));
                }
                // Join with temp table and compute the envelope on the server side
                resultSet = st.executeQuery("SELECT " + fields + " FROM " + tableName + " t1, " + selectionTable + " t2 where " + pkEquality).unwrap(SpatialResultSet.class);
            } finally {
                pm.removePropertyChangeListener(cancelListener);
            }
        }

        public SpatialResultSet getResultSet() {
            return resultSet;
        }

        @Override
        public void close() throws IOException {
            try {
                resultSet.close();
                st.execute("DROP TABLE IF EXISTS "+selectionTable);
                st.close();
            } catch (SQLException ex) {
                throw new IOException(ex.getLocalizedMessage(), ex);
            }
        }
    }
}
