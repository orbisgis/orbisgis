package org.gdms.data;

import java.util.Iterator;

import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadWriteDriver;
import org.gdms.sql.strategies.FullIterator;

/**
 * Base class for all the DataSources that directly access a driver. getDriver()
 * returns a not null instance
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public abstract class DriverDataSource extends DataSourceCommonImpl {

	public DriverDataSource(String name) {
		super(name);
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

	public Iterator<PhysicalDirection> queryIndex(IndexQuery queryIndex)
			throws DriverException {
		try {
			Iterator<PhysicalDirection> ret = getDataSourceFactory()
					.getIndexManager().queryIndex(getName(), queryIndex);

			if (ret != null) {
				return ret;
			} else {
				return new FullIterator(this);
			}
		} catch (IndexException e) {
			throw new DriverException(e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException(e);
		}
	}

	public Commiter getCommiter() {
		return (Commiter) this;
	}
}
