package org.gdms.data;

import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.persistence.DataSourceLayerMemento;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Base class with the common implementation for all DataSource implementations
 * and methods that invoke other DataSource methods
 *
 * @author Fernando Gonzalez Cortes
 */
public abstract class DataSourceCommonImpl extends AbstractDataSource {

	private String name;

	private String alias;

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

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOpen() {
		return false;
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