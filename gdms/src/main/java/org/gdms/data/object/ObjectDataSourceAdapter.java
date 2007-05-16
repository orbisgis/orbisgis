package org.gdms.data.object;

import java.io.IOException;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DriverDataSourceImpl;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.OpenCloseCounter;
import org.gdms.data.edition.EditableDataSource;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.MetadataEditionSupport;
import org.gdms.data.edition.RowOrientedEditionDataSourceImpl;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.ReadAccess;

public class ObjectDataSourceAdapter extends DataSourceCommonImpl implements
		EditableDataSource {
	private RowOrientedEditionDataSourceImpl rowOrientedEdition;

	private ObjectDriver driver;

	private OpenCloseCounter ocCounter;

	private MetadataEditionSupport mes;

	private ObjectDataSourceSupport objectSupport;

	private DriverDataSourceImpl driverDataSourceSupport;

	public ObjectDataSourceAdapter(String name, String alias,
			ObjectDriver driver) {
		super(name, alias);
		mes = new MetadataEditionSupport(this);
		rowOrientedEdition = new RowOrientedEditionDataSourceImpl(this, driver,
				mes);
		rowOrientedEdition.setDriver(driver);
		ocCounter = new OpenCloseCounter(this);
		objectSupport = new ObjectDataSourceSupport(driver);
		driverDataSourceSupport = new DriverDataSourceImpl(driver);
		this.driver = driver;
	}

	public int getFieldCount() throws DriverException {
		return mes.getFieldCount();
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		return mes.getFieldIndexByName(fieldName);
	}

	public void beginTrans() throws DriverException {
		if (ocCounter.start()) {
			driver.start();
			mes.start();
			rowOrientedEdition.beginTrans();
		}
	}

	public void commitTrans() throws DriverException, FreeingResourcesException {
		if (ocCounter.stop()) {
			try {
				driver.stop();
				driver.write(this);
				rowOrientedEdition.commitTrans();
			} catch (DriverException e) {
				ocCounter.start();
				throw e;
			}
		}
	}

	public void deleteRow(long rowId) throws DriverException {
		rowOrientedEdition.deleteRow(rowId);
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return rowOrientedEdition.getFieldValue(rowIndex, fieldId);
	}

	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return driver.getFieldValue(rowIndex, fieldId);
	}

	public long getRowCount() throws DriverException {
		return rowOrientedEdition.getRowCount();
	}

	public void insertEmptyRow() throws DriverException {
		rowOrientedEdition.insertEmptyRow();
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		rowOrientedEdition.insertEmptyRowAt(index);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		rowOrientedEdition.insertFilledRow(values);
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		rowOrientedEdition.insertFilledRowAt(index, values);
	}

	public void setDriver(ReadAccess driver) {
		rowOrientedEdition.setDriver(driver);
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		rowOrientedEdition.setFieldValue(row, fieldId, value);
	}

	public void rollBackTrans() throws DriverException, AlreadyClosedException {
		if (ocCounter.stop()) {
			try {
				driver.stop();
				rowOrientedEdition.rollBackTrans();
			} catch (DriverException e) {
				ocCounter.start();
				throw e;
			}
		}
	}

	public void saveData(DataSource ds) throws DriverException {
		ds.beginTrans();
		driver.write(ds);
		ds.rollBackTrans();
	}

	public String getFieldName(int fieldId) throws DriverException {
		return getDataSourceMetadata().getFieldName(fieldId);
	}

	public int getFieldType(int i) throws DriverException {
		return getDataSourceMetadata().getFieldType(i);
	}

	public void addEditionListener(EditionListener listener) {
		rowOrientedEdition.addEditionListener(listener);
	}

	public void removeEditionListener(EditionListener listener) {
		rowOrientedEdition.removeEditionListener(listener);
	}

	public void setDispatchingMode(int dispatchingMode) {
		rowOrientedEdition.setDispatchingMode(dispatchingMode);
	}

	public int getDispatchingMode() {
		return rowOrientedEdition.getDispatchingMode();
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		mes.addMetadataEditionListener(listener);
	}

	public void addField(String name, String type) throws DriverException {
		addField(name, type, new String[0], new String[0]);
	}

	public void addField(String name, String type, String[] paramNames,
			String[] paramValues) throws DriverException {
		mes.addField(name, type, paramNames, paramValues);
		rowOrientedEdition.addField();
	}

	public Metadata getDataSourceMetadata() {
		return mes.getDataSourceMetadata();
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		mes.removeMetadataEditionListener(listener);
	}

	public void removeField(int index) throws DriverException {
		mes.removeField(index);
		rowOrientedEdition.removeField(index);
	}

	public void setFieldName(int index, String name) throws DriverException {
		mes.setFieldName(index, name);
		rowOrientedEdition.setFieldName();
	}

	public int getOriginalFieldCount() throws DriverException {
		return mes.getOriginalFieldCount();
	}

	public Metadata getOriginalMetadata() throws DriverException {
		return objectSupport.getOriginalMetadata();
	}

	public DriverMetadata getDriverMetadata() throws DriverException {
		return mes.getDriverMetadata();
	}

	public int getType(String driverType) {
		return driverDataSourceSupport.getType(driverType);
	}

	public DriverMetadata getOriginalDriverMetadata() throws DriverException {
		return driver.getDriverMetadata();
	}

	public String check(int fieldId, Value value) throws DriverException {
		return driver.check(mes.getField(fieldId), value);
	}

	public ObjectDriver getDriver() {
		return driver;
	}

	public void endUndoRedoAction() {
		rowOrientedEdition.endUndoRedoAction();
	}

	public void startUndoRedoAction() {
		rowOrientedEdition.startUndoRedoAction();
	}

	public boolean isModified() {
		return rowOrientedEdition.isModified();
	}

	public long[] getWhereFilter() throws IOException {
		return null;
	}

	public boolean isOpen() {
		return ocCounter.isOpen();
	}
}
