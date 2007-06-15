package org.gdms.driver;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.TypeDefinition;

import com.hardcode.driverManager.Driver;

public interface ReadOnlyDriver extends Driver, ReadAccess {

	public void setDataSourceFactory(DataSourceFactory dsf);

	/**
	 * Gets the driver specific metadata
	 *
	 * @return
	 * @throws DriverException
	 */
	public Metadata getMetadata() throws DriverException;

	/**
	 * @return
	 * @throws DriverException
	 */
	public TypeDefinition[] getTypesDefinitions() throws DriverException;
}