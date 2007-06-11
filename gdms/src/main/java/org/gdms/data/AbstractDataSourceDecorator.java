/**
 * 
 */
package org.gdms.data;

import java.io.IOException;

import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;

/**
 * @author leduc
 * 
 */
public class AbstractDataSourceDecorator extends AbstractDataSource {
	private DataSource internalDataSource;

	public AbstractDataSourceDecorator(final DataSource internalDataSource) {
		this.internalDataSource = internalDataSource;
	}

	/**
	 * @return the internalDataSource
	 */
	public DataSource getDataSource() {
		return internalDataSource;
	}

	/**
	 * @param listener
	 * @see org.gdms.data.DataSource#addEditionListener(org.gdms.data.edition.EditionListener)
	 */
	public void addEditionListener(EditionListener listener) {
		internalDataSource.addEditionListener(listener);
	}

	/**
	 * @param name
	 * @param driverType
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#addField(java.lang.String,
	 *      java.lang.String)
	 */
	public void addField(String name, Type driverType) throws DriverException {
		internalDataSource.addField(name, driverType);
	}

	/**
	 * @param listener
	 * @see org.gdms.data.DataSource#addMetadataEditionListener(org.gdms.data.edition.MetadataEditionListener)
	 */
	public void addMetadataEditionListener(MetadataEditionListener listener) {
		internalDataSource.addMetadataEditionListener(listener);
	}

	/**
	 * @throws DriverException
	 * @throws AlreadyClosedException
	 * @see org.gdms.data.DataSource#cancel()
	 */
	public void cancel() throws DriverException, AlreadyClosedException {
		internalDataSource.cancel();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#canRedo()
	 */
	public boolean canRedo() {
		return internalDataSource.canRedo();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#canUndo()
	 */
	public boolean canUndo() {
		return internalDataSource.canUndo();
	}

	/**
	 * @param fieldId
	 * @param value
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#check(int, org.gdms.data.values.Value)
	 */
	public String check(int fieldId, Value value) throws DriverException {
		return internalDataSource.check(fieldId, value);
	}

	/**
	 * @throws DriverException
	 * @throws FreeingResourcesException
	 * @throws NonEditableDataSourceException
	 * @see org.gdms.data.DataSource#commit()
	 */
	public void commit() throws DriverException, FreeingResourcesException,
			NonEditableDataSourceException {
		internalDataSource.commit();
	}

	/**
	 * @param rowId
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#deleteRow(long)
	 */
	public void deleteRow(long rowId) throws DriverException {
		internalDataSource.deleteRow(rowId);
	}

	/**
	 * 
	 * @see org.gdms.data.DataSource#endUndoRedoAction()
	 */
	public void endUndoRedoAction() {
		internalDataSource.endUndoRedoAction();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#getAlias()
	 */
	public String getAlias() {
		return internalDataSource.getAlias();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#getDataSourceFactory()
	 */
	public DataSourceFactory getDataSourceFactory() {
		return internalDataSource.getDataSourceFactory();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getDataSourceMetadata()
	 */
	public Metadata getDataSourceMetadata() throws DriverException {
		return internalDataSource.getDataSourceMetadata();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#getDispatchingMode()
	 */
	public int getDispatchingMode() {
		return internalDataSource.getDispatchingMode();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#getDriver()
	 */
	public ReadOnlyDriver getDriver() {
		return internalDataSource.getDriver();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getDriverMetadata()
	 */
	public Metadata getMetadata() throws DriverException {
		return internalDataSource.getOriginalMetadata();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getFieldCount()
	 */
	public int getFieldCount() throws DriverException {
		return internalDataSource.getFieldCount();
	}

	/**
	 * @param fieldName
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String fieldName) throws DriverException {
		return internalDataSource.getFieldIndexByName(fieldName);
	}

	/**
	 * @param fieldId
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws DriverException {
		return internalDataSource.getFieldName(fieldId);
	}

	/**
	 * @param i
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getFieldType(int)
	 */
	public Type getFieldType(int i) throws DriverException {
		return internalDataSource.getFieldType(i);
	}

	/**
	 * @param rowIndex
	 * @param fieldId
	 * @return
	 * @throws DriverException
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return internalDataSource.getFieldValue(rowIndex, fieldId);
	}

	/**
	 * @return
	 * @throws MementoException
	 * @see org.gdms.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return internalDataSource.getMemento();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#getName()
	 */
	public String getName() {
		return internalDataSource.getName();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getOriginalFieldCount()
	 */
	public int getOriginalFieldCount() throws DriverException {
		return internalDataSource.getOriginalFieldCount();
	}

	/**
	 * @param rowIndex
	 * @param fieldId
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getOriginalFieldValue(long, int)
	 */
	public Value getOriginalFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return internalDataSource.getOriginalFieldValue(rowIndex, fieldId);
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getOriginalMetadata()
	 */
	public Metadata getOriginalMetadata() throws DriverException {
		return internalDataSource.getOriginalMetadata();
	}

	/**
	 * @return
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getOriginalRowCount()
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
	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
		return internalDataSource.getScope(dimension, fieldName);
	}

	/**
	 * @return
	 * @throws IOException
	 * @see org.gdms.data.DataSource#getWhereFilter()
	 */
	public long[] getWhereFilter() throws IOException {
		return internalDataSource.getWhereFilter();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#insertEmptyRow()
	 */
	public void insertEmptyRow() throws DriverException {
		internalDataSource.insertEmptyRow();
	}

	/**
	 * @param index
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#insertEmptyRowAt(long)
	 */
	public void insertEmptyRowAt(long index) throws DriverException {
		internalDataSource.insertEmptyRowAt(index);
	}

	/**
	 * @param values
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#insertFilledRow(org.gdms.data.values.Value[])
	 */
	public void insertFilledRow(Value[] values) throws DriverException {
		internalDataSource.insertFilledRow(values);
	}

	/**
	 * @param index
	 * @param values
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#insertFilledRowAt(long,
	 *      org.gdms.data.values.Value[])
	 */
	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		internalDataSource.insertFilledRowAt(index, values);
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#isEditable()
	 */
	public boolean isEditable() {
		return internalDataSource.isEditable();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#isModified()
	 */
	public boolean isModified() {
		return internalDataSource.isModified();
	}

	/**
	 * @return
	 * @see org.gdms.data.DataSource#isOpen()
	 */
	public boolean isOpen() {
		return internalDataSource.isOpen();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#open()
	 */
	public void open() throws DriverException {
		internalDataSource.open();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#redo()
	 */
	public void redo() throws DriverException {
		internalDataSource.redo();
	}

	/**
	 * @param listener
	 * @see org.gdms.data.DataSource#removeEditionListener(org.gdms.data.edition.EditionListener)
	 */
	public void removeEditionListener(EditionListener listener) {
		internalDataSource.removeEditionListener(listener);
	}

	/**
	 * @param index
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#removeField(int)
	 */
	public void removeField(int index) throws DriverException {
		internalDataSource.removeField(index);
	}

	/**
	 * @param listener
	 * @see org.gdms.data.DataSource#removeMetadataEditionListener(org.gdms.data.edition.MetadataEditionListener)
	 */
	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		internalDataSource.removeMetadataEditionListener(listener);
	}

	/**
	 * @param ds
	 * @throws IllegalStateException
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#saveData(org.gdms.data.DataSource)
	 */
	public void saveData(DataSource ds) throws IllegalStateException,
			DriverException {
		internalDataSource.saveData(ds);
	}

	/**
	 * @param dsf
	 * @see org.gdms.data.DataSource#setDataSourceFactory(org.gdms.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		internalDataSource.setDataSourceFactory(dsf);
	}

	/**
	 * @param dispatchingMode
	 * @see org.gdms.data.DataSource#setDispatchingMode(int)
	 */
	public void setDispatchingMode(int dispatchingMode) {
		internalDataSource.setDispatchingMode(dispatchingMode);
	}

	/**
	 * @param index
	 * @param name
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#setFieldName(int, java.lang.String)
	 */
	public void setFieldName(int index, String name) throws DriverException {
		internalDataSource.setFieldName(index, name);
	}

	/**
	 * @param row
	 * @param fieldId
	 * @param value
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#setFieldValue(long, int,
	 *      org.gdms.data.values.Value)
	 */
	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		internalDataSource.setFieldValue(row, fieldId, value);
	}

	/**
	 * 
	 * @see org.gdms.data.DataSource#startUndoRedoAction()
	 */
	public void startUndoRedoAction() {
		internalDataSource.startUndoRedoAction();
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#undo()
	 */
	public void undo() throws DriverException {
		internalDataSource.undo();
	}
}
