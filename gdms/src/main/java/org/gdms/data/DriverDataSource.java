package org.gdms.data;

import java.util.Iterator;

import org.gdms.data.indexes.DataSourceIndex;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.IndexResolver;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadWriteDriver;
import org.gdms.sql.strategies.Row;

/**
 * Base class for all the DataSources that directly access a driver. getDriver()
 * returns a not null instance
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public abstract class DriverDataSource extends DataSourceCommonImpl {

	private IndexResolver indexResolver;

	public DriverDataSource(String name, String alias) {
		super(name, alias);
	}

	public Number[] getScope(int dimension) throws DriverException {
		return getDriver().getScope(dimension);
	}

	public boolean isEditable() {
		final ReadOnlyDriver driver = getDriver();

		// return ((driver instanceof ReadWriteDriver) && ((ReadWriteDriver)
		// driver)
		// .isEditable());
		// TODO I think we cannot rely in the order the compiler solve the
		// expressions
		if (driver instanceof ReadWriteDriver) {
			return ((ReadWriteDriver) driver).isCommitable();
		} else {
			return false;
		}

	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return getDriver().getFieldValue(rowIndex, fieldId);
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return getDriver().getRowCount();
	}

	/**
	 * @see org.gdms.data.DataSource#getMetadata()
	 */
	public Metadata getMetadata() throws DriverException {
		return getDriver().getMetadata();
	}

	public void open() throws DriverException {
		indexResolver.openIndexes();
	}

	public Iterator<Row> queryIndex(IndexQuery queryIndex)
			throws DriverException {
		String indexId = queryIndex.getIndexId();

		for (DataSourceIndex idx : indexResolver.getDataSourceIndexes()) {
			if ((idx.getId().equals(indexId))
					&& (idx.getFieldName().equals(queryIndex.getFieldName()))) {
				return idx.getIterator(queryIndex);
			}
		}
		return null;
	}

	public void setIndexResolver(IndexResolver indexResolver) {
		this.indexResolver = indexResolver;
	}
}
