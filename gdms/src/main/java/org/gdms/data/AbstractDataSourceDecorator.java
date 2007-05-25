/**
 * 
 */
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

/**
 * @author leduc
 * 
 */
public class AbstractDataSourceDecorator extends AbstractDataSource {
	private InternalDataSource internalDataSource;

	public AbstractDataSourceDecorator(
			final InternalDataSource internalDataSource) {
		this.internalDataSource = internalDataSource;
	}

	/**
	 * @return the internalDataSource
	 */
	public InternalDataSource getDataSource() {
		return internalDataSource;
	}

	/**
	 * @param listener
	 * @see org.gdms.data.InternalDataSource#addEditionListener(org.gdms.data.edition.EditionListener)
	 */
	public void addEditionListener(EditionListener listener) {
		internalDataSource.addEditionListener(listener);
	}

	/**
	 * @param name
	 * @param driverType
	 * @param paramNames
	 * @param paramValues
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#addField(java.lang.String, java.lang.String, java.lang.String[], java.lang.String[])
	 */
	public void addField(String name, String driverType, String[] paramNames, String[] paramValues) throws DriverException {
		internalDataSource.addField(name, driverType, paramNames, paramValues);
	}

	/**
	 * @param name
	 * @param driverType
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#addField(java.lang.String, java.lang.String)
	 */
	public void addField(String name, String driverType) throws DriverException {
		internalDataSource.addField(name, driverType);
	}

	/**
	 * @param listener
	 * @see org.gdms.data.InternalDataSource#addMetadataEditionListener(org.gdms.data.edition.MetadataEditionListener)
	 */
	public void addMetadataEditionListener(MetadataEditionListener listener) {
		internalDataSource.addMetadataEditionListener(listener);
	}

	/**
	 * @throws DriverException
	 * @throws AlreadyClosedException
	 * @see org.gdms.data.InternalDataSource#cancel()
	 */
	public void cancel() throws DriverException, AlreadyClosedException {
		internalDataSource.cancel();
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#canRedo()
	 */
	public boolean canRedo() {
		return internalDataSource.canRedo();
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#canUndo()
	 */
	public boolean canUndo() {
		return internalDataSource.canUndo();
	}

	/**
	 * @param fieldId
	 * @param value
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#check(int, org.gdms.data.values.Value)
	 */
	public String check(int fieldId, Value value) throws DriverException {
		return internalDataSource.check(fieldId, value);
	}

	/**
	 * @throws DriverException
	 * @throws FreeingResourcesException
	 * @throws NonEditableDataSourceException
	 * @see org.gdms.data.InternalDataSource#commit()
	 */
	public void commit() throws DriverException, FreeingResourcesException, NonEditableDataSourceException {
		internalDataSource.commit();
	}

	/**
	 * @param rowId
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#deleteRow(long)
	 */
	public void deleteRow(long rowId) throws DriverException {
		internalDataSource.deleteRow(rowId);
	}

	/**
	 * 
	 * @see org.gdms.data.InternalDataSource#endUndoRedoAction()
	 */
	public void endUndoRedoAction() {
		internalDataSource.endUndoRedoAction();
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#getAlias()
	 */
	public String getAlias() {
		return internalDataSource.getAlias();
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#getDataSourceFactory()
	 */
	public DataSourceFactory getDataSourceFactory() {
		return internalDataSource.getDataSourceFactory();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getDataSourceMetadata()
	 */
	public Metadata getDataSourceMetadata() throws DriverException {
		return internalDataSource.getDataSourceMetadata();
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#getDispatchingMode()
	 */
	public int getDispatchingMode() {
		return internalDataSource.getDispatchingMode();
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#getDriver()
	 */
	public ReadOnlyDriver getDriver() {
		return internalDataSource.getDriver();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getDriverMetadata()
	 */
	public DriverMetadata getDriverMetadata() throws DriverException {
		return internalDataSource.getDriverMetadata();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		return internalDataSource.getFieldCount();
	}

	/**
	 * @param fieldName
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		return internalDataSource.getFieldIndexByName(fieldName);
	}

	/**
	 * @param fieldId
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws DriverException {
		return internalDataSource.getFieldName(fieldId);
	}

	/**
	 * @param i
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getFieldType(int)
	 */
	public int getFieldType(int i) throws DriverException {
		return internalDataSource.getFieldType(i);
	}

	/**
	 * @param rowIndex
	 * @param fieldId
	 * @return
	 * @throws DriverException
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
		return internalDataSource.getFieldValue(rowIndex, fieldId);
	}

	/**
	 * @return
	 * @throws MementoException
	 * @see org.gdms.data.InternalDataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return internalDataSource.getMemento();
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#getName()
	 */
	public String getName() {
		return internalDataSource.getName();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getOriginalDriverMetadata()
	 */
	public DriverMetadata getOriginalDriverMetadata() throws DriverException {
		return internalDataSource.getOriginalDriverMetadata();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getOriginalFieldCount()
	 */
	public int getOriginalFieldCount() throws DriverException {
		return internalDataSource.getOriginalFieldCount();
	}

	/**
	 * @param rowIndex
	 * @param fieldId
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getOriginalFieldValue(long, int)
	 */
	public Value getOriginalFieldValue(long rowIndex, int fieldId) throws DriverException {
		return internalDataSource.getOriginalFieldValue(rowIndex, fieldId);
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getOriginalMetadata()
	 */
	public Metadata getOriginalMetadata() throws DriverException {
		return internalDataSource.getOriginalMetadata();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#getOriginalRowCount()
	 */
	public long getOriginalRowCount() throws DriverException {
		return internalDataSource.getOriginalRowCount();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return internalDataSource.getRowCount();
	}

	/**
	 * @param dimension
	 * @param fieldName
	 * @return
	 * @throws DriverException
	 * @see org.gdms.driver.ReadAccess#getScope(int, java.lang.String)
	 */
	public Number[] getScope(int dimension, String fieldName) throws DriverException {
		return internalDataSource.getScope(dimension, fieldName);
	}

	/**
	 * @param driverType
	 * @return
	 * @see org.gdms.data.InternalDataSource#getType(java.lang.String)
	 */
	public int getType(String driverType) {
		return internalDataSource.getType(driverType);
	}

	/**
	 * @return
	 * @throws IOException
	 * @see org.gdms.data.InternalDataSource#getWhereFilter()
	 */
	public long[] getWhereFilter() throws IOException {
		return internalDataSource.getWhereFilter();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#insertEmptyRow()
	 */
	public void insertEmptyRow() throws DriverException {
		internalDataSource.insertEmptyRow();
	}

	/**
	 * @param index
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#insertEmptyRowAt(long)
	 */
	public void insertEmptyRowAt(long index) throws DriverException {
		internalDataSource.insertEmptyRowAt(index);
	}

	/**
	 * @param values
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#insertFilledRow(org.gdms.data.values.Value[])
	 */
	public void insertFilledRow(Value[] values) throws DriverException {
		internalDataSource.insertFilledRow(values);
	}

	/**
	 * @param index
	 * @param values
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#insertFilledRowAt(long, org.gdms.data.values.Value[])
	 */
	public void insertFilledRowAt(long index, Value[] values) throws DriverException {
		internalDataSource.insertFilledRowAt(index, values);
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#isEditable()
	 */
	public boolean isEditable() {
		return internalDataSource.isEditable();
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#isModified()
	 */
	public boolean isModified() {
		return internalDataSource.isModified();
	}

	/**
	 * @return
	 * @see org.gdms.data.InternalDataSource#isOpen()
	 */
	public boolean isOpen() {
		return internalDataSource.isOpen();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#open()
	 */
	public void open() throws DriverException {
		internalDataSource.open();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#redo()
	 */
	public void redo() throws DriverException {
		internalDataSource.redo();
	}

	/**
	 * @param listener
	 * @see org.gdms.data.InternalDataSource#removeEditionListener(org.gdms.data.edition.EditionListener)
	 */
	public void removeEditionListener(EditionListener listener) {
		internalDataSource.removeEditionListener(listener);
	}

	/**
	 * @param index
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#removeField(int)
	 */
	public void removeField(int index) throws DriverException {
		internalDataSource.removeField(index);
	}

	/**
	 * @param listener
	 * @see org.gdms.data.InternalDataSource#removeMetadataEditionListener(org.gdms.data.edition.MetadataEditionListener)
	 */
	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		internalDataSource.removeMetadataEditionListener(listener);
	}

	/**
	 * @param ds
	 * @throws IllegalStateException
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#saveData(org.gdms.data.InternalDataSource)
	 */
	public void saveData(InternalDataSource ds) throws IllegalStateException, DriverException {
		internalDataSource.saveData(ds);
	}

	/**
	 * @param dsf
	 * @see org.gdms.data.InternalDataSource#setDataSourceFactory(org.gdms.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		internalDataSource.setDataSourceFactory(dsf);
	}

	/**
	 * @param dispatchingMode
	 * @see org.gdms.data.InternalDataSource#setDispatchingMode(int)
	 */
	public void setDispatchingMode(int dispatchingMode) {
		internalDataSource.setDispatchingMode(dispatchingMode);
	}

	/**
	 * @param index
	 * @param name
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#setFieldName(int, java.lang.String)
	 */
	public void setFieldName(int index, String name) throws DriverException {
		internalDataSource.setFieldName(index, name);
	}

	/**
	 * @param row
	 * @param fieldId
	 * @param value
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#setFieldValue(long, int, org.gdms.data.values.Value)
	 */
	public void setFieldValue(long row, int fieldId, Value value) throws DriverException {
		internalDataSource.setFieldValue(row, fieldId, value);
	}

	/**
	 * 
	 * @see org.gdms.data.InternalDataSource#startUndoRedoAction()
	 */
	public void startUndoRedoAction() {
		internalDataSource.startUndoRedoAction();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.InternalDataSource#undo()
	 */
	public void undo() throws DriverException {
		internalDataSource.undo();
	}
}
