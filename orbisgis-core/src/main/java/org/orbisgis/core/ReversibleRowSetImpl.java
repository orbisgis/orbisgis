package org.orbisgis.core;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.log4j.Logger;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.api.ReversibleRowSet;

import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.RowSetListener;
import javax.sql.rowset.BaseRowSet;
import javax.swing.event.UndoableEditListener;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * RowSet implementation that can be only linked with a table (or view).
 *
 * @author Nicolas Fortin
 */
public class ReversibleRowSetImpl extends BaseRowSet implements ReversibleRowSet, DataSource, ResultSetMetaData {
    private static final int WAITING_FOR_RESULTSET = 5;
    private final List<UndoableEditListener> undoListenerList = new ArrayList<>();
    private final TableLocation location;
    private final DataSource dataSource;
    private Map<Long, Object[]> rowCache = new HashMap<>();
    private List<Long> cachedRows = new LinkedList<>();
    private Object[] currentRow;
    private long rowId = 0;
    /** If the table has been updated or never read, rowCount is set to -1 (unknown) */
    private long cachedRowCount = -1;
    private int cachedColumnCount = -1;
    private Map<String, Integer> cachedColumnNames;
    private boolean wasNull = true;

    private int rowFetchSize = 100;
    private final ResultSetHolder resultSetHolder;

    /**
     * Constructor.
     * @param dataSource Connection properties
     * @param location Table location
     */
    public ReversibleRowSetImpl(DataSource dataSource, TableLocation location) {
        this.dataSource = dataSource;
        this.location = location;
        resultSetHolder =new ResultSetHolder(this, getCommand());
    }

    private void setWasNull(boolean wasNull) {
        this.wasNull = wasNull;
    }

    /**
     * Guaranty a result set in ready state.
     * Call this method into a synchronized (resultSetHolder) block
     * @throws SQLException
     */
    private void checkResultSet() throws SQLException {
        // Reactivate result set if necessary
        if(resultSetHolder.getStatus() == ResultSetHolder.STATUS.CLOSED || resultSetHolder.getStatus() == ResultSetHolder.STATUS.NEVER_STARTED) {
            execute();
        }
        while(resultSetHolder.getStatus() == ResultSetHolder.STATUS.STARTED) {
            try {
                Thread.sleep(WAITING_FOR_RESULTSET);
            } catch (InterruptedException e) {
                throw new SQLException(e);
            }
        }
    }

    private void checkColumnIndex(int columnIndex) throws SQLException {
        if(columnIndex < 1 || columnIndex > cachedColumnCount) {
            throw new SQLException(new IndexOutOfBoundsException("Column index "+columnIndex+" out of bound[1-"+cachedColumnCount+"]"));
        }
    }

    private void checkCurrentRow() throws SQLException {
        if(rowId < 1 || rowId > getRowCount() || currentRow == null) {
            throw new SQLException("Not in a valid row "+rowId+"/"+getRowCount());
        }
    }

    /**
     * Read the content of the DB near the current row id
     */
    private void updateRowCache() throws SQLException {
        if(!rowCache.containsKey(rowId)) {
            synchronized (resultSetHolder) {
                checkResultSet();
                ResultSet rs = resultSetHolder.getResultSet();
                final int columnCount = getColumnCount();
                if(cachedColumnNames == null) {
                    cachedColumnNames = new HashMap<>(columnCount);
                    ResultSetMetaData metaData = rs.getMetaData();
                    for(int idColumn=1; idColumn <= columnCount; idColumn++) {
                        cachedColumnNames.put(metaData.getColumnName(idColumn), idColumn);
                    }
                }
                // Cache values
                long begin = Math.max(1, rowId - (int)(rowFetchSize * 0.25));
                long end = rowId + (int)(rowFetchSize * 0.75);
                for(long fetchId = begin; fetchId < end; fetchId ++) {
                    if(!rowCache.containsKey(fetchId)) {
                        if(rs.absolute((int)rowId)) {
                            Object[] row = new Object[columnCount];
                            for(int idColumn=1; idColumn <= columnCount; idColumn++) {
                                row[idColumn-1] = rs.getObject(idColumn);
                            }
                            rowCache.put(rowId, row);
                            cachedRows.add(rowId);
                        }
                    }
                }
            }
        }
        currentRow = rowCache.get(rowId);
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
    public void addUndoableEditListener(UndoableEditListener listener) {
        undoListenerList.add(listener);
    }

    @Override
    public void removeUndoableEditListener(UndoableEditListener listener) {
        undoListenerList.remove(listener);
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
        throw new SQLException("Cannot set command");
    }

    @Override
    public void execute() throws SQLException {
        Thread resultSetThread = new Thread(resultSetHolder, "ResultSet of "+getCommand());
        resultSetThread.start();
        while(resultSetHolder.getStatus() == ResultSetHolder.STATUS.NEVER_STARTED) {
            try {
                Thread.sleep(WAITING_FOR_RESULTSET);
            } catch (InterruptedException e) {
                throw new SQLException(e);
            }
        }
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
        synchronized (resultSetHolder) {
            try {
                rowCache.clear();
                resultSetHolder.close();
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }
    }

    @Override
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
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
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
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getByte(String s) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public short getShort(String s) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getInt(String s) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLong(String s) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getFloat(String s) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getDouble(String s) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte[] getBytes(String s) throws SQLException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getDate(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Time getTime(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Timestamp getTimestamp(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getAsciiStream(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getBinaryStream(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clearWarnings() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getCursorName() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
        return getObject(cachedColumnNames.get(s));
    }

    @Override
    public int findColumn(String s) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Reader getCharacterStream(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Reader getCharacterStream(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isFirst() throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isLast() throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void beforeFirst() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void afterLast() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean first() throws SQLException {
        rowId=1;
        updateRowCache();
        notifyCursorMoved();
        return rowId>0 && rowId <= getRowCount();
    }

    @Override
    public boolean last() throws SQLException {
        rowId=getRowCount();
        updateRowCache();
        notifyCursorMoved();
        return rowId>0 && rowId <= getRowCount();
    }

    @Override
    public int getRow() throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean absolute(int i) throws SQLException {
        rowId=i;
        updateRowCache();
        notifyCursorMoved();
        return rowId>0 && rowId <= getRowCount();
    }

    @Override
    public boolean relative(int i) throws SQLException {
        rowId+=i;
        updateRowCache();
        notifyCursorMoved();
        return rowId>0 && rowId <= getRowCount();
    }

    @Override
    public boolean previous() throws SQLException {
        rowId--;
        updateRowCache();
        notifyCursorMoved();
        return rowId > 0;
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
        rowFetchSize = i;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return rowFetchSize;
    }

    @Override
    public int getType() throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getConcurrency() throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNull(int i) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateByte(int i, byte b) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateShort(int i, short i2) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateInt(int i, int i2) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateLong(int i, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateFloat(int i, float v) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateDouble(int i, double v) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateString(int i, String s) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateDate(int i, Date date) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateTime(int i, Time time) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i2) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i2) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i2) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateObject(int i, Object o, int i2) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateObject(int i, Object o) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNull(String s) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateByte(String s, byte b) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateShort(String s, short i) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateInt(String s, int i) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateLong(String s, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateFloat(String s, float v) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateDouble(String s, double v) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateString(String s, String s2) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateDate(String s, Date date) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateTime(String s, Time time) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateObject(String s, Object o) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void insertRow() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateRow() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteRow() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void refreshRow() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Statement getStatement() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> stringClassMap) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Ref getRef(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Blob getBlob(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Clob getClob(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Array getArray(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> stringClassMap) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Ref getRef(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Blob getBlob(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Clob getClob(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Array getArray(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public URL getURL(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public URL getURL(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateRef(int i, Ref ref) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateRef(String s, Ref ref) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateClob(int i, Clob clob) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateClob(String s, Clob clob) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateArray(int i, Array array) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateArray(String s, Array array) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RowId getRowId(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RowId getRowId(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNString(int i, String s) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNString(String s, String s2) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public NClob getNClob(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public NClob getNClob(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SQLXML getSQLXML(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SQLXML getSQLXML(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getNString(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getNString(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Reader getNCharacterStream(int i) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Reader getNCharacterStream(String s) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateClob(int i, Reader reader) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateClob(String s, Reader reader) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T getObject(int i, Class<T> tClass) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T getObject(String s, Class<T> tClass) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getCatalogName(i);
        }
    }

    @Override
    public int getColumnCount() throws SQLException {
        if(cachedColumnCount == -1) {
            synchronized (resultSetHolder) {
                checkResultSet();
                ResultSet rs = resultSetHolder.getResultSet();
                cachedColumnCount = rs.getMetaData().getColumnCount();
                return cachedColumnCount;
            }
        }
        return cachedColumnCount;
    }

    @Override
    public boolean isAutoIncrement(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().isAutoIncrement(i);
        }
    }

    @Override
    public boolean isCaseSensitive(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().isCaseSensitive(i);
        }
    }

    @Override
    public boolean isSearchable(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().isSearchable(i);
        }
    }

    @Override
    public boolean isCurrency(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().isCurrency(i);
        }
    }

    @Override
    public int isNullable(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().isNullable(i);
        }
    }

    @Override
    public boolean isSigned(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().isSigned(i);
        }
    }

    @Override
    public int getColumnDisplaySize(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getColumnDisplaySize(i);
        }
    }

    @Override
    public String getColumnLabel(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getColumnLabel(i);
        }
    }

    @Override
    public String getColumnName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getColumnName(i);
        }
    }

    @Override
    public String getSchemaName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getSchemaName(i);
        }
    }

    @Override
    public int getPrecision(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getPrecision(i);
        }
    }

    @Override
    public int getScale(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getScale(i);
        }
    }

    @Override
    public String getTableName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getTableName(i);
        }
    }

    @Override
    public int getColumnType(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getColumnType(i);
        }
    }

    @Override
    public String getColumnTypeName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getColumnTypeName(i);
        }
    }

    @Override
    public boolean isReadOnly(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().isReadOnly(i);
        }
    }

    @Override
    public boolean isWritable(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().isWritable(i);
        }
    }

    @Override
    public boolean isDefinitelyWritable(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().isDefinitelyWritable(i);
        }
    }

    @Override
    public String getColumnClassName(int i) throws SQLException {
        synchronized (resultSetHolder) {
            checkResultSet();
            return resultSetHolder.getResultSet().getMetaData().getColumnClassName(i);
        }
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

        private ResultSetHolder(DataSource dataSource, String command) {
            this.dataSource = dataSource;
            this.command = command;
        }

        @Override
        public void run() {
            lastUsage = System.currentTimeMillis();
            status = STATUS.STARTED;
            try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement();
            ResultSet activeResultSet = st.executeQuery(command)) {
                resultSet = activeResultSet;
                status = STATUS.READY;
                while(lastUsage + RESULT_SET_TIMEOUT > System.currentTimeMillis()) {
                    Thread.sleep(SLEEP_TIME);
                }
                // Do not release the ResultSet while it is used
                synchronized (this) {
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

        public ResultSet getResultSet() {
            return resultSet;
        }
    }
}
