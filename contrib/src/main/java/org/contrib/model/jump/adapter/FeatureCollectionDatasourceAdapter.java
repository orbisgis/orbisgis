package org.contrib.model.jump.adapter;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;

import org.contrib.model.jump.model.AttributeType;
import org.contrib.model.jump.model.Feature;
import org.contrib.model.jump.model.FeatureCollection;
import org.contrib.model.jump.model.FeatureSchema;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceListener;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.source.Source;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;

public class FeatureCollectionDatasourceAdapter implements DataSource {

	private FeatureCollection fc;

	public FeatureCollectionDatasourceAdapter(FeatureCollection fc) {
		this.fc = fc;
	}

	public void addDataSourceListener(DataSourceListener listener) {
		// TODO Auto-generated method stub

	}

	public void addEditionListener(EditionListener listener) {
		// TODO Auto-generated method stub

	}

	public void addField(String name, Type driverType) throws DriverException {

	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		// TODO Auto-generated method stub

	}

	public boolean canRedo() {
		return false;
	}

	public boolean canUndo() {
		return false;
	}

	public String check(int fieldId, Value value) throws DriverException {
		return null;
	}

	public void close() throws DriverException, AlreadyClosedException {

	}

	public void commit() throws DriverException, NonEditableDataSourceException {

	}

	public void deleteRow(long rowId) throws DriverException {

	}

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

	public byte[] getBinary(long row, String fieldName) throws DriverException {

		return null;
	}

	public byte[] getBinary(long row, int fieldId) throws DriverException {

		return null;
	}

	public boolean getBoolean(long row, String fieldName)
			throws DriverException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getBoolean(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return false;
	}

	public byte getByte(long row, String fieldName) throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getByte(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Commiter getCommiter() {
		// TODO Auto-generated method stub
		return null;
	}

	public DataSourceFactory getDataSourceFactory() {
		return null;
	}

	public Date getDate(long row, String fieldName) throws DriverException {
		return null;
	}

	public Date getDate(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getDispatchingMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(long row, String fieldName) throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * todo : add a reference to datasource driver
	 */
	/*
	 * public ReadOnlyDriver getDriver() { return fc.getDriver(); }
	 */
	public int getFieldCount() throws DriverException {
		return getMetadata().getFieldCount();
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

	public String getFieldName(int fieldId) throws DriverException {
		return fc.getFeatureSchema().getAttributeName(fieldId);
	}

	public String[] getFieldNames() throws DriverException {

		String[] fieldNames = new String[getFieldCount()];
		for (int i = 0; i < getFieldCount(); i++) {
			fieldNames[i] = getFieldName(i);
		}
		return fieldNames;
	}

	public Type getFieldType(int i) throws DriverException {

		FeatureSchema fSchema = fc.getFeatureSchema();
		if (fSchema instanceof FeatureSchemaAdapter) {
			return ((FeatureSchemaAdapter) fSchema).getDs().getMetadata()
					.getFieldType(i);
		} else {
			AttributeType at = fSchema.getAttributeType(i);
			if (at == AttributeType.DATE) {
				return TypeFactory.createType(Type.DATE);
			} else if (at == AttributeType.DOUBLE) {
				return TypeFactory.createType(Type.DOUBLE);
			} else if (at == AttributeType.GEOMETRY) {
				return TypeFactory.createType(Type.GEOMETRY);
			} else if (at == AttributeType.INTEGER) {
				return TypeFactory.createType(Type.INT);
			} else if (at == AttributeType.STRING) {
				return TypeFactory.createType(Type.STRING);
			} else if (at == AttributeType.OBJECT) {
				return TypeFactory.createType(Type.STRING);
			}

			throw new RuntimeException("OpenUMP attribute type unknow"); //$NON-NLS-1$
		}
	}

	public float getFloat(long row, String fieldName) throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloat(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInt(long row, String fieldName) throws DriverException {
		return getInt(row, getFieldIndexByName(fieldName));
	}

	public int getInt(long row, int fieldId) throws DriverException {
		try {
			return getFieldValue(row, fieldId).getAsInt();
		} catch (IncompatibleTypesException e) {
			throw new DriverException(e);
		}
	}

	public long getLong(long row, String fieldName) throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Metadata getMetadata() throws DriverException {
		DefaultMetadata metadata = new DefaultMetadata();
		for (int i = 0; i < fc.getFeatureSchema().getAttributeCount(); i++) {
			metadata.addField(getFieldName(i), getFieldType(i));
		}
		return metadata;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueCollection getPK(int row) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getReferencedSources() {
		// TODO Auto-generated method stub
		return null;
	}

	public Value[] getRow(long rowIndex) throws DriverException {
		Value[] values = new Value[getMetadata().getFieldCount()];
		for (int i = 0; i < values.length; i++) {
			values[i] = getFieldValue(rowIndex, i);
		}
		return values;
	}

	public short getShort(long row, String fieldName) throws DriverException {
		return 0;
	}

	public short getShort(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Source getSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(long row, String fieldName) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(long row, String fieldName) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(long row, String fieldName)
			throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public long[] getWhereFilter() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void insertEmptyRow() throws DriverException {
		// TODO Auto-generated method stub

	}

	public void insertEmptyRowAt(long index) throws DriverException {
		// TODO Auto-generated method stub

	}

	public void insertFilledRow(Value[] values) throws DriverException,
			IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNull(long row, int fieldId) throws DriverException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isNull(long row, String fieldName) throws DriverException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	public void open() throws DriverException {

	}

	public void printStack() {
		// TODO Auto-generated method stub

	}

	public Iterator<Integer> queryIndex(IndexQuery queryIndex)
			throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public void redo() throws DriverException {

	}

	public void removeDataSourceListener(DataSourceListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeEditionListener(EditionListener listener) {
		// TODO Auto-generated method stub

	}

	public void removeField(int index) throws DriverException {
		// TODO Auto-generated method stub

	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		// TODO Auto-generated method stub

	}

	public void saveData(DataSource ds) throws IllegalStateException,
			DriverException {
		// TODO Auto-generated method stub

	}

	public void setBinary(long row, String fieldName, byte[] value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setBinary(long row, int fieldId, byte[] value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setBoolean(long row, String fieldName, boolean value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setBoolean(long row, int fieldId, boolean value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setByte(long row, String fieldName, byte value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setByte(long row, int fieldId, byte value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
		// TODO Auto-generated method stub

	}

	public void setDate(long row, String fieldName, Date value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setDate(long row, int fieldId, Date value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setDispatchingMode(int dispatchingMode) {
		// TODO Auto-generated method stub

	}

	public void setDouble(long row, String fieldName, double value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setDouble(long row, int fieldId, double value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setFieldName(int index, String name) throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setFloat(long row, String fieldName, float value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setFloat(long row, int fieldId, float value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setInt(long row, String fieldName, int value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setInt(long row, int fieldId, int value) throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setLong(long row, String fieldName, long value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setLong(long row, int fieldId, long value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setShort(long row, String fieldName, short value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setShort(long row, int fieldId, short value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setString(long row, String fieldName, String value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setString(long row, int fieldId, String value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setTime(long row, String fieldName, Time value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setTime(long row, int fieldId, Time value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setTimestamp(long row, String fieldName, Timestamp value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void setTimestamp(long row, int fieldId, Timestamp value)
			throws DriverException {
		// TODO Auto-generated method stub

	}

	public void syncWithSource() throws DriverException {
	}

	public void undo() throws DriverException {
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		Feature f = ((Feature) fc.getFeatures().get((int) rowIndex));
		Object o = f.getAttribute(fieldId);
		if (o == null) {
			return ValueFactory.createNullValue();
		}
		if (o instanceof Geometry) {
			if (((Geometry) o).isEmpty()) {
				return ValueFactory.createNullValue();
			} else {
				return (Value) o;
			}
		} else {
			if (o instanceof Value) {
				return (Value) o;
			} else {
				AttributeType at = fc.getFeatureSchema().getAttributeType(
						fieldId);
				if (at == AttributeType.DATE) {
					return ValueFactory.createValue((Date) o);
				} else if (at == AttributeType.DOUBLE) {
					return ValueFactory.createValue((Double) o);
				} else if (at == AttributeType.INTEGER) {
					return ValueFactory.createValue((Integer) o);
				} else if (at == AttributeType.STRING) {
					return ValueFactory.createValue((String) o);
				} else if (at == AttributeType.OBJECT) {
					return ValueFactory.createValue(o.toString());
				}

				throw new RuntimeException("OpenJUMP attribute type unknown");

			}
		}
	}

	public long getRowCount() throws DriverException {
		return fc.size();
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

	public ReadOnlyDriver getDriver() {
		return null;
	}

}
