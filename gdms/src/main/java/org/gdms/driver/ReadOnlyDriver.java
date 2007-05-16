package org.gdms.driver;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.values.Value;
import org.gdms.spatial.FID;

import com.hardcode.driverManager.Driver;

public interface ReadOnlyDriver extends Driver, ReadAccess {

	public void setDataSourceFactory(DataSourceFactory dsf);

	/**
	 * Gets the suitable GDMS type for the given driver specific type
	 * 
	 * @param driverType
	 * @return
	 */
	int getType(String driverType);

	/**
	 * Gets the driver specific metadata
	 * 
	 * @return
	 * @throws DriverException
	 */
	public DriverMetadata getDriverMetadata() throws DriverException;

	/**
	 * Checks if a given value is suitable for the specified field
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	String check(Field field, Value value) throws DriverException;

	/**
	 * Returns true if the specified field is read only
	 * 
	 * @param i
	 * @return
	 * @throws DriverException
	 */
	public boolean isReadOnly(int i) throws DriverException;

	/**
	 * Gets a string identificator for each type a field can have
	 * 
	 * @return
	 * @throws DriverException
	 */
	String[] getAvailableTypes() throws DriverException;

	/**
	 * Gets the parameters used in creating the type
	 * 
	 * @param driverType
	 * @return
	 * @throws DriverException
	 */
	String[] getParameters(String driverType) throws DriverException;

	/**
	 * Returns if the given value (paramValue) for the parameter called
	 * paramName of the given driver specific type is valid or not
	 * 
	 * @param driverType
	 * @param paramName
	 * @param paramValue
	 *            null if the parameter is not specified
	 * 
	 * @return
	 */
	boolean isValidParameter(String driverType, String paramName,
			String paramValue);

	/**
	 * Returns
	 * 
	 * @param row
	 *            the row number
	 * @return
	 */
	FID getFid(final long row);

	/**
	 * Returns true iff the FID support is allready defined in the DataSource
	 * 
	 * @return
	 */
	boolean hasFid();
}