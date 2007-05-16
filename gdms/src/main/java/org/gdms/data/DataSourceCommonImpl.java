package org.gdms.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.DataSourceLayerMemento;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.values.BinaryValue;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.DateValue;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.TimeValue;
import org.gdms.data.values.TimestampValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadWriteDriver;

/**
 * Base class with the common implementation for all DataSource implementations
 * and methods that invoke other DataSource methods
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public abstract class DataSourceCommonImpl implements DataSource {

	private String name;

	private String alias;

	private Metadata metadata;

	protected DataSourceFactory dsf;

	public DataSourceCommonImpl(String name, String alias) {
		this.name = name;
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public String getName() {
		return name;
	}

	/**
	 * @see org.gdbms.data.DataSource#getDataSourceFactory()
	 */
	public DataSourceFactory getDataSourceFactory() {
		return dsf;
	}

	/**
	 * @see org.gdbms.data.DataSource#setDataSourceFactory(DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	/**
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		DataSourceLayerMemento m = new DataSourceLayerMemento(getName(),
				getAlias());

		return m;
	}

	/**
	 * th.data.DataSource#remove()
	 */
	public void remove() throws DriverException {
		dsf.remove(this);
	}

	private Metadata getMetadata() throws DriverException {
		if (metadata == null) {
			metadata = getDataSourceMetadata();
		}

		return metadata;
	}

	/**
	 * @see org.gdms.data.DataSource#getRow(long)
	 */
	public Value[] getRow(long rowIndex) throws DriverException {
		Value[] ret = new Value[getMetadata().getFieldCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = getFieldValue(rowIndex, i);
		}

		return ret;
	}

	/**
	 * @see org.gdms.data.DataSource#getFieldNames()
	 */
	public String[] getFieldNames() throws DriverException {
		String[] ret = new String[getMetadata().getFieldCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = getMetadata().getFieldName(i);
		}

		return ret;
	}

	/**
	 * gets a string representation of this datasource
	 * 
	 * @return String
	 * 
	 * @throws DriverException
	 */
	public String getAsString() throws DriverException {

		StringBuffer aux = new StringBuffer();
		int fc = getMetadata().getFieldCount();
		int rc = (int) getRowCount();

		for (int i = 0; i < fc; i++) {
			aux.append(getMetadata().getFieldName(i)).append("\t");
		}
		aux.append("\n");
		for (int row = 0; row < rc; row++) {
			for (int j = 0; j < fc; j++) {
				aux.append(getFieldValue(row, j)).append("\t");
			}
			aux.append("\n");
		}

		return aux.toString();
	}

	/**
	 * Redoes the last undone edition action
	 * 
	 * @throws DriverException
	 */
	public void redo() throws DriverException {
		throw new UnsupportedOperationException(
				"Not supported. Try to obtain the DataSource with the DataSourceFactory.UNDOABLE constant");
	}

	/**
	 * Undoes the last edition action
	 * 
	 * @throws DriverException
	 */
	public void undo() throws DriverException {
		throw new UnsupportedOperationException(
				"Not supported. Try to obtain the DataSource with the DataSourceFactory.UNDOABLE constant");
	}

	/**
	 * @return true if there is an edition action to redo
	 * 
	 */
	public boolean canRedo() {
		return false;
	}

	/**
	 * @return true if there is an edition action to undo
	 * 
	 */
	public boolean canUndo() {
		return false;
	}

	public int getInt(long row, String fieldName) throws DriverException {
		return getInt(row, getFieldIndexByName(fieldName));
	}

	public int getInt(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return 0;
		} else {
			return ((NumericValue) v).intValue();
		}
	}

	public byte[] getBinary(long row, String fieldName) throws DriverException {
		return getBinary(row, getFieldIndexByName(fieldName));
	}

	public byte[] getBinary(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return null;
		} else {
			return ((BinaryValue) v).getValue();
		}
	}

	public boolean getBoolean(long row, String fieldName)
			throws DriverException {
		return getBoolean(row, getFieldIndexByName(fieldName));
	}

	public boolean getBoolean(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return false;
		} else {
			return ((BooleanValue) v).getValue();
		}
	}

	public byte getByte(long row, String fieldName) throws DriverException {
		return getByte(row, getFieldIndexByName(fieldName));
	}

	public byte getByte(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return 0;
		} else {
			return ((NumericValue) v).byteValue();
		}
	}

	public Date getDate(long row, String fieldName) throws DriverException {
		return getDate(row, getFieldIndexByName(fieldName));
	}

	public Date getDate(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return null;
		} else {
			return ((DateValue) v).getValue();
		}
	}

	public double getDouble(long row, String fieldName) throws DriverException {
		return getDouble(row, getFieldIndexByName(fieldName));
	}

	public double getDouble(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return 0;
		} else {
			return ((NumericValue) v).doubleValue();
		}
	}

	public float getFloat(long row, String fieldName) throws DriverException {
		return getFloat(row, getFieldIndexByName(fieldName));
	}

	public float getFloat(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return 0;
		} else {
			return ((NumericValue) v).floatValue();
		}
	}

	public long getLong(long row, String fieldName) throws DriverException {
		return getLong(row, getFieldIndexByName(fieldName));
	}

	public long getLong(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return 0;
		} else {
			return ((LongValue) v).getValue();
		}
	}

	public short getShort(long row, String fieldName) throws DriverException {
		return getShort(row, getFieldIndexByName(fieldName));
	}

	public short getShort(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return 0;
		} else {
			return ((NumericValue) v).shortValue();
		}
	}

	public String getString(long row, String fieldName) throws DriverException {
		return getString(row, getFieldIndexByName(fieldName));
	}

	public String getString(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return null;
		} else {
			return ((StringValue) v).getValue();
		}
	}

	public Timestamp getTimestamp(long row, String fieldName)
			throws DriverException {
		return getTimestamp(row, getFieldIndexByName(fieldName));
	}

	public Timestamp getTimestamp(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return null;
		} else {
			return ((TimestampValue) v).getValue();
		}
	}

	public Time getTime(long row, String fieldName) throws DriverException {
		return getTime(row, getFieldIndexByName(fieldName));
	}

	public Time getTime(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return null;
		} else {
			return ((TimeValue) v).getValue();
		}
	}

	public void setInt(long row, String fieldName, int value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setInt(long row, int fieldId, int value) throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setBinary(long row, String fieldName, byte[] value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setBinary(long row, int fieldId, byte[] value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setBoolean(long row, String fieldName, boolean value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setBoolean(long row, int fieldId, boolean value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setByte(long row, String fieldName, byte value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setByte(long row, int fieldId, byte value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setDate(long row, String fieldName, Date value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setDate(long row, int fieldId, Date value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setDouble(long row, String fieldName, double value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setDouble(long row, int fieldId, double value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setFloat(long row, String fieldName, float value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setFloat(long row, int fieldId, float value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setLong(long row, String fieldName, long value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setLong(long row, int fieldId, long value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setShort(long row, String fieldName, short value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setShort(long row, int fieldId, short value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setString(long row, String fieldName, String value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setString(long row, int fieldId, String value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setTimestamp(long row, String fieldName, Timestamp value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setTimestamp(long row, int fieldId, Timestamp value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public void setTime(long row, String fieldName, Time value)
			throws DriverException {
		setFieldValue(row, getFieldIndexByName(fieldName), ValueFactory
				.createValue(value));
	}

	public void setTime(long row, int fieldId, Time value)
			throws DriverException {
		setFieldValue(row, fieldId, ValueFactory.createValue(value));
	}

	public boolean isNull(long row, int fieldId) throws DriverException {
		Value v = getFieldValue(row, fieldId);
		if (v instanceof NullValue) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isNull(long row, String fieldName) throws DriverException {
		return isNull(row, getFieldIndexByName(fieldName));
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
		return getDriver().getScope(dimension, fieldName);
	}

	public boolean isEditable() {
		final ReadOnlyDriver driver = getDriver();

		return ((driver instanceof ReadWriteDriver) && ((ReadWriteDriver) driver)
				.isEditable());
	}
}