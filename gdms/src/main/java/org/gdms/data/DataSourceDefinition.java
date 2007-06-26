package org.gdms.data;

import org.gdms.driver.DriverException;


/**
 *
 */
public interface DataSourceDefinition {
	/**
	 * Creates a DataSource with the information of this object
	 *
	 * @param tableName
	 *            name of the DataSource
	 * @param tableAlias
	 *            alias of the DataSource
	 * @param indexResolver TODO
	 * @return DataSource
	 */
	public DataSource createDataSource(String tableName,
			String driverName) throws DataSourceCreationException;

	/**
	 * Creates this source with the content specified in the parameter
	 *
	 * @param contents
	 */
	public void createDataSource(String driverName, DataSource contents) throws DriverException;

	/**
	 * if any, frees the resources taken when the DataSource was created
	 *
	 * @param name
	 *            DataSource registration name
	 *
	 * @throws DataSourceFinalizationException
	 *             If the operation fails
	 */
	public void freeResources(String name)
			throws DataSourceFinalizationException;

	/**
	 * Gives to the DataSourceDefinition a reference of the DataSourceFactory
	 * where the DataSourceDefinition is registered
	 *
	 * @param dsf
	 */
	public void setDataSourceFactory(DataSourceFactory dsf);

}
