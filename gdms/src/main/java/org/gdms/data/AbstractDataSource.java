package org.gdms.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Type;
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
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

public abstract class AbstractDataSource implements DataSource {

	/**
	 * @see org.gdms.data.DataSource#remove()
	 */
	public void remove() throws DriverException {
		getDataSourceFactory().remove(this);
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
		Metadata dataSourceMetadata = getMetadata();
		String[] ret = new String[dataSourceMetadata.getFieldCount()];

		for (int i = 0; i < ret.length; i++) {
			ret[i] = dataSourceMetadata.getFieldName(i);
		}

		return ret;
	}

	public Type getFieldType(int fieldId) throws DriverException {
		return getMetadata().getFieldType(fieldId);
	}

	/**
	 * @see org.gdms.data.DataSource#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		Metadata dataSourceMetadata = getMetadata();
		return dataSourceMetadata.getFieldCount();
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		Metadata metadata = getMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldName(i).equals(fieldName)) {
				return i;
			}
		}

		return -1;
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

	public ValueCollection getPK(int rowIndex) throws DriverException {
		int[] fieldsId = MetadataUtilities
				.getPKIndices(getMetadata());
		if (fieldsId.length > 0) {
			Value[] pks = new Value[fieldsId.length];

			for (int i = 0; i < pks.length; i++) {
				pks[i] = getFieldValue(rowIndex, fieldsId[i]);
			}

			return ValueFactory.createValue(pks);
		} else {
			return ValueFactory.createValue(new Value[] { ValueFactory
					.createValue(rowIndex) });
		}
	}

	public void deleteRow(long rowId) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void insertEmptyRow() throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void addEditionListener(EditionListener listener) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void removeEditionListener(EditionListener listener) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void setDispatchingMode(int dispatchingMode) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public int getDispatchingMode() {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void endUndoRedoAction() {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void startUndoRedoAction() {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void addField(String name, Type type) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void removeField(int fieldId) throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void setFieldName(int fieldId, String newFieldName)
			throws DriverException {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		throw new UnsupportedOperationException("The DataSource wasn't "
				+ "retrieved with edition capabilities");
	}

	public boolean isModified() {
		return false;
	}

}
