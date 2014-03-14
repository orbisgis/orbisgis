package org.orbisgis.core.jdbc;

import com.vividsolutions.jts.geom.Geometry;
import org.h2gis.utilities.SpatialResultSet;

import javax.sql.rowset.BaseRowSet;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * @author Nicolas Fortin
 */
public abstract class AbstractRowSet extends BaseRowSet implements SpatialResultSet {

    @Override
    public Object getObject(String s) throws SQLException {
        return getObject(findColumn(s));
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
        return getBoolean(findColumn(s));
    }

    @Override
    public byte getByte(String s) throws SQLException {
        return getByte(findColumn(s));
    }

    @Override
    public short getShort(String s) throws SQLException {
        return getByte(findColumn(s));
    }

    @Override
    public int getInt(String s) throws SQLException {
        return getInt(findColumn(s));
    }

    @Override
    public long getLong(String s) throws SQLException {
        return getLong(findColumn(s));
    }

    @Override
    public float getFloat(String s) throws SQLException {
        return getFloat(findColumn(s));
    }

    @Override
    public double getDouble(String s) throws SQLException {
        return getDouble(findColumn(s));
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        return getBigDecimal(findColumn(s), i);
    }

    @Override
    public byte[] getBytes(String s) throws SQLException {
        return getBytes(findColumn(s));
    }

    @Override
    public Date getDate(String s) throws SQLException {
        return getDate(findColumn(s));
    }

    @Override
    public Time getTime(String s) throws SQLException {
        return getTime(findColumn(s));
    }

    @Override
    public Timestamp getTimestamp(String s) throws SQLException {
        return getTimestamp(findColumn(s));
    }

    @Override
    public InputStream getAsciiStream(String s) throws SQLException {
        return getAsciiStream(findColumn(s));
    }

    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {
        return getUnicodeStream(findColumn(s));
    }

    @Override
    public InputStream getBinaryStream(String s) throws SQLException {
        return getBinaryStream(findColumn(s));
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
        return getCharacterStream(findColumn(s));
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
        return getBigDecimal(findColumn(s));
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
        return getObject(findColumn(s), stringClassMap);
    }

    @Override
    public Ref getRef(String s) throws SQLException {
        return getRef(findColumn(s));
    }

    @Override
    public Blob getBlob(String s) throws SQLException {
        return getBlob(findColumn(s));
    }

    @Override
    public Clob getClob(String s) throws SQLException {
        return getClob(findColumn(s));
    }

    @Override
    public Array getArray(String s) throws SQLException {
        return getArray(findColumn(s));
    }

    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {
        return getDate(i);
    }

    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {
        return getDate(findColumn(s), calendar);
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        return getTime(i);
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        return getTime(findColumn(s), calendar);
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        return getTimestamp(i);
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        return getTimestamp(findColumn(s), calendar);
    }

    @Override
    public java.net.URL getURL(int i) throws SQLException {
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
        return getURL(findColumn(s));
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
        return getNClob(findColumn(s));
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
        return getSQLXML(findColumn(s));
    }

    @Override
    public String getNString(int i) throws SQLException {
        return getString(i);
    }

    @Override
    public String getNString(String s) throws SQLException {
        return getNString(findColumn(s));
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
        return getNCharacterStream(findColumn(s));
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
        return getObject(findColumn(s), tClass);
    }
    @Override
    public Geometry getGeometry(int columnIndex) throws SQLException {
        Object field =  getObject(columnIndex);
        if(field==null) {
            return null;
        }
        if(field instanceof Geometry) {
            return (Geometry)field;
        } else {
            throw new SQLException("The column "+getMetaData().getColumnName(columnIndex)+ " is not a Geometry");
        }
    }

    @Override
    public Geometry getGeometry(String columnLabel) throws SQLException {
        Object field =  getObject(columnLabel);
        if(field==null) {
            return null;
        }
        if(field instanceof Geometry) {
            return (Geometry)field;
        } else {
            throw new SQLException("The column "+columnLabel+ " is not a Geometry");
        }
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException {
        if(tClass.isAssignableFrom(SpatialResultSet.class)) {
            return tClass.cast(this);
        } else {
            throw new SQLFeatureNotSupportedException("Not a RowSet wrapper");
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return aClass.isAssignableFrom(SpatialResultSet.class);
    }
}
