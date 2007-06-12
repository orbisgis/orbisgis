package org.gdms.driver;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.TypeDefinition;
import org.gdms.spatial.FID;

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
	 * Returns
	 *
	 * @param row
	 *            the row number
	 * @return
	 */
	public FID getFid(final long row);

	/**
	 * Returns true if the FID support is already defined in the DataSource
	 *
	 * @return
	 */
	public boolean hasFid();

	/**
	 * @return
	 * @throws DriverException
	 */
	public TypeDefinition[] getTypesDefinitions() throws DriverException;
}