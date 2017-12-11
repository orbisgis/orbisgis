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
package org.orbisgis.corejdbc.internal;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.map.LRUMap;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.SpatialResultSetMetaData;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.AbstractRowSet;
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetWarning;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RowSet implementation that can be only linked with a table (or view).
 *
 * @author Nicolas Fortin
 */
public class ReadRowSetImpl extends AbstractRowSet implements JdbcRowSet, DataSource, SpatialResultSetMetaData, ReadRowSet {
    private static final int WAITING_FOR_RESULTSET = 5;
    public static final int DEFAULT_FETCH_SIZE = 90;
    public static final int DEFAULT_CACHE_SIZE = 300;
    // Like binary search, max intermediate batch fetching
    private static final int MAX_INTERMEDIATE_BATCH = 5;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadRowSetImpl.class);
    protected static final I18n I18N = I18nFactory.getI18n(ReadRowSetImpl.class, Locale.getDefault(), I18nFactory.FALLBACK);
    protected TableLocation location;
    protected final DataSource dataSource;
    protected Row currentRow;
    protected long rowId = 0;
    /** If the table has been updated or never read, rowCount is set to -1 (unknown) */
    protected long cachedRowCount = -1;
    private int cachedColumnCount = -1;
    protected BidiMap<String, Integer> cachedColumnNames;
    protected BidiMap<String, Integer> cachedGeomColumnNames;
    private boolean wasNull = true;
    /** Used to managed table without primary key (ResultSet are kept {@link ResultSetHolder#RESULT_SET_TIMEOUT} */
    protected final ResultSetHolder resultSetHolder;
    /** If the table contains a unique non null index then this variable contain the batch first row PK value */
    protected List<Long> rowFetchFirstPk = new ArrayList<>(Arrays.asList(new Long[]{null}));
    protected String pk_name = "";
    protected String select_fields = "*";
    protected String select_where = "";
    // Parameters for prepared statement
    protected Map<Integer, Object> parameters = new HashMap<>();
    protected int firstGeometryIndex = -1;
    protected final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    protected final Lock readLock = rwl.writeLock(); // Read here is exclusive
    protected int fetchSize = DEFAULT_FETCH_SIZE;
    // Cache of requested rows
    protected Map<Long, Row> cache = new LRUMap<>(DEFAULT_CACHE_SIZE);
    // Cache of last queried batch
    protected long currentBatchId = -1;
    protected List<Row> currentBatch = new ArrayList<>();
    private int fetchDirection = FETCH_UNKNOWN;
    // When close is called, in how many ms the result set is really closed
    private int closeDelay = 0;
    protected boolean isH2;
    protected Boolean excludeGeomFields = false;
    
    //Limit the size of the clob to 1000 characters
    private static final int NUMBER_CHARACTERS= 1000;


    /**
     * Constructor, row set based on primary key, significant faster on large table
     * @param dataSource Connection properties
     * @throws IllegalArgumentException If one of the argument is incorrect, that lead to a SQL exception
     */
    public ReadRowSetImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        resultSetHolder = new ResultSetHolder(fetchSize, this);
    }

    @Override
    public Lock getReadLock() {
        return readLock;
    }

    private void setWasNull(boolean wasNull) {
        this.wasNull = wasNull;
    }

    protected void checkColumnIndex(int columnIndex) throws SQLException {
        if(columnIndex < 1 || columnIndex > getColumnCount()) {
            throw new SQLException(new IndexOutOfBoundsException("Column index "+columnIndex+" out of bound[1-"+getColumnCount()+"]"));
        }
    }

    @Override
    public long getPk() throws SQLException {
        checkCurrentRow();
        return currentRow.pk;
    }

    @Override
    public SortedSet<Integer> getRowNumberFromRowPk(SortedSet<Long> pkSet) throws SQLException {
        SortedSet<Integer> rowsNum = new IntegerUnion();
        if(rowFetchFirstPk == null) {
            for(long pk : pkSet) {
                rowsNum.add((int)pk);
            }
        } else {
            // Use first Pk value of batch in order to fetch only batch that contains a selected pk
            Iterator<Long> fetchPkIt = pkSet.iterator();
            int batchIterId = -1;
            List<Long> batchPK = new ArrayList<>(fetchSize);
            while (fetchPkIt.hasNext()) {
                Long fetchPk = fetchPkIt.next();
                if(fetchPk != null) {
                    if(batchIterId == -1 || fetchPk > batchPK.get(batchPK.size() - 1)) {
                        batchPK.clear();
                        // Iterate through batch until next PK is superior than search pk.
                        // For optimisation sake, a binary search could be faster than serial search
                        Long nextPk = Long.MAX_VALUE;
                        final int batchCount = getBatchCount();
                        do  {
                            batchIterId++;
                            if (batchIterId + 1 >= rowFetchFirstPk.size() || rowFetchFirstPk.get(batchIterId + 1) == null) {
                                fetchBatchPk(batchIterId + 1);
                            }
                            if(rowFetchFirstPk.size() > batchIterId + 1) {
                                nextPk = rowFetchFirstPk.get(batchIterId + 1);
                            } else {
                                break;
                            }
                        } while (nextPk < fetchPk && batchIterId + 1 < batchCount - 1);
                        if(nextPk <= fetchPk) {
                            batchIterId++;
                        }
                    }
                    fetchBatchPk(batchIterId);
                    Long batchFirstPk = rowFetchFirstPk.get(batchIterId);
                    // We are in good batch
                    // Query only PK for this batch
                    if(batchPK.isEmpty()) {
                        try (Connection connection = dataSource.getConnection();
                             PreparedStatement st = createBatchQuery(connection, batchFirstPk, false, 0, fetchSize,
                                     true)) {
                            try (ResultSet rs = st.executeQuery()) {
                                while (rs.next()) {
                                    batchPK.add(rs.getLong(1));
                                }
                            }
                        }
                    }
                    // Target batch is in memory, just find the target pk index in it
                    rowsNum.add(batchIterId * fetchSize + Collections.binarySearch(batchPK, fetchPk) + 1);
                }
            }
        }
        return rowsNum;
    }

    private int getBatchCount() throws SQLException {
        return (int)Math.ceil(getRowCount() / (double)fetchSize);
    }

    /**
     * Check the current RowId, throw an exception if the cursor is at wrong position.
     * @throws SQLException
     */
    protected void checkCurrentRow() throws SQLException {
        if(rowId < 1 || rowId > getRowCount()) {
            throw new SQLException("Not in a valid row "+rowId+"/"+getRowCount());
        }
        if(currentRow == null) {
            refreshRowCache();
            if(currentRow == null) {
                throw new SQLException("Not in a valid row "+rowId+"/"+getRowCount());
            }
        }
    }

    protected void cacheColumnNames() throws SQLException {
        cachedColumnNames = new DualHashBidiMap<>();
        cachedGeomColumnNames = new DualHashBidiMap<>();
        try(Resource res = resultSetHolder.getResource()) {
            ResultSetMetaData meta = res.getResultSet().getMetaData();
            for (int idColumn = 1; idColumn <= meta.getColumnCount(); idColumn++) {
                cachedColumnNames.put(meta.getColumnName(idColumn), idColumn);
            }
            if(excludeGeomFields) {
                List<String> geomFields = SFSUtilities.getGeometryFields(getConnection(), location);
                for (String geomField : geomFields) {
                    cachedGeomColumnNames.put(geomField, cachedColumnNames.get(geomField));
                }
            }
        }
    }

    /**
     * Clear local cache of rows
     */
    protected void clearRowCache() {
        currentRow = null;
    }


    private PreparedStatement createBatchQuery(Connection connection, Long firstPk, boolean cacheData, int queryOffset, int limit, boolean queryPk) throws SQLException {
        StringBuilder command = new StringBuilder();
        if(cachedColumnNames == null || cachedGeomColumnNames==null) {
            cacheColumnNames();
        }
        command.append("SELECT ");
        if (queryPk) {
            command.append(pk_name);
            if(cacheData) {
                command.append(",");
            }
        }
        if(cacheData) {
            String fields = select_fields;
            if(fields.contains("*")){
                StringBuilder allFields = new StringBuilder();
                BidiMap<Integer, String> map = cachedColumnNames.inverseBidiMap();
                Iterable<Integer> keys = new TreeSet<>(map.keySet());
                for(Integer i : keys){
                    if(!excludeGeomFields || !cachedGeomColumnNames.containsValue(i)) {
                        if (allFields.length() > 0) {
                            allFields.append(",");
                        }
                        allFields.append(TableLocation.quoteIdentifier(map.get(i)));
                    }
                }
                fields=fields.replaceAll("\\*", allFields.toString());
            }
            command.append(fields);
        }
        command.append(" FROM ");
        command.append(getTable());
        if(firstPk != null || !select_where.isEmpty()) {
            command.append(" WHERE ");
            if(!select_where.isEmpty()) {
                command.append(select_where);
            }
            if (firstPk != null) {
                if(!select_where.isEmpty()) {
                    command.append(" AND ");
                }
                command.append(pk_name);
                command.append(" >= ?");
            }
        }
        if (isH2 || !pk_name.equals(MetaData.POSTGRE_ROW_IDENTIFIER)) {
            command.append(" ORDER BY ");
            command.append(pk_name);
        }
        command.append(" LIMIT ");
        command.append(limit);
        if(queryOffset > 0) {
            command.append(" OFFSET ");
            command.append(queryOffset);
        }
        PreparedStatement st = connection.prepareStatement(command.toString());
        for(Map.Entry<Integer, Object> entry : parameters.entrySet()) {
            st.setObject(entry.getKey(), entry.getValue());
        }
        if(firstPk != null) {
            if (isH2 || !pk_name.equals(MetaData.POSTGRE_ROW_IDENTIFIER)) {
                st.setLong(parameters.size() + 1, firstPk);
            } else {
                Ref pkRef = new Tid(firstPk);
                st.setRef(parameters.size() + 1, pkRef);
            }
        }
        return st;
    }

    /**
     * Fetch a batch that start with firstPk
     *
     * @param firstPk     First row PK
     * @param cacheData   False to only feed rowFetchFirstPk
     * @param queryOffset Offset pk fetching by this number of rows
     * @return Pk of next batch
     * @throws SQLException
     */
    private Long fetchBatch(Long firstPk, boolean cacheData, int queryOffset) throws SQLException {
        if (cacheData) {
            currentBatch.clear();
        }
        final int columnCount = getColumnCount();
        if (cachedColumnNames == null) {
            cacheColumnNames();
        }
        boolean ignoreFirstColumn = !cachedColumnNames.containsKey(pk_name);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement st = createBatchQuery(connection, firstPk, cacheData, queryOffset, cacheData ?
                     fetchSize + 1 : 1, ignoreFirstColumn || !cacheData);
             ResultSet rsBatch = st.executeQuery()) {

            int curRow = 1;
            while (rsBatch.next()) {
                long currentRowPk = rsBatch.getLong(pk_name);
                if (cacheData) {
                    int offset = ignoreFirstColumn ? 1 : 0;
                    Object[] row = new Object[columnCount];
                    for (int idColumn = 1 + offset; idColumn <= columnCount + offset; idColumn++) {
                        Object obj = rsBatch.getObject(idColumn);
                            if(obj instanceof Clob){
                                Clob clob = (Clob) obj;
                                obj = clob.getSubString(1, NUMBER_CHARACTERS)+" ...";
                            }
                        row[idColumn - 1 - offset] = obj;
                    }
                    currentBatch.add(new Row(row, currentRowPk));
                    if (curRow++ == fetchSize + 1) {
                        return rsBatch.getLong(pk_name);
                    }
                } else {
                    return currentRowPk;
                }
            }
            return null;

        }
    }

    /**
     * Read the content of the DB near the current row id
     */
    protected void refreshRowCache() throws SQLException {
        if(!cache.containsKey(rowId) && rowId > 0 && rowId <= getRowCount()) {
            try(Resource res = resultSetHolder.getResource()) {
                ResultSet rs = res.getResultSet();
                final int columnCount = getColumnCount();
                if(cachedColumnNames == null) {
                    cacheColumnNames();
                }
                // Do not use pk if not available or if using indeterminate fetch without filtering
                if(pk_name.isEmpty()) {
                    boolean validRow = false;
                    if(rs.getType() == ResultSet.TYPE_FORWARD_ONLY) {
                        if(rowId < rs.getRow()) {
                            // If the result set is Forward only, we have to re-execute the request in order to read the row
                            resultSetHolder.close();
                            res.close();
                            try(Resource res2 = resultSetHolder.getResource()) {
                                rs = res2.getResultSet();
                            }
                        }
                        while (rs.getRow() < rowId) {
                            validRow = rs.next();
                        }
                    } else {
                        validRow = rs.absolute((int)rowId);
                    }
                    if(validRow) {
                        Object[] row = new Object[columnCount];
                        for(int idColumn=1; idColumn <= columnCount; idColumn++) {
                            Object obj = rs.getObject(idColumn);
                            if(obj instanceof Clob){
                                Clob clob = (Clob) obj;
                                obj = clob.getSubString(1, (int) clob.length());
                            }
                            row[idColumn-1] = obj;
                        }
                        cache.put(rowId, new Row(row, null));
                    }
                } else {
                    // Fetch block pk of current row
                    final int targetBatch = (int) (rowId - 1) / fetchSize;
                    if (currentBatchId != targetBatch) {
                        if (targetBatch >= rowFetchFirstPk.size() || (targetBatch != 0 && rowFetchFirstPk.get(targetBatch) == null)) {
                            // For optimisation sake
                            // Like binary search if the gap of target batch is too wide, require average PK values
                            int topBatchCount = getBatchCount();
                            int lowerBatchCount = 0;
                            int intermediateBatchFetching = 0;
                            while(lowerBatchCount + ((topBatchCount - lowerBatchCount) / 2) != targetBatch &&
                                    intermediateBatchFetching < MAX_INTERMEDIATE_BATCH) {
                                int midleBatchTarget = lowerBatchCount + ((topBatchCount - lowerBatchCount) / 2);
                                if(targetBatch < midleBatchTarget) {
                                    topBatchCount = midleBatchTarget;
                                } else {
                                    if(midleBatchTarget >= rowFetchFirstPk.size() ||
                                            rowFetchFirstPk.get(midleBatchTarget) == null) {
                                        fetchBatchPk(midleBatchTarget);
                                    }
                                    intermediateBatchFetching++;
                                    lowerBatchCount = midleBatchTarget;
                                }
                            }
                            fetchBatchPk(targetBatch);
                        }
                        // Fetch all data of current batch
                        Long firstPk = fetchBatch(rowFetchFirstPk.get(targetBatch), true, 0);
                        if(firstPk!=null) {
                            if(targetBatch + 1 < rowFetchFirstPk.size()) {
                                rowFetchFirstPk.set(targetBatch + 1, firstPk);
                            } else {
                                rowFetchFirstPk.add(firstPk);
                            }
                        }
                        currentBatchId = targetBatch;
                    }
                    // Ok, still in current batch
                    int targetRowInBatch = (int) (rowId - 1) % fetchSize;
                    if(targetRowInBatch < currentBatch.size()) {
                        cache.put(rowId, currentBatch.get(targetRowInBatch));
                    }
                }
            }
        }
        currentRow = cache.get(rowId);
    }

    private void fetchBatchPk(int targetBatch) throws SQLException {
        Long firstPk = null;
        if (targetBatch >= rowFetchFirstPk.size() || rowFetchFirstPk.get(targetBatch) == null) {
            // Using limit and offset in query try to reduce query time
            // There is another batchs between target batch and the end of cached batch PK, add null intermediate pk for those.
            if(targetBatch > rowFetchFirstPk.size()) {
                Long[] intermediateSkipped = new Long[targetBatch - rowFetchFirstPk.size()];
                Arrays.fill(intermediateSkipped, null);
                rowFetchFirstPk.addAll(Arrays.asList(intermediateSkipped));
            }
            int lastNullBatchPK = targetBatch > 0 ? targetBatch - 1 : 0;
            while(firstPk == null && lastNullBatchPK > 0) {
                firstPk = rowFetchFirstPk.get(lastNullBatchPK);
                if(firstPk == null) {
                    lastNullBatchPK--;
                }
            }
            firstPk = fetchBatch(firstPk, false, (targetBatch - lastNullBatchPK) * fetchSize);
            if(firstPk != null) {
                if(targetBatch >= rowFetchFirstPk.size()) {
                    rowFetchFirstPk.add(firstPk);
                } else {
                    rowFetchFirstPk.set(targetBatch, firstPk);
                }
            }
        }
    }

    /**
     * Reestablish connection if necessary
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        if(getQueryTimeout() > 0) {
            try(Statement st = connection.createStatement()) {
                st.execute("SET QUERY_TIMEOUT "+getQueryTimeout());
            }
        }
        return connection;
    }

    @Override
    public int getTransactionIsolation() {
        try(Connection connection = getConnection()) {
            return connection.getMetaData().getDefaultTransactionIsolation();
        } catch (SQLException ex) {
            return 0;
        }
    }

    protected String getCommandWithoutFields(String additionalWhere) {
        return " FROM " + location + " " +(select_where.isEmpty() ? additionalWhere : "WHERE " + select_where +
                (additionalWhere.isEmpty() ? "" : " AND "+additionalWhere));
    }

    @Override
    public String getCommand() {
        return "SELECT " + select_fields + getCommandWithoutFields("");
    }

    @Override
    public void setCommand(String s) throws SQLException {
        // Extract catalog,schema and table name
        final Pattern selectFieldPattern = Pattern.compile("^select(.+?)from", Pattern.CASE_INSENSITIVE);
        final Pattern commandPattern = Pattern.compile("from\\s+((([\"`][^\"`]+[\"`])|(\\w+))\\.){0,2}(([\"`][^\"`]+[\"`])|(\\w+))", Pattern.CASE_INSENSITIVE);
        final Pattern commandPatternTable = Pattern.compile("^from\\s+", Pattern.CASE_INSENSITIVE);
        final Pattern wherePattern = Pattern.compile("where\\s+((.+?)+)", Pattern.CASE_INSENSITIVE);
        String table = "";
        Matcher matcher = commandPattern.matcher(s);
        Matcher selectFieldMatcher = selectFieldPattern.matcher(s);
        if(selectFieldMatcher.find()) {
            select_fields = selectFieldMatcher.group(1);
        }
        if (matcher.find()) {
            Matcher tableMatcher = commandPatternTable.matcher(matcher.group());
            if(tableMatcher.find()) {
                table = matcher.group().substring(tableMatcher.group().length());
            }
        }
        Matcher whereMatcher = wherePattern.matcher(s);
        if(whereMatcher.find()) {
            select_where = whereMatcher.group(1);
        }

        if(table.isEmpty()) {
            throw new SQLException("Command does not contain a table name, should be like this \"select * from tablename\"");
        }
        this.location = TableLocation.parse(table);
        try(Connection connection = dataSource.getConnection()) {
            this.pk_name = MetaData.getPkName(connection, location.toString(), true);
        }
    }

    @Override
    public String getTable() {
        return location.toString();
    }

    @Override
    public void initialize(String tableIdentifier, String pk_name, ProgressMonitor pm) throws SQLException {
        initialize(TableLocation.parse(tableIdentifier), pk_name, pm);
    }

    @Override
    public void setExcludeGeomFields(boolean excludeGeomFields) {
        this.excludeGeomFields = excludeGeomFields;
    }

    /**
     * Initialize this row set. This method cache primary key.
     * @param location Table location
     * @param pk_name Primary key name {@link org.orbisgis.corejdbc.MetaData#getPkName(java.sql.Connection, String, boolean)}
     * @param pm Progress monitor Progression of primary key caching
     */
    public void initialize(TableLocation location,String pk_name, ProgressMonitor pm) throws SQLException {
        this.location = location;
        this.pk_name = pk_name;
        execute(pm);
    }

    @Override
    public void execute(ProgressMonitor pm) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
            // Cache Columncount here
            cachedColumnCount = -1;
            // Cache Rowcount here
            cachedRowCount = -1;
            getRowCount(pm);
        }
        if(resultSetHolder.getStatus() == ResultSetHolder.STATUS.NEVER_STARTED) {
            resultSetHolder.setParameters(parameters);
            if (!pk_name.isEmpty()) {
                // Always use PK to fetch rows
                resultSetHolder.setCommand(getCommand() + " LIMIT 0");
            } else {
                resultSetHolder.setCommand(getCommand());
                PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, resultSetHolder, "cancel");
                pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, listener);
                try (Resource resource = resultSetHolder.getResource()) {
                } finally {
                    pm.removePropertyChangeListener(listener);
                }
            }
        } else {
            // Clear cache of all rows
            rowFetchFirstPk = new ArrayList<>(Arrays.asList(new Long[]{null}));
            moveCursorTo(Math.min(getRowCount(), rowId));
            refreshRow();
        }
    }

    @Override
    public void execute() throws SQLException {
        if(location == null) {
            throw new SQLException("You must execute RowSet.setCommand(String sql) first");
        }
        initialize(location, pk_name, new NullProgressMonitor());
    }

    @Override
    public boolean next() throws SQLException {
        return moveCursorTo(rowId + 1);
    }

    @Override
    public void close() throws SQLException {
        clearRowCache();
        try {
            resultSetHolder.delayedClose(closeDelay);
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public void setCloseDelay(int milliseconds) {
        closeDelay = milliseconds;
    }

    public long getRowCount(ProgressMonitor pm) throws SQLException {
        if(cachedRowCount == -1) {
            try (Connection connection = getConnection();
                 PreparedStatement st = createPreparedStatement(connection, "COUNT(*) CPT", "")) {
                PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, st, "cancel");
                pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, listener);
                try(ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        cachedRowCount = rs.getLong(1);
                    }
                } finally {
                    pm.removePropertyChangeListener(listener);
                }
            }
        }
        return cachedRowCount;
    }

    @Override
    public long getRowCount() throws SQLException {
        return getRowCount(new NullProgressMonitor());
    }

    protected PreparedStatement createPreparedStatement(Connection connection, String fields, String additionalWhere) throws SQLException {
        PreparedStatement st = connection.prepareStatement("SELECT "+fields+" "+getCommandWithoutFields(additionalWhere));
        for(Map.Entry<Integer, Object> entry : parameters.entrySet()) {
            st.setObject(entry.getKey(), entry.getValue());
        }
        return st;
    }

    @Override
    public boolean wasNull() throws SQLException {
        return wasNull;
    }


    @Override
    public SQLWarning getWarnings() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getWarnings();
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            res.getResultSet().clearWarnings();
        }
    }

    @Override
    public String getCursorName() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getCursorName();
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this;
    }

    @Override
    public Object getObject(int i) throws SQLException {
        checkColumnIndex(i);
        checkCurrentRow();
        Object cell = currentRow.row[i-1];
        setWasNull(cell == null);
        return cell;
    }

    @Override
    public int findColumn(String label) throws SQLException {
        if(cachedColumnNames == null) {
            cacheColumnNames();
        }
        Integer columnId = cachedColumnNames.get(label);
        if(columnId == null) {
            // Search with insensitive case
            for(Map.Entry<String, Integer> entry : cachedColumnNames.entrySet()) {
                if(entry.getKey().equalsIgnoreCase(label)) {
                    return entry.getValue();
                }
            }
            throw new SQLException("Column "+label+" does not exists");
        }
        return columnId;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return rowId == 0;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return rowId > getRowCount();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return rowId == 1;
    }

    @Override
    public boolean isLast() throws SQLException {
        return rowId == getRowCount();
    }

    @Override
    public void beforeFirst() throws SQLException {
        moveCursorTo(0);
    }

    @Override
    public void afterLast() throws SQLException {
        moveCursorTo((int) (getRowCount() + 1));
    }

    @Override
    public boolean first() throws SQLException {
        return moveCursorTo(1);
    }

    @Override
    public boolean last() throws SQLException {
        return moveCursorTo((int)getRowCount());
    }

    @Override
    public int getRow() throws SQLException {
        return (int)rowId;
    }

    @Override
    public boolean absolute(int i) throws SQLException {
        return moveCursorTo(i);
    }

    private boolean moveCursorTo(long i) throws SQLException {
        i = Math.max(0, i);
        i = Math.min(getRowCount() + 1, i);
        long oldRowId = rowId;
        rowId = i;
        boolean validRow = !(rowId == 0 || rowId > getRowCount());
        if(validRow) {
            refreshRowCache();
        } else {
            currentRow = null;
        }
        if(rowId != oldRowId) {
            notifyCursorMoved();
        }
        return validRow && currentRow != null;
    }

    @Override
    public boolean relative(int i) throws SQLException {
        return moveCursorTo((int)(rowId + i));
    }

    @Override
    public boolean previous() throws SQLException {
        return moveCursorTo(rowId - 1);
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {
        fetchDirection = i;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return fetchDirection;
    }

    @Override
    public void setFetchSize(int i) throws SQLException {
        fetchSize = i;
        LRUMap<Long, Row> lruMap = new LRUMap<>(fetchSize + 1);
        lruMap.putAll(cache);
        cache = lruMap;
        rowFetchFirstPk = new ArrayList<>(Arrays.asList(new Long[]{null}));
        currentBatch.clear();
        currentBatchId = -1;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return fetchSize;
    }

    @Override
    public int getType() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getType();
        }
    }

    @Override
    public int getConcurrency() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getConcurrency();
        }
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().rowUpdated();
        }
    }

    @Override
    public boolean rowInserted() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().rowInserted();
        }
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().rowDeleted();
        }
    }

    @Override
    public void refreshRows(SortedSet<Integer> rowsIndex) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            Set<Integer> batchIds = new HashSet<>();
            for(int refRowId : rowsIndex) {
                batchIds.add(refRowId / fetchSize);
                cache.remove(((long)refRowId));
            }
            for(int batchId : batchIds) {
                if(batchId < rowFetchFirstPk.size() && batchId >= 0) {
                    rowFetchFirstPk.set(batchId, null);
                }
                if(batchId == currentBatchId) {
                    currentBatchId = -1;
                    currentBatch = new ArrayList<>(fetchSize + 1);
                }
            }
        } catch (SQLException ex) {
            LOGGER.warn(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void refreshRow() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            currentRow = null;
            cache.clear();
            currentBatch = new ArrayList<>(fetchSize + 1);
            currentBatchId = -1;
            if(res.getResultSet().getRow() > 0 && !res.getResultSet().isAfterLast()) {
                res.getResultSet().refreshRow();
            }
        } catch (SQLException ex) {
            LOGGER.warn(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public Statement getStatement() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getStatement();
        }
    }


    @Override
    public RowId getRowId(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getRowId(i);
        }
    }

    @Override
    public RowId getRowId(String s) throws SQLException {
        return getRowId(findColumn(s));
    }

    @Override
    public int getHoldability() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getHoldability();
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false; // Never closed
    }

    // DataSource methods

    @Override
    public Connection getConnection(String s, String s2) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }

    // ResultSetMetaData functions


    @Override
    public String getCatalogName(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getCatalogName(i);
        }
    }

    @Override
    public int getColumnCount() throws SQLException {
        if(cachedColumnNames == null || cachedGeomColumnNames==null) {
            cacheColumnNames();
        }
        if(cachedColumnCount == -1) {
            try(Resource res = resultSetHolder.getResource()) {
                int hiddenFields = excludeGeomFields?cachedGeomColumnNames.size():0;
                cachedColumnCount = res.getResultSet().getMetaData().getColumnCount()-hiddenFields;
                if(cachedColumnCount == 0){
                    cachedColumnCount = res.getResultSet().getMetaData().getColumnCount();
                }
                return cachedColumnCount;
            }
        }
        return cachedColumnCount;
    }

    @Override
    public boolean isAutoIncrement(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().isAutoIncrement(i);
        }
    }

    @Override
    public boolean isCaseSensitive(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().isCaseSensitive(i);
        }
    }

    @Override
    public boolean isSearchable(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().isSearchable(i);
        }
    }

    @Override
    public boolean isCurrency(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().isCurrency(i);
        }
    }

    @Override
    public int isNullable(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().isNullable(i);
        }
    }

    @Override
    public boolean isSigned(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().isSigned(i);
        }
    }

    @Override
    public int getColumnDisplaySize(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getColumnDisplaySize(i);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        parameters.put(parameterIndex, x);
    }

    @Override
    public String getColumnLabel(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getColumnLabel(i);
        }
    }

    @Override
    public String getColumnName(int i) throws SQLException {
        if(cachedColumnNames == null) {
            cacheColumnNames();
        }
        return cachedColumnNames.getKey(i);
    }

    @Override
    public String getSchemaName(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getSchemaName(i);
        }
    }

    @Override
    public int getPrecision(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getPrecision(i);
        }
    }

    @Override
    public int getScale(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getScale(i);
        }
    }

    @Override
    public String getTableName(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getTableName(i);
        }
    }

    @Override
    public int getColumnType(int i) throws SQLException {
        //If the geometries are exluded, the index i should shift
        int offset = 0;
        if(excludeGeomFields){
            for(int val : cachedGeomColumnNames.values()){
                if(val<=i){
                    offset++;
                }
            }
        }
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getColumnType(i+offset);
        }
    }

    @Override
    public String getColumnTypeName(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getColumnTypeName(i);
        }
    }

    @Override
    public boolean isReadOnly(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().isReadOnly(i);
        }
    }

    @Override
    public boolean isWritable(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().isWritable(i);
        }
    }

    @Override
    public boolean isDefinitelyWritable(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().isDefinitelyWritable(i);
        }
    }

    @Override
    public String getColumnClassName(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getColumnClassName(i);
        }
    }

    @Override
    public void updateNull(int i) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateByte(int i, byte b) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateShort(int i, short i2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateInt(int i, int i2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateLong(int i, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateFloat(int i, float v) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateDouble(int i, double v) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateString(int i, String s) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateDate(int i, Date date) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateTime(int i, Time time) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateObject(int i, Object o, int i2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateObject(int i, Object o) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNull(String s) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateByte(String s, byte b) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateShort(String s, short i) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateInt(String s, int i) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateLong(String s, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateFloat(String s, float v) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateDouble(String s, double v) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateString(String s, String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateDate(String s, Date date) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateTime(String s, Time time) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateObject(String s, Object o) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void insertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateRef(int i, Ref ref) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateRef(String s, Ref ref) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateClob(int i, Clob clob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateClob(String s, Clob clob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateArray(int i, Array array) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateArray(String s, Array array) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNString(int i, String s) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNString(String s, String s2) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateClob(int i, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateClob(String s, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public RowSetWarning getRowSetWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("getRowSetWarnings not supported");
    }

    @Override
    public void commit() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void rollback() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void rollback(Savepoint s) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void setMatchColumn(int columnIdx) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void setMatchColumn(int[] columnIdxes) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void setMatchColumn(String columnName) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void setMatchColumn(String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public int[] getMatchColumnIndexes() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public String[] getMatchColumnNames() throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void unsetMatchColumn(int columnIdx) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void unsetMatchColumn(int[] columnIdxes) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void unsetMatchColumn(String columnName) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void unsetMatchColumn(String[] columnName) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public Geometry getGeometry() throws SQLException {
        if(firstGeometryIndex == -1) {
            try(Connection connection = dataSource.getConnection()) {
                List<String> geoFields = SFSUtilities.getGeometryFields(connection, location);
                if(!geoFields.isEmpty()) {
                    firstGeometryIndex = JDBCUtilities.getFieldIndex(getMetaData(), geoFields.get(0));
                } else {
                    throw new SQLException("No geometry column found");
                }
            }
        }
        return getGeometry(firstGeometryIndex);
    }

    @Override
    public void updateGeometry(int columnIndex, Geometry geometry) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public void updateGeometry(String columnLabel, Geometry geometry) throws SQLException {
        throw new SQLFeatureNotSupportedException("Read only RowSet");
    }

    @Override
    public String getPkName() {
        return pk_name;
    }

    @Override
    public int getGeometryType(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            SpatialResultSetMetaData meta = res.getResultSet().getMetaData().unwrap(SpatialResultSetMetaData.class);
            return meta.getGeometryType(i);
        }
    }

    @Override
    public int getGeometryType() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            SpatialResultSetMetaData meta = res.getResultSet().getMetaData().unwrap(SpatialResultSetMetaData.class);
            return meta.getGeometryType();
        }
    }

    @Override
    public int getFirstGeometryFieldIndex() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            SpatialResultSetMetaData meta = res.getResultSet().getMetaData().unwrap(SpatialResultSetMetaData.class);
            return meta.getFirstGeometryFieldIndex();
        }
    }

    /**
     * This thread guaranty that the connection,ResultSet is released when no longer used.
     */
    private static class ResultSetHolder implements Runnable,AutoCloseable {
        private static final int SLEEP_TIME = 1000;
        private static final int RESULT_SET_TIMEOUT = 60000;
        private final int fetchSize;
        public enum STATUS { NEVER_STARTED, STARTED , READY, CLOSING, CLOSED, EXCEPTION}
        private Exception ex;
        private ResultSet resultSet;
        private DataSource dataSource;
        private String command;
        private Map<Integer, Object> parameters = new HashMap<>();
        private STATUS status = STATUS.NEVER_STARTED;
        private long lastUsage = System.currentTimeMillis();
        private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetHolder.class);
        private int openCount = 0;
        private Statement cancelStatement;

        private ResultSetHolder(int fetchSize, DataSource dataSource) {
            this.fetchSize = fetchSize;
            this.dataSource = dataSource;
        }

        /**
         * @param command SQL command to execute
         */
        public void setCommand(String command) {
            this.command = command;
        }

        public void setParameters(Map<Integer, Object> parameters) {
            this.parameters = parameters;
        }

        /**
         * @return SQL Command, may be null if not set
         */
        public String getCommand() {
            return command;
        }

        @Override
        public void run() {
            lastUsage = System.currentTimeMillis();
            status = STATUS.STARTED;
            try (Connection connection = dataSource.getConnection()) {
                boolean isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
                try (
                        PreparedStatement st = connection.prepareStatement(command, isH2 ? ResultSet
                                .TYPE_SCROLL_SENSITIVE : ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                    cancelStatement = st;
                    st.setFetchSize(fetchSize);
                    for(Map.Entry<Integer, Object> entry : parameters.entrySet()) {
                        st.setObject(entry.getKey(), entry.getValue());
                    }
                    if (!isH2) {
                        // Memory optimisation for PostGre
                        connection.setAutoCommit(false);
                    }
                    // PostGreSQL use cursor only if auto commit is false
                    try (ResultSet activeResultSet = st.executeQuery()) {
                        resultSet = activeResultSet;
                        status = STATUS.READY;
                        while (lastUsage + RESULT_SET_TIMEOUT > System.currentTimeMillis() || openCount != 0) {
                            Thread.sleep(SLEEP_TIME);
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
                this.ex = ex;
                status = STATUS.EXCEPTION;
            } finally {
                if (status != STATUS.EXCEPTION) {
                    status = STATUS.CLOSED;
                }
            }
        }

        @Override
        public void close() throws SQLException {
            lastUsage = 0;
            openCount = 0;
            status = STATUS.CLOSING;
        }

        public void delayedClose(int milliSec) {
            lastUsage = System.currentTimeMillis() - RESULT_SET_TIMEOUT + milliSec;
            openCount = 0;
        }

        /**
         * {@link java.sql.Statement#cancel()}
         * @throws SQLException
         */
        public void cancel() throws SQLException {
            Statement cancelObj = cancelStatement;
            if(cancelObj != null && !cancelObj.isClosed()) {
                cancelObj.cancel();
            }
        }

        /**
         * @return ResultSet status
         */
        public STATUS getStatus() {
            return status;
        }

        public Resource getResource() throws SQLException {
            // Wait execution of request
            while(getStatus() != STATUS.READY) {
                // Reactivate result set if necessary
                if(getStatus() == ResultSetHolder.STATUS.CLOSED || getStatus() == ResultSetHolder.STATUS.NEVER_STARTED) {
                    Thread resultSetThread = new Thread(this, "ResultSet of "+command);
                    resultSetThread.start();
                }
                if(status == STATUS.EXCEPTION) {
                    if(ex instanceof SQLException) {
                        throw (SQLException)ex;
                    } else {
                        throw new SQLException(ex);
                    }
                }
                try {
                    Thread.sleep(WAITING_FOR_RESULTSET);
                } catch (InterruptedException e) {
                    throw new SQLException(e);
                }
            }
            lastUsage = System.currentTimeMillis();
            openCount++;
            return new Resource(this, resultSet);
        }

        /**
         * Even if the timer should close the result set, the connection is not closed
         */
        public void onResourceClosed() {
            openCount = Math.max(0, openCount-1);
        }
    }

    /**
     * This class is created each time the result set is necessary, close this object to release the result set.
     */
    private static class Resource implements AutoCloseable {
        private final ResultSet resultSet;
        private final ResultSetHolder holder;

        private Resource(ResultSetHolder holder, ResultSet resultSet) {
            this.holder = holder;
            this.resultSet = resultSet;
        }

        @Override
        public void close() {
            holder.onResourceClosed();
        }

        private ResultSet getResultSet() {
            return resultSet;
        }
    }

    private static class Tid implements Ref {
        private long value;

        private Tid(long value) {
            this.value = value;
        }

        @Override
        public String getBaseTypeName() throws SQLException {
            return "tid";
        }

        @Override
        public Object getObject(Map<String, Class<?>> map) throws SQLException {
            return value;
        }

        @Override
        public Object getObject() throws SQLException {
            return value;
        }

        @Override
        public void setObject(Object value) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    protected static class Row {
        public final Object[] row;
        public final Long pk;

        public Row(Object[] row, Long pk) {
            this.row = row;
            this.pk = pk;
        }
    }
}
