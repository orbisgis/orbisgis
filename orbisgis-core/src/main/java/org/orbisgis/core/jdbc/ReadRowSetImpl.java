package org.orbisgis.core.jdbc;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.log4j.Logger;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.api.ReadRowSet;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;

import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.rowset.BaseRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetWarning;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RowSet implementation that can be only linked with a table (or view).
 *
 * @author Nicolas Fortin
 */
public class ReadRowSetImpl extends BaseRowSet implements JdbcRowSet, DataSource, ResultSetMetaData, ReadRowSet {
    private static final int WAITING_FOR_RESULTSET = 5;
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
    private Map<Integer, Object> rowPk;
    private String pk_name = "";
    private static final String H2_SYSTEM_PK_COLUMN = "_ROWID_";

    /**
     * Constructor, row set based on primary key, significant faster on large table
     * @param dataSource Connection properties
     * @param location Table location
     * @param pk_name Primary key name
     * @param pm Progress monitor, caching of pk values.
     * @throws IllegalArgumentException If one of the argument is incorrect, that lead to a SQL exception
     */
    public ReadRowSetImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        resultSetHolder = new ResultSetHolder(this);
    }

    private void cachePrimaryKey(ProgressMonitor pm) throws SQLException {
        ProgressMonitor cachePm = pm.startTask(getRowCount());
        if(rowPk == null) {
            rowPk = new HashMap<>((int)getRowCount());
        } else {
            rowPk.clear();
        }
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT "+pk_name+" FROM "+location)) {
            // Cache the primary key values
            int pkRowId = 0;
            while(rs.next()) {
                pkRowId++;
                rowPk.put(pkRowId, rs.getObject(1));
                cachePm.endTask();
            }
        } catch (SQLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Find the primary key name of the table.
     * @param dataSource Connection properties
     * @param location Table location
     * @return The primary key name or empty
     * @throws SQLException
     */
    public static String getPkName(DataSource dataSource, TableLocation location) throws SQLException {
        String pkName = "";
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            int pkId = JDBCUtilities.getIntegerPrimaryKey(connection.getMetaData(), location.toString());
            if(JDBCUtilities.isH2DataBase(connection.getMetaData())) {
                try(ResultSet rs = st.executeQuery("select _ROWID_ from "+location+" LIMIT 0")) {
                    pkName = rs.getMetaData().getColumnName(1);
                } catch (SQLException ex) {
                    //Ignore, key does not exists
                }
            } else if(pkId > 0) {
                // This table has a Primary key, get the field name
                pkName = JDBCUtilities.getFieldName(connection.getMetaData(), location.toString(), pkId);
            }
        }
        return pkName;
    }

    private void setWasNull(boolean wasNull) {
        this.wasNull = wasNull;
    }

    protected int getColumnByLabel(String label) throws SQLException {
        Integer columnId = cachedColumnNames.get(label.toUpperCase());
        if(columnId == null) {
            throw new SQLException("Column "+label+" does not exists");
        }
        return columnId;
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
            synchronized (resultSetHolder) {
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
                            if(!H2_SYSTEM_PK_COLUMN.equals(pk_name)) {
                                st.setObject(1, rowPk.get((int)rowId));
                            } else {
                                st.setObject(1, rowId);
                            }
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
        return String.format("SELECT * FROM %s", location);
    }

    @Override
    public void setCommand(String s) throws SQLException {
        // Extract catalog,schema and table name
        final Pattern commandPattern = Pattern.compile("from\\s+((([\"`][^\"`]+[\"`])|(\\w+))\\.){0,2}(([\"`][^\"`]+[\"`])|(\\w+))", Pattern.CASE_INSENSITIVE);
        final Pattern commandPatternTable = Pattern.compile("^from\\s+");
        String table = "";
        Matcher matcher = commandPattern.matcher(s);
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
        this.pk_name = ReadRowSetImpl.getPkName(dataSource, location);
    }

    @Override
    public void initialize(String tableIdentifier, String pk_name, ProgressMonitor pm) throws SQLException {
        initialize(TableLocation.parse(tableIdentifier), pk_name, pm);
    }

    /**
     * Initialize this row set. This method cache primary key.
     * @param location Table location
     * @param pk_name Primary key name {@link org.orbisgis.core.jdbc.ReadRowSetImpl#getPkName(javax.sql.DataSource, org.h2gis.utilities.TableLocation)}
     * @param pm Progress monitor Progression of primary key caching
     */
    public void initialize(TableLocation location,String pk_name, ProgressMonitor pm) throws SQLException {
        this.location = location;
        this.pk_name = pk_name;
        if(!pk_name.isEmpty()) {
            resultSetHolder.setCommand(getCommand()+" LIMIT 0");
            // Do not cache _ROWID_
            if(!H2_SYSTEM_PK_COLUMN.equals(pk_name)) {
                cachePrimaryKey(pm);
            } else {
                rowPk = new HashMap<>();
            }
        } else {
            resultSetHolder.setCommand(getCommand());
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
        synchronized (resultSetHolder) {
            try {
                resultSetHolder.close();
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
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
    public String getString(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        } else {
            return cell.toString();
        }
    }

    @Override
    public boolean getBoolean(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return false;
        }

        if(cell instanceof Boolean) {
            return (Boolean)cell;
        } else {
            try {
                return Boolean.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public byte getByte(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return 0;
        }

        if(cell instanceof Byte) {
            return (Byte)cell;
        } else {
            try {
                return Byte.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public short getShort(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return 0;
        }

        if(cell instanceof Number) {
            return ((Number)cell).shortValue();
        } else {
            try {
                return Short.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public int getInt(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return 0;
        }

        if(cell instanceof Number) {
            return ((Number)cell).intValue();
        } else {
            try {
                return Integer.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public long getLong(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return 0;
        }

        if(cell instanceof Number) {
            return ((Number)cell).longValue();
        } else {
            try {
                return Long.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public float getFloat(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return 0;
        }

        if(cell instanceof Number) {
            return ((Number)cell).floatValue();
        } else {
            try {
                return Float.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public double getDouble(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return 0;
        }

        if(cell instanceof Number) {
            return ((Number)cell).doubleValue();
        } else {
            try {
                return Double.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public BigDecimal getBigDecimal(int i, int i2) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        try {
            return new BigDecimal(cell.toString());
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public byte[] getBytes(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof byte[]) {
            return (byte[])cell;
        } else {
            try {
                return cell.toString().getBytes();
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public Date getDate(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof Date) {
            return (Date)cell;
        } else {
            try {
                return Date.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public Time getTime(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof Time) {
            return (Time)cell;
        } else {
            try {
                return Time.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public Timestamp getTimestamp(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof Timestamp) {
            return (Timestamp)cell;
        } else {
            try {
                return Timestamp.valueOf(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public InputStream getAsciiStream(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof InputStream) {
            return (InputStream)cell;
        } else {
            throw new SQLException("Column is not an input stream");
        }
    }

    @Override
    public InputStream getUnicodeStream(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof InputStream) {
            return (InputStream)cell;
        } else {
            throw new SQLException("Column is not an input stream");
        }
    }

    @Override
    public InputStream getBinaryStream(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof InputStream) {
            return (InputStream)cell;
        } else {
            throw new SQLException("Column is not an input stream");
        }
    }

    @Override
    public String getString(String s) throws SQLException {
        Object cell = getObject(s);

        if(cell == null) {
            return null;
        } else {
            return cell.toString();
        }
    }

    @Override
    public boolean getBoolean(String s) throws SQLException {
        return getBoolean(getColumnByLabel(s));
    }

    @Override
    public byte getByte(String s) throws SQLException {
        return getByte(getColumnByLabel(s));
    }

    @Override
    public short getShort(String s) throws SQLException {
        return getByte(getColumnByLabel(s));
    }

    @Override
    public int getInt(String s) throws SQLException {
        return getInt(getColumnByLabel(s));
    }

    @Override
    public long getLong(String s) throws SQLException {
        return getLong(getColumnByLabel(s));
    }

    @Override
    public float getFloat(String s) throws SQLException {
        return getFloat(getColumnByLabel(s));
    }

    @Override
    public double getDouble(String s) throws SQLException {
        return getDouble(getColumnByLabel(s));
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        return getBigDecimal(getColumnByLabel(s), i);
    }

    @Override
    public byte[] getBytes(String s) throws SQLException {
        return getBytes(getColumnByLabel(s));
    }

    @Override
    public Date getDate(String s) throws SQLException {
        return getDate(getColumnByLabel(s));
    }

    @Override
    public Time getTime(String s) throws SQLException {
        return getTime(getColumnByLabel(s));
    }

    @Override
    public Timestamp getTimestamp(String s) throws SQLException {
        return getTimestamp(getColumnByLabel(s));
    }

    @Override
    public InputStream getAsciiStream(String s) throws SQLException {
        return getAsciiStream(getColumnByLabel(s));
    }

    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {
        return getUnicodeStream(getColumnByLabel(s));
    }

    @Override
    public InputStream getBinaryStream(String s) throws SQLException {
        return getBinaryStream(getColumnByLabel(s));
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getWarnings();
            }
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                res.getResultSet().clearWarnings();
            }
        }
    }

    @Override
    public String getCursorName() throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getCursorName();
            }
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
    public Object getObject(String s) throws SQLException {
        return getObject(getColumnByLabel(s));
    }

    @Override
    public int findColumn(String s) throws SQLException {
        return getColumnByLabel(s);
    }

    @Override
    public Reader getCharacterStream(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof Reader) {
            return (Reader)cell;
        } else {
            throw new SQLException("Column is not an character stream");
        }
    }

    @Override
    public Reader getCharacterStream(String s) throws SQLException {
        return getCharacterStream(getColumnByLabel(s));
    }

    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return new BigDecimal(0);
        }

        if(cell instanceof BigDecimal) {
            return (BigDecimal)cell;
        } else {
            try {
                return new BigDecimal(cell.toString());
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {
        return getBigDecimal(getColumnByLabel(s));
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
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getType();
            }
        }
    }

    @Override
    public int getConcurrency() throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getConcurrency();
            }
        }
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().rowUpdated();
            }
        }
    }

    @Override
    public boolean rowInserted() throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().rowInserted();
            }
        }
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().rowDeleted();
            }
        }
    }

    @Override
    public void refreshRow() throws SQLException {
        synchronized (resultSetHolder) {
            currentRow = null;
            try(Resource res = resultSetHolder.getResource()) {
                res.getResultSet().refreshRow();
                moveCursorTo(rowId);
            }
        }
    }

    @Override
    public Statement getStatement() throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getStatement();
            }
        }
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> stringClassMap) throws SQLException {
        return getObject(i);
    }

    @Override
    public Ref getRef(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof Ref) {
            return (Ref)cell;
        } else {
            throw new SQLException("Not instance of Ref class");
        }
    }

    @Override
    public Blob getBlob(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof Blob) {
            return (Blob)cell;
        } else {
            throw new SQLException("Not instance of Blob class");
        }
    }

    @Override
    public Clob getClob(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof Clob) {
            return (Clob)cell;
        } else {
            throw new SQLException("Not instance of Clob class");
        }
    }

    @Override
    public Array getArray(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof Array) {
            return (Array)cell;
        } else {
            throw new SQLException("Not instance of Array class");
        }
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> stringClassMap) throws SQLException {
        return getObject(getColumnByLabel(s), stringClassMap);
    }

    @Override
    public Ref getRef(String s) throws SQLException {
        return getRef(getColumnByLabel(s));
    }

    @Override
    public Blob getBlob(String s) throws SQLException {
        return getBlob(getColumnByLabel(s));
    }

    @Override
    public Clob getClob(String s) throws SQLException {
        return getClob(getColumnByLabel(s));
    }

    @Override
    public Array getArray(String s) throws SQLException {
        return getArray(getColumnByLabel(s));
    }

    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {
        return getDate(i);
    }

    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {
        return getDate(getColumnByLabel(s), calendar);
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        return getTime(i);
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        return getTime(getColumnByLabel(s), calendar);
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        return getTimestamp(i);
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        return getTimestamp(getColumnByLabel(s), calendar);
    }

    @Override
    public URL getURL(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof URL) {
            return (URL)cell;
        } else {
            throw new SQLException("Not instance of URL class");
        }
    }

    @Override
    public URL getURL(String s) throws SQLException {
        return getURL(getColumnByLabel(s));
    }

    @Override
    public RowId getRowId(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getRowId(i);
            }
        }
    }

    @Override
    public RowId getRowId(String s) throws SQLException {
        return getRowId(getColumnByLabel(s));
    }

    @Override
    public int getHoldability() throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getHoldability();
            }
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false; // Never closed
    }

    @Override
    public NClob getNClob(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof NClob) {
            return (NClob)cell;
        } else {
            throw new SQLException("Not instance of NClob class");
        }
    }

    @Override
    public NClob getNClob(String s) throws SQLException {
        return getNClob(getColumnByLabel(s));
    }

    @Override
    public SQLXML getSQLXML(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof SQLXML) {
            return (SQLXML)cell;
        } else {
            throw new SQLException("Not instance of SQLXML class");
        }
    }

    @Override
    public SQLXML getSQLXML(String s) throws SQLException {
        return getSQLXML(getColumnByLabel(s));
    }

    @Override
    public String getNString(int i) throws SQLException {
        return getString(i);
    }

    @Override
    public String getNString(String s) throws SQLException {
        return getNString(getColumnByLabel(s));
    }

    @Override
    public Reader getNCharacterStream(int i) throws SQLException {
        Object cell = getObject(i);

        if(cell == null) {
            return null;
        }

        if(cell instanceof Reader) {
            return (Reader)cell;
        } else {
            throw new SQLException("Not instance of URL class");
        }
    }

    @Override
    public Reader getNCharacterStream(String s) throws SQLException {
        return getNCharacterStream(getColumnByLabel(s));
    }

    @Override
    public <T> T getObject(int i, Class<T> tClass) throws SQLException {
        Object obj = getObject(i);
        if(obj == null) {
            return null;
        }
        if(tClass == null || !tClass.isInstance(obj)) {
            throw new SQLException(obj.getClass().getSimpleName()+" not instance of "
                    + (tClass == null ? "NULL" : tClass.getSimpleName()));
        }
        return tClass.cast(obj);
    }

    @Override
    public <T> T getObject(String s, Class<T> tClass) throws SQLException {
        return getObject(getColumnByLabel(s), tClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
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
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getCatalogName(i);
            }
        }
    }

    @Override
    public int getColumnCount() throws SQLException {
        if(cachedColumnCount == -1) {
            synchronized (resultSetHolder) {
                try(Resource res = resultSetHolder.getResource()) {
                    cachedColumnCount = res.getResultSet().getMetaData().getColumnCount();
                    return cachedColumnCount;
                }
            }
        }
        return cachedColumnCount;
    }

    @Override
    public boolean isAutoIncrement(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().isAutoIncrement(i);
            }
        }
    }

    @Override
    public boolean isCaseSensitive(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().isCaseSensitive(i);
            }
        }
    }

    @Override
    public boolean isSearchable(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().isSearchable(i);
            }
        }
    }

    @Override
    public boolean isCurrency(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().isCurrency(i);
            }
        }
    }

    @Override
    public int isNullable(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().isNullable(i);
            }
        }
    }

    @Override
    public boolean isSigned(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().isSigned(i);
            }
        }
    }

    @Override
    public int getColumnDisplaySize(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getColumnDisplaySize(i);
            }
        }
    }

    @Override
    public String getColumnLabel(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getColumnLabel(i);
            }
        }
    }

    @Override
    public String getColumnName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getColumnName(i);
            }
        }
    }

    @Override
    public String getSchemaName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getSchemaName(i);
            }
        }
    }

    @Override
    public int getPrecision(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getPrecision(i);
            }
        }
    }

    @Override
    public int getScale(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getScale(i);
            }
        }
    }

    @Override
    public String getTableName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getTableName(i);
            }
        }
    }

    @Override
    public int getColumnType(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getColumnType(i);
            }
        }
    }

    @Override
    public String getColumnTypeName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getColumnTypeName(i);
            }
        }
    }

    @Override
    public boolean isReadOnly(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().isReadOnly(i);
            }
        }
    }

    @Override
    public boolean isWritable(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().isWritable(i);
            }
        }
    }

    @Override
    public boolean isDefinitelyWritable(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().isDefinitelyWritable(i);
            }
        }
    }

    @Override
    public String getColumnClassName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            try(Resource res = resultSetHolder.getResource()) {
                return res.getResultSet().getMetaData().getColumnClassName(i);
            }
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
    public <T> T unwrap(Class<T> tClass) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not a RowSet wrapper");
    }

    /**
     * This thread guaranty that the connection,ResultSet is released when no longer used.
     */
    private static class ResultSetHolder implements Runnable,AutoCloseable {
        private static final int SLEEP_TIME = 1000;
        private static final int RESULT_SET_TIMEOUT = 60000;
        public enum STATUS { NEVER_STARTED, STARTED , READY, CLOSED}

        private ResultSet resultSet;
        private DataSource dataSource;
        private String command;
        private STATUS status = STATUS.NEVER_STARTED;
        private long lastUsage = System.currentTimeMillis();
        private static final Logger LOGGER = Logger.getLogger(ResultSetHolder.class);
        private int openCount = 0;

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
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet activeResultSet = st.executeQuery(command)) {
                resultSet = activeResultSet;
                status = STATUS.READY;
                while(lastUsage + RESULT_SET_TIMEOUT > System.currentTimeMillis() || openCount != 0) {
                    Thread.sleep(SLEEP_TIME);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            } finally {
                status = STATUS.CLOSED;
            }
        }

        @Override
        public void close() throws Exception {
            lastUsage = 0;
        }

        /**
         * @return ResultSet status
         */
        public STATUS getStatus() {
            return status;
        }

        public Resource getResource() throws SQLException {
            // Reactivate result set if necessary
            if(getStatus() == ResultSetHolder.STATUS.CLOSED || getStatus() == ResultSetHolder.STATUS.NEVER_STARTED) {
                Thread resultSetThread = new Thread(this, "ResultSet of "+command);
                resultSetThread.start();
            }
            // Wait execution of request
            while(getStatus() != STATUS.READY) {
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
            openCount--;
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
