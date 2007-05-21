package org.gdms.data;

import java.io.IOException;

import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;

public class StatusCheckDecorator extends AbstractDataSource implements DataSource {

	private DataSource ds;

	public StatusCheckDecorator(DataSource ds) {
		this.ds = ds;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
		ds.setDataSourceFactory(dsf);
	}

	public void addEditionListener(EditionListener listener) {
		ds.addEditionListener(listener);
	}

	public void addField(String name, String driverType, String[] paramNames, String[] paramValues) throws DriverException {
		if (isOpen()) {
			ds.addField(name, driverType, paramNames, paramValues);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void addField(String name, String driverType) throws DriverException {
		if (isOpen()) {
			ds.addField(name, driverType);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		ds.addMetadataEditionListener(listener);
	}

	public void beginTrans() throws DriverException {
		ds.beginTrans();
	}

	public boolean canRedo() {
		return ds.canRedo();
	}

	public boolean canUndo() {
		return ds.canUndo();
	}

	public String check(int fieldId, Value value) throws DriverException {
		if (isOpen()) {
			return ds.check(fieldId, value);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void commitTrans() throws DriverException, FreeingResourcesException, NonEditableDataSourceException {
		if (isOpen()) {
			if (isEditable()) {
				ds.commitTrans();
			} else {
				throw new NonEditableDataSourceException();
			}
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void deleteRow(long rowId) throws DriverException {
		if (isOpen()) {
			ds.deleteRow(rowId);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public Metadata getDataSourceMetadata() throws DriverException {
		if (isOpen()) {
			return ds.getDataSourceMetadata();
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public int getDispatchingMode() {
		return ds.getDispatchingMode();
	}

	public ReadOnlyDriver getDriver() {
		return ds.getDriver();
	}

	public DriverMetadata getDriverMetadata() throws DriverException {
		if (isOpen()) {
			return ds.getDriverMetadata();
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		if (isOpen()) {
			return ds.getFieldIndexByName(fieldName);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
		if (isOpen()) {
			return ds.getFieldValue(rowIndex, fieldId);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public long getRowCount() throws DriverException {
		if (isOpen()) {
			return ds.getRowCount();
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public Number[] getScope(int dimension, String fieldName) throws DriverException {
		if (isOpen()) {
			return ds.getScope(dimension, fieldName);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public long[] getWhereFilter() throws IOException {
		return ds.getWhereFilter();
	}

	public void insertEmptyRow() throws DriverException {
		if (isOpen()) {
			ds.insertEmptyRow();
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		if (isOpen()) {
			ds.insertEmptyRowAt(index);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		if (isOpen()) {
			ds.insertFilledRow(values);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void insertFilledRowAt(long index, Value[] values) throws DriverException {
		if (isOpen()) {
			ds.insertFilledRowAt(index, values);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public boolean isModified() {
		if (isOpen()) {
			return ds.isModified();
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public boolean isOpen() {
		return ds.isOpen();
	}

	public void redo() throws DriverException {
		if (isOpen()) {
			ds.redo();
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void remove() throws DriverException {
		ds.remove();
	}

	public void removeEditionListener(EditionListener listener) {
		ds.removeEditionListener(listener);
	}

	public void removeField(int index) throws DriverException {
		if (isOpen()) {
			ds.removeField(index);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		ds.removeMetadataEditionListener(listener);
	}

	public void rollBackTrans() throws DriverException, AlreadyClosedException {
		ds.rollBackTrans();
	}

	public void saveData(DataSource ds) throws IllegalStateException, DriverException {
		if (isOpen()) {
			throw new IllegalStateException("The data source must be closed to call this method");
		} else {
			ds.saveData(ds);
		}
	}

	public void setFieldName(int index, String name) throws DriverException {
		if (isOpen()) {
			ds.setFieldName(index, name);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void setFieldValue(long row, int fieldId, Value value) throws DriverException {
		if (isOpen()) {
			ds.setFieldValue(row, fieldId, value);
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void undo() throws DriverException {
		if (isOpen()) {
			ds.undo();
		} else {
			throw new ClosedDataSourceException("The data source must be open to call this method");
		}
	}

	public void setDispatchingMode(int dispatchingMode) {
		ds.setDispatchingMode(dispatchingMode);
	}

	public String getAlias() {
		return ds.getAlias();
	}

	public DataSourceFactory getDataSourceFactory() {
		return ds.getDataSourceFactory();
	}

	public Memento getMemento() throws MementoException {
		return ds.getMemento();
	}

	public String getName() {
		return ds.getName();
	}

	public boolean isEditable() {
		return ds.isEditable();
	}
}