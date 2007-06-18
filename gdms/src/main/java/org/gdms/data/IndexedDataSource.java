package org.gdms.data;

import org.gdms.data.indexes.DataSourceIndex;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.hardcode.driverManager.DriverLoadException;

public interface IndexedDataSource {

	/**
	 * Gets the array of indexes used in this DataSource
	 *
	 * @return
	 * @throws DriverException
	 */
	public DataSourceIndex[] getDataSourceIndexes() throws DriverException;

	/**
	 * Notifies that the DataSource has commited changes and the indexes should
	 * be rebuilt
	 *
	 * @throws IncompatibleTypesException
	 * @throws DriverLoadException
	 * @throws DriverException
	 * @throws NoSuchTableException
	 * @throws DataSourceCreationException
	 */
	public void commitIndexChanges() throws IncompatibleTypesException,
			DriverLoadException, DriverException, NoSuchTableException,
			DataSourceCreationException;
}
