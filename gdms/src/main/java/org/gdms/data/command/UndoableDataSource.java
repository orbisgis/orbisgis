package org.gdms.data.command;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.edition.EditableDataSource;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.GDBMSDriver;


public class UndoableDataSource extends DataSourceCommonImpl implements EditableDataSource {

	private CommandImpl ci;
	private EditableDataSource ds;

    public UndoableDataSource(DataSource ds) {
        super(ds.getName(), ds.getAlias());
    	this.ds = (EditableDataSource) ds;
    	ci = new CommandImpl(this.ds);
    }

	public void addField(String name, String type) throws DriverException {
		ci.addField(name, type);
	}

	public boolean canRedo() {
		return ci.canRedo();
	}

	public boolean canUndo() {
		return ci.canUndo();
	}

	public void deleteRow(long rowId) throws DriverException {
		ci.deleteRow(rowId);
	}

	public void insertEmptyRow() throws DriverException {
		ci.insertEmptyRow();
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		ci.insertEmptyRowAt(index);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		ci.insertFilledRow(values);
	}

	public void insertFilledRowAt(long index, Value[] values) throws DriverException {
		ci.insertFilledRowAt(index, values);
	}

	public void redo() throws DriverException {
		ci.redo();
	}

	public void removeField(int index) throws DriverException {
		ci.removeField(index);
	}

	public void rollBackTrans() throws DriverException {
		ci.rollBackTrans();
	}

	public void setFieldName(int index, String name) throws DriverException {
		ci.setFieldName(index, name);
	}

	public void setFieldValue(long row, int fieldId, Value value) throws DriverException {
		ci.setFieldValue(row, fieldId, value);
	}

	public void undo() throws DriverException {
		ci.undo();
	}

	public void addEditionListener(EditionListener listener) {
		ds.addEditionListener(listener);
	}

	public void addField(String name, String driverType, String[] paramNames, String[] paramValues) throws DriverException {
		ds.addField(name, driverType, paramNames, paramValues);
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		ds.addMetadataEditionListener(listener);
	}

	public void beginTrans() throws DriverException {
		ds.beginTrans();
	}

	public String check(int fieldId, Value value) throws DriverException {
		return ds.check(fieldId, value);
	}

	public void commitTrans() throws DriverException, FreeingResourcesException {
		ds.commitTrans();
	}

	public void endUndoRedoAction() {
		ds.endUndoRedoAction();
	}

	public String getAlias() {
		return ds.getAlias();
	}

	public String getAsString() throws DriverException {
		return ds.getAsString();
	}

	public byte[] getBinary(long row, int fieldId) throws DriverException {
		return ds.getBinary(row, fieldId);
	}

	public byte[] getBinary(long row, String fieldName) throws DriverException {
		return ds.getBinary(row, fieldName);
	}

	public boolean getBoolean(long row, int fieldId) throws DriverException {
		return ds.getBoolean(row, fieldId);
	}

	public boolean getBoolean(long row, String fieldName) throws DriverException {
		return ds.getBoolean(row, fieldName);
	}

	public byte getByte(long row, int fieldId) throws DriverException {
		return ds.getByte(row, fieldId);
	}

	public byte getByte(long row, String fieldName) throws DriverException {
		return ds.getByte(row, fieldName);
	}

	public DataSourceFactory getDataSourceFactory() {
		return ds.getDataSourceFactory();
	}

	public Metadata getDataSourceMetadata() throws DriverException {
		return ds.getDataSourceMetadata();
	}

	public Date getDate(long row, int fieldId) throws DriverException {
		return ds.getDate(row, fieldId);
	}

	public Date getDate(long row, String fieldName) throws DriverException {
		return ds.getDate(row, fieldName);
	}

	public int getDispatchingMode() {
		return ds.getDispatchingMode();
	}

	public double getDouble(long row, int fieldId) throws DriverException {
		return ds.getDouble(row, fieldId);
	}

	public double getDouble(long row, String fieldName) throws DriverException {
		return ds.getDouble(row, fieldName);
	}

	public GDBMSDriver getDriver() {
		return ds.getDriver();
	}

	public DriverMetadata getDriverMetadata() throws DriverException {
		return ds.getDriverMetadata();
	}

	public int getFieldCount() throws DriverException {
		return ds.getFieldCount();
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		return ds.getFieldIndexByName(fieldName);
	}

	public String getFieldName(int fieldId) throws DriverException {
		return ds.getFieldName(fieldId);
	}

	public String[] getFieldNames() throws DriverException {
		return ds.getFieldNames();
	}

	public int getFieldType(int i) throws DriverException {
		return ds.getFieldType(i);
	}

	public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
		return ds.getFieldValue(rowIndex, fieldId);
	}

	public float getFloat(long row, int fieldId) throws DriverException {
		return ds.getFloat(row, fieldId);
	}

	public float getFloat(long row, String fieldName) throws DriverException {
		return ds.getFloat(row, fieldName);
	}

	public int getInt(long row, int fieldId) throws DriverException {
		return ds.getInt(row, fieldId);
	}

	public int getInt(long row, String fieldName) throws DriverException {
		return ds.getInt(row, fieldName);
	}

	public long getLong(long row, int fieldId) throws DriverException {
		return ds.getLong(row, fieldId);
	}

	public long getLong(long row, String fieldName) throws DriverException {
		return ds.getLong(row, fieldName);
	}

	public Memento getMemento() throws MementoException {
		return ds.getMemento();
	}

	public String getName() {
		return ds.getName();
	}

	public DriverMetadata getOriginalDriverMetadata() throws DriverException {
		return ds.getOriginalDriverMetadata();
	}

	public int getOriginalFieldCount() throws DriverException {
		return ds.getOriginalFieldCount();
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId) throws DriverException {
		return ds.getOriginalFieldValue(rowIndex, fieldId);
	}

	public Metadata getOriginalMetadata() throws DriverException {
		return ds.getOriginalMetadata();
	}

	public Value[] getRow(long rowIndex) throws DriverException {
		return ds.getRow(rowIndex);
	}

	public long getRowCount() throws DriverException {
		return ds.getRowCount();
	}

	public short getShort(long row, int fieldId) throws DriverException {
		return ds.getShort(row, fieldId);
	}

	public short getShort(long row, String fieldName) throws DriverException {
		return ds.getShort(row, fieldName);
	}

	public String getString(long row, int fieldId) throws DriverException {
		return ds.getString(row, fieldId);
	}

	public String getString(long row, String fieldName) throws DriverException {
		return ds.getString(row, fieldName);
	}

	public Time getTime(long row, int fieldId) throws DriverException {
		return ds.getTime(row, fieldId);
	}

	public Time getTime(long row, String fieldName) throws DriverException {
		return ds.getTime(row, fieldName);
	}

	public Timestamp getTimestamp(long row, int fieldId) throws DriverException {
		return ds.getTimestamp(row, fieldId);
	}

	public Timestamp getTimestamp(long row, String fieldName) throws DriverException {
		return ds.getTimestamp(row, fieldName);
	}

	public int getType(String driverType) {
		return ds.getType(driverType);
	}

	public long[] getWhereFilter() throws IOException {
		return ds.getWhereFilter();
	}

	public boolean isModified() {
		return ds.isModified();
	}

	public boolean isNull(long row, int fieldId) throws DriverException {
		return ds.isNull(row, fieldId);
	}

	public boolean isNull(long row, String fieldName) throws DriverException {
		return ds.isNull(row, fieldName);
	}

	public boolean isOpen() {
		return ds.isOpen();
	}

	public void remove() throws DriverException {
		ds.remove();
	}

	public void removeEditionListener(EditionListener listener) {
		ds.removeEditionListener(listener);
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		ds.removeMetadataEditionListener(listener);
	}

	public void saveData(DataSource ds) throws DriverException {
		ds.saveData(ds);
	}

	public void setBinary(long row, int fieldId, byte[] value) throws DriverException {
		ds.setBinary(row, fieldId, value);
	}

	public void setBinary(long row, String fieldName, byte[] value) throws DriverException {
		ds.setBinary(row, fieldName, value);
	}

	public void setBoolean(long row, int fieldId, boolean value) throws DriverException {
		ds.setBoolean(row, fieldId, value);
	}

	public void setBoolean(long row, String fieldName, boolean value) throws DriverException {
		ds.setBoolean(row, fieldName, value);
	}

	public void setByte(long row, int fieldId, byte value) throws DriverException {
		ds.setByte(row, fieldId, value);
	}

	public void setByte(long row, String fieldName, byte value) throws DriverException {
		ds.setByte(row, fieldName, value);
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
		ds.setDataSourceFactory(dsf);
	}

	public void setDate(long row, int fieldId, Date value) throws DriverException {
		ds.setDate(row, fieldId, value);
	}

	public void setDate(long row, String fieldName, Date value) throws DriverException {
		ds.setDate(row, fieldName, value);
	}

	public void setDispatchingMode(int dispatchingMode) {
		ds.setDispatchingMode(dispatchingMode);
	}

	public void setDouble(long row, int fieldId, double value) throws DriverException {
		ds.setDouble(row, fieldId, value);
	}

	public void setDouble(long row, String fieldName, double value) throws DriverException {
		ds.setDouble(row, fieldName, value);
	}

	public void setFloat(long row, int fieldId, float value) throws DriverException {
		ds.setFloat(row, fieldId, value);
	}

	public void setFloat(long row, String fieldName, float value) throws DriverException {
		ds.setFloat(row, fieldName, value);
	}

	public void setInt(long row, int fieldId, int value) throws DriverException {
		ds.setInt(row, fieldId, value);
	}

	public void setInt(long row, String fieldName, int value) throws DriverException {
		ds.setInt(row, fieldName, value);
	}

	public void setLong(long row, int fieldId, long value) throws DriverException {
		ds.setLong(row, fieldId, value);
	}

	public void setLong(long row, String fieldName, long value) throws DriverException {
		ds.setLong(row, fieldName, value);
	}

	public void setShort(long row, int fieldId, short value) throws DriverException {
		ds.setShort(row, fieldId, value);
	}

	public void setShort(long row, String fieldName, short value) throws DriverException {
		ds.setShort(row, fieldName, value);
	}

	public void setString(long row, int fieldId, String value) throws DriverException {
		ds.setString(row, fieldId, value);
	}

	public void setString(long row, String fieldName, String value) throws DriverException {
		ds.setString(row, fieldName, value);
	}

	public void setTime(long row, int fieldId, Time value) throws DriverException {
		ds.setTime(row, fieldId, value);
	}

	public void setTime(long row, String fieldName, Time value) throws DriverException {
		ds.setTime(row, fieldName, value);
	}

	public void setTimestamp(long row, int fieldId, Timestamp value) throws DriverException {
		ds.setTimestamp(row, fieldId, value);
	}

	public void setTimestamp(long row, String fieldName, Timestamp value) throws DriverException {
		ds.setTimestamp(row, fieldName, value);
	}

	public void startUndoRedoAction() {
		ds.startUndoRedoAction();
	}

}
