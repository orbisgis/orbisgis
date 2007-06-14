package org.gdms.data;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadWriteDriver;

/**
 * Base class for all the DataSources that directly access a driver. getDriver()
 * returns a not null instance
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public abstract class DriverDataSource extends DataSourceCommonImpl {

	public DriverDataSource(String name, String alias) {
		super(name, alias);
	}

	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
		return getDriver().getScope(dimension, fieldName);
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

}
