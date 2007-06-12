package org.gdms.sql.strategies;

import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;

/**
 * operation layer DataSource base class
 *
 * @author Fernando Gonzalez Cortes
 */
public abstract class AbstractSecondaryDataSource extends DataSourceCommonImpl {
	private DataSourceFactory dsf;

	private String sql;

	public AbstractSecondaryDataSource() {
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
		setAlias(null);
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

	public void commit() throws DriverException {
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

	public String check(int fieldId, Value value) throws DriverException {
		if (getMetadata().getFieldType(fieldId).getTypeCode() == value
				.getType()) {
			return null;
		} else {
			return "Types does not match";
		}
	}

	public ReadOnlyDriver getDriver() {
		return null;
	}

	// begin :: Following methods are implementations of EditableDataSource

	public String getFieldName(int fieldId) throws DriverException {
		return getMetadata().getFieldName(fieldId);
	}

	public Type getFieldType(int i) throws DriverException {
		return getMetadata().getFieldType(i);
	}

	// end :: Following methods are implementations of EditableDataSource
}