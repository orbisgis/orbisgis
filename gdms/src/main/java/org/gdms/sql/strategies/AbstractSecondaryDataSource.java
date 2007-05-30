package org.gdms.sql.strategies;

import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.MetadataEditionSupport;
import org.gdms.data.edition.RowOrientedEditionDataSourceImpl;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;

import com.hardcode.driverManager.DriverLoadException;

/**
 * operation layer DataSource base class
 *
 * @author Fernando Gonzalez Cortes
 */
public abstract class AbstractSecondaryDataSource extends DataSourceCommonImpl {
	private DataSourceFactory dsf;

	private String sql;

	private RowOrientedEditionDataSourceImpl rowOrientedEdition;

	private MetadataEditionSupport metadataEdition;

	public AbstractSecondaryDataSource() {
		super(null, null);

		metadataEdition = new MetadataEditionSupport(this);
		rowOrientedEdition = new RowOrientedEditionDataSourceImpl(this,
				metadataEdition);
	}

	/**
	 * @see org.gdms.data.DataSource#getWhereFilter()
	 */
	public long[] getWhereFilter() throws IOException {
		return null;
	}

	/**
	 * @see org.gdms.data.DataSource#getDataSourceFactory()
	 */
	public DataSourceFactory getDataSourceFactory() {
		return dsf;
	}

	/**
	 * @see org.gdms.data.DataSource#setDataSourceFactory(org.gdms.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
		setName(dsf.getUID());
		setAlias(dsf.getUID());
	}

	/**
	 * sets the sql query of this operation DataSource. It's needed by the
	 * getMemento method which contains basically the sql
	 *
	 * @param sql
	 *            query
	 */
	public void setSQL(String sql) {
		this.sql = sql;
	}

	/**
	 * Gets the SQL string that created this DataSource
	 *
	 * @return String with the query
	 */
	public String getSQL() {
		return sql;
	}

	/**
	 * @see org.gdms.data.DataSource#remove()
	 */
	public void remove() throws DriverException {
		dsf.remove(this);
	}

	public void deleteRow(long rowId) throws DriverException {
		rowOrientedEdition.deleteRow(rowId);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		rowOrientedEdition.insertFilledRow(values);
	}

	public void insertEmptyRow() throws DriverException {
		rowOrientedEdition.insertEmptyRow();
	}

	public void commit() throws DriverException {
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		rowOrientedEdition.setFieldValue(row, fieldId, value);
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		rowOrientedEdition.insertEmptyRowAt(index);
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		rowOrientedEdition.insertFilledRowAt(index, values);
	}

	public void saveData(DataSource ds) throws DriverException {
		throw new UnsupportedOperationException(
				"OperationDataSources are not editable");
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.DataSource#getFieldIndexByName(java.lang.String)
	 */
	public int getFieldIndexByName(String name) throws DriverException {
		String[] fieldNames = getFieldNames();
		for (int i = 0; i < fieldNames.length; i++) {
			if (fieldNames[i].equals(name)) {
				return i;
			}
		}

		return -1;
	}

	public boolean canRedo() {
		return false;
	}

	public boolean canUndo() {
		return false;
	}

	public void redo() throws DriverException {
		throw new UnsupportedOperationException(
				"OperationDataSources are not editable");
	}

	public void undo() throws DriverException {
		throw new UnsupportedOperationException(
				"OperationDataSources are not editable");
	}

	public void addEditionListener(EditionListener listener) {
		rowOrientedEdition.addEditionListener(listener);
	}

	public void removeEditionListener(EditionListener listener) {
		rowOrientedEdition.removeEditionListener(listener);
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		metadataEdition.addMetadataEditionListener(listener);
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		metadataEdition.removeMetadataEditionListener(listener);
	}

	public void setDispatchingMode(int dispatchingMode) {
		rowOrientedEdition.setDispatchingMode(dispatchingMode);
	}

	public int getDispatchingMode() {
		return rowOrientedEdition.getDispatchingMode();
	}

	public void addField(String name, String type) throws DriverException {
		addField(name, type, new String[0], new String[0]);
	}

	public void addField(String name, String type, String[] paramNames,
			String[] paramValues) throws DriverException {
		metadataEdition.addField(name, type, paramNames, paramValues);
		rowOrientedEdition.addField();
	}

	public void removeField(int index) throws DriverException {
		metadataEdition.removeField(index);
		rowOrientedEdition.removeField(index);
	}

	public void setFieldName(int index, String name) throws DriverException {
		metadataEdition.setFieldName(index, name);
		rowOrientedEdition.setFieldName();
	}

	public DriverMetadata getDriverMetadata() {
		return metadataEdition.getDriverMetadata();
	}

	public String check(int fieldId, Value value) throws DriverException {
		if (getDataSourceMetadata().getFieldType(fieldId) == value.getType()) {
			return null;
		} else {
			return "Types does not match";
		}
	}

	public ReadOnlyDriver getDriver() {
		return null;
	}

	public boolean isModified() {
		return rowOrientedEdition.isModified();
	}

	public final boolean isEditable() {
		return false;
	}

	public abstract DataSource cloneDataSource();

	// begin :: Following methods are implementations of EditableDataSource

	public final Metadata getDataSourceMetadata() throws DriverException {
		return metadataEdition.getDataSourceMetadata();
	}

	public void open() throws DriverException {
		rowOrientedEdition.beginTrans();
	}

	public void cancel() throws DriverException {
		rowOrientedEdition.rollBackTrans();
	}

	public void endUndoRedoAction() {
	}

	public String getFieldName(int fieldId) throws DriverException {
		return getDataSourceMetadata().getFieldName(fieldId);
	}

	public int getFieldType(int i) throws DriverException {
		return getDataSourceMetadata().getFieldType(i);
	}

	public DriverMetadata getOriginalDriverMetadata() throws DriverException {
		return null;
	}

	public int getOriginalFieldCount() throws DriverException {
		return metadataEdition.getOriginalFieldCount();
	}

	public int getType(String driverType) {
		throw new UnsupportedOperationException("???");
	}

	public void startUndoRedoAction() {
	}

	public int getFieldCount() throws DriverException {
		return metadataEdition.getFieldCount();
	}

	public final Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return rowOrientedEdition.getFieldValue(rowIndex, fieldId);
	}

	public final long getRowCount() throws DriverException {
		return rowOrientedEdition.getRowCount();
	}

	/**
	 * If the DataSource belongs to the SQL part of the stack this method uses
	 * the cloneDataSource method (specific for this stack). If the DataSource
	 * is not of the SQL part of the stack we ask the DataSourceFactory for the
	 * DataSource instance
	 *
	 * @param source
	 * @return
	 */
	protected DataSource clone(DataSource source) {
		if (source instanceof AbstractSecondaryDataSource) {
			return ((AbstractSecondaryDataSource) source).cloneDataSource();
		} else {
			try {
				return getDataSourceFactory()
				.
				getDataSource(source
						.
						getName(),
						source
						.
						getAlias());
			} catch (DriverLoadException e) {
				throw new RuntimeException(e);
			} catch (NoSuchTableException e) {
				throw new RuntimeException(e);
			} catch (DataSourceCreationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	// end :: Following methods are implementations of EditableDataSource
}