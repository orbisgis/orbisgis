/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
import org.apache.log4j.Logger;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetWarning;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RowSet implementation that can be only linked with a table (or view).
 *
 * @author Nicolas Fortin
 */
public class ReadRowSetImpl extends AbstractRowSet implements JdbcRowSet, DataSource, ResultSetMetaData, ReadRowSet {
    private static final int WAITING_FOR_RESULTSET = 5;
    private static final Logger LOGGER = Logger.getLogger(ReadRowSetImpl.class);
    private static final I18n I18N = I18nFactory.getI18n(ReadRowSetImpl.class, Locale.getDefault(), I18nFactory.FALLBACK);
    private TableLocation location;
    private final DataSource dataSource;
    private Object[] currentRow;
    private long rowId = 0;
    /** If the table has been updated or never read, rowCount is set to -1 (unknown) */
    private long cachedRowCount = -1;
    private int cachedColumnCount = -1;
    private Map<String, Integer> cachedColumnNames;
    private boolean wasNull = true;
    /** Used to managed table without primary key (ResultSet are kept {@link ResultSetHolder#RESULT_SET_TIMEOUT} */
    protected final ResultSetHolder resultSetHolder;
    private static final int CACHE_SIZE = 100;
    private Map<Long, Object[]> cache = new LRUMap<>(CACHE_SIZE);
    /** If the table contains a unique non null index then this variable contain the map between the row id [1-n] to the primary key value */
    private BidiMap<Integer, Object> rowPk;
    private String pk_name = "";
    private String select_fields = "*";
    private int firstGeometryIndex = -1;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock readLock = rwl.writeLock(); // Read here is exclusive
    private static final int FETCH_SIZE = 100;

    /**
     * Constructor, row set based on primary key, significant faster on large table
     * @param dataSource Connection properties
     * @throws IllegalArgumentException If one of the argument is incorrect, that lead to a SQL exception
     */
    public ReadRowSetImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        resultSetHolder = new ResultSetHolder(this);
    }

    @Override
    public Lock getReadLock() {
        return readLock;
    }

    private void cachePrimaryKey(ProgressMonitor pm) throws SQLException {
        ProgressMonitor cachePm = pm.startTask(getRowCount());
        if(rowPk == null) {
            rowPk = new DualHashBidiMap<>();
        } else {
            rowPk.clear();
        }
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            st.setFetchSize(FETCH_SIZE);
            try(ResultSet rs = st.executeQuery("SELECT "+pk_name+" FROM "+location)) {
                // Cache the primary key values
                int pkRowId = 0;
                while (rs.next()) {
                    pkRowId++;
                    rowPk.put(pkRowId, rs.getObject(1));
                    cachePm.endTask();
                }
            }
        } catch (SQLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private void setWasNull(boolean wasNull) {
        this.wasNull = wasNull;
    }

    protected void checkColumnIndex(int columnIndex) throws SQLException {
        if(columnIndex < 1 || columnIndex > cachedColumnCount) {
            throw new SQLException(new IndexOutOfBoundsException("Column index "+columnIndex+" out of bound[1-"+cachedColumnCount+"]"));
        }
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
            updateRowCache();
        }
    }

    /**
     * Clear local cache of rows
     */
    protected void clearRowCache() {
        currentRow = null;
    }

    /**
     * Read the content of the DB near the current row id
     */
    protected void updateRowCache() throws SQLException {
        if(!cache.containsKey(rowId)) {
            try(Resource res = resultSetHolder.getResource()) {
                ResultSet rs = res.getResultSet();
                final int columnCount = getColumnCount();
                if(cachedColumnNames == null) {
                    cachedColumnNames = new HashMap<>(columnCount);
                    ResultSetMetaData metaData = rs.getMetaData();
                    for(int idColumn=1; idColumn <= columnCount; idColumn++) {
                        cachedColumnNames.put(metaData.getColumnName(idColumn).toUpperCase(), idColumn);
                    }
                }
                if(rowPk == null) {
                    if(rs.absolute((int)rowId)) {
                        Object[] row = new Object[columnCount];
                        for(int idColumn=1; idColumn <= columnCount; idColumn++) {
                            row[idColumn-1] = rs.getObject(idColumn);
                        }
                        cache.put(rowId, row);
                    }
                } else {
                    // Acquire row values by using primary key
                    try(Connection connection = dataSource.getConnection();
                        PreparedStatement st = connection.prepareStatement(getCommand()+" WHERE "+pk_name+" = ?")) {
                        st.setObject(1, rowPk.get((int)rowId));
                        try(ResultSet lineRs = st.executeQuery()) {
                            Object[] row = new Object[columnCount];
                            if(lineRs.next()) {
                                for(int idColumn=1; idColumn <= columnCount; idColumn++) {
                                    row[idColumn-1] = lineRs.getObject(idColumn);
                                }
                            }
                            cache.put(rowId, row);
                        }
                    }
                }
            }
        }
        currentRow = cache.get(rowId);
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

    @Override
    public String getCommand() {
        return String.format("SELECT "+select_fields+" FROM %s", location);
    }

    @Override
    public void setCommand(String s) throws SQLException {
        // Extract catalog,schema and table name
        final Pattern selectFieldPattern = Pattern.compile("^select(.+?)from", Pattern.CASE_INSENSITIVE);
        final Pattern commandPattern = Pattern.compile("from\\s+((([\"`][^\"`]+[\"`])|(\\w+))\\.){0,2}(([\"`][^\"`]+[\"`])|(\\w+))", Pattern.CASE_INSENSITIVE);
        final Pattern commandPatternTable = Pattern.compile("^from\\s+", Pattern.CASE_INSENSITIVE);
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
        if(!pk_name.isEmpty()) {
            resultSetHolder.setCommand(getCommand()+" LIMIT 0");
            cachePrimaryKey(pm);
        } else {
            LOGGER.warn(I18N.tr("Table {0} does not contain a primary key. A vast amount of memory may be required.", location));
            resultSetHolder.setCommand(getCommand());
            PropertyChangeListener listener = EventHandler.create(PropertyChangeListener.class, resultSetHolder, "cancel");
            pm.addPropertyChangeListener(ProgressMonitor.PROP_CANCEL, listener);
            try {
                resultSetHolder.getResource(); // Long query
            } finally {
                pm.removePropertyChangeListener(listener);
            }
        }
    }

    @Override
    public void execute() throws SQLException {
        if(resultSetHolder.getCommand() != null) {
            throw new SQLException("This row set is already executed");
        }
        if(location == null) {
            throw new SQLException("You must execute RowSet.setCommand(String sql) first");
        }
        initialize(location, pk_name, new NullProgressMonitor());
    }

    @Override
    public boolean next() throws SQLException {
        rowId++;
        updateRowCache();
        notifyCursorMoved();
        return rowId <= getRowCount();
    }

    @Override
    public void close() throws SQLException {
        clearRowCache();
        try {
            resultSetHolder.close();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    public long getRowCount() throws SQLException {
        if(cachedRowCount == -1) {
            try (Connection connection = getConnection();
                 Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + location)) {
                if (rs.next()) {
                    cachedRowCount = rs.getLong(1);
                }
            }
        }
        return cachedRowCount;
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
        Object cell = currentRow[i-1];
        setWasNull(cell == null);
        return cell;
    }

    @Override
    public int findColumn(String label) throws SQLException {
        Integer columnId = cachedColumnNames.get(label.toUpperCase());
        if(columnId == null) {
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
        moveCursorTo((int)(getRowCount() + 1));
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
        long oldRowId = rowId;
        rowId=i;
        updateRowCache();
        if(rowId != oldRowId) {
            notifyCursorMoved();
        }
        return rowId>0 && rowId <= getRowCount();
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
        // Not used
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return RowSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int i) throws SQLException {
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 1;
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
    public void refreshRow() throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            moveCursorTo(rowId);
            currentRow = null;
            cache.clear();
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
        if(cachedColumnCount == -1) {
            try(Resource res = resultSetHolder.getResource()) {
                cachedColumnCount = res.getResultSet().getMetaData().getColumnCount();
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
    public String getColumnLabel(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getColumnLabel(i);
        }
    }

    @Override
    public String getColumnName(int i) throws SQLException {
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getColumnName(i);
        }
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
        try(Resource res = resultSetHolder.getResource()) {
            return res.getResultSet().getMetaData().getColumnType(i);
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
    public Integer getRowId(Object primaryKeyRowValue) {
        if(!pk_name.isEmpty()) {
            return rowPk.getKey(primaryKeyRowValue);
        } else {
            throw new IllegalStateException("The RowSet has not been initialised");
        }
    }

    /**
     * This thread guaranty that the connection,ResultSet is released when no longer used.
     */
    private static class ResultSetHolder implements Runnable,AutoCloseable {
        private static final int SLEEP_TIME = 1000;
        private static final int RESULT_SET_TIMEOUT = 60000;
        public enum STATUS { NEVER_STARTED, STARTED , READY, CLOSED, EXCEPTION}
        private Exception ex;
        private ResultSet resultSet;
        private DataSource dataSource;
        private String command;
        private STATUS status = STATUS.NEVER_STARTED;
        private long lastUsage = System.currentTimeMillis();
        private static final Logger LOGGER = Logger.getLogger(ResultSetHolder.class);
        private int openCount = 0;
        private Statement cancelStatement;

        private ResultSetHolder(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        /**
         * @param command SQL command to execute
         */
        public void setCommand(String command) {
            this.command = command;
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
            try(Connection connection = dataSource.getConnection();
                Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY)) {
                cancelStatement = st;
                st.setFetchSize(FETCH_SIZE);
                try(ResultSet activeResultSet = st.executeQuery(command)) {
                    resultSet = activeResultSet;
                    status = STATUS.READY;
                    while(lastUsage + RESULT_SET_TIMEOUT > System.currentTimeMillis() || openCount != 0) {
                        Thread.sleep(SLEEP_TIME);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
                this.ex = ex;
                status = STATUS.EXCEPTION;
            } finally {
                if(status != STATUS.EXCEPTION) {
                    status = STATUS.CLOSED;
                }
            }
        }

        @Override
        public void close() throws Exception {
            lastUsage = 0;
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
}
