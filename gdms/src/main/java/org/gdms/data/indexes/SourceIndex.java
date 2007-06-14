package org.gdms.data.indexes;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.hardcode.driverManager.DriverLoadException;

/**
 *
 */
public interface SourceIndex {

	/**
	 * Returns a DataSource specific index with the same indexing information
	 * than this index
	 * @param ds
	 *
	 * @return
	 * @throws DriverException
	 */
	public DataSourceIndex getDataSourceIndex(DataSource ds) throws DriverException;

	/**
	 * Indexes the specified field of the specified source
	 *
	 * @param ds
	 * @param fieldName
	 * @throws DriverException
	 * @throws IncompatibleTypesException
	 *             If the index cannot be built with that field type
	 * @throws DataSourceCreationException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 */
	public void buildIndex(DataSourceFactory dsf, String name, String fieldName)
			throws DriverException, IncompatibleTypesException, DriverLoadException, NoSuchTableException, DataSourceCreationException;

	/**
	 * Returns the identification of the index
	 *
	 * @return
	 */
	public String getId();

	/**
	 * @return A new empty index instance ready to be used
	 */
	public SourceIndex getNewIndex();

}
