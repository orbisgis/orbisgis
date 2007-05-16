package org.gdms.sql.strategies;

import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.GDBMSDriver;



/**
 * operation layer DataSource base class
 *
 * @author Fernando Gonzalez Cortes
 */
public abstract class OperationDataSource extends DataSourceCommonImpl implements DataSource {
	private DataSourceFactory dsf;
	private String sql;

    public OperationDataSource() {
        super(null, null);
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
	 * @param sql query
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
        throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }

    public void insertFilledRow(Value[] values) throws DriverException {
        throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }

    public void insertEmptyRow() throws DriverException {
        throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }

    public void commitTrans() throws DriverException {
    }

    public void setFieldValue(long row, int fieldId, Value value) throws DriverException {
        throw new UnsupportedOperationException("OperationDataSources do not have primary keys");
    }

    public void insertEmptyRowAt(long index) throws DriverException {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void insertFilledRowAt(long index, Value[] values) throws DriverException {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void saveData(DataSource ds) throws DriverException {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    /**
     * @throws DriverException
     * @see org.gdms.data.DataSource#getFieldIndexByName(java.lang.String)
     */
    public int getFieldIndexByName(String name) throws DriverException {
        String[] fieldNames = getFieldNames();
        for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(name)){
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
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void undo() throws DriverException {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void addEditionListener(EditionListener listener) {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void removeEditionListener(EditionListener listener) {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void addMetadataEditionListener(MetadataEditionListener listener) {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void removeMetadataEditionListener(MetadataEditionListener listener) {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void setDispatchingMode(int dispatchingMode) {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public int getDispatchingMode() {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void addField(String name, String type){
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void addField(String name, String type, String[] paramNames, String[] paramValues){
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void removeField(int index){
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public void setFieldName(int index, String name){
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public DriverMetadata getDriverMetadata() {
        throw new UnsupportedOperationException();
    }

    public String check(int fieldId, Value value) throws DriverException {
        throw new UnsupportedOperationException("OperationDataSources are not editable");
    }

    public GDBMSDriver getDriver() {
        return null;
    }

    public boolean isModified() {
        return false;
    }
}
