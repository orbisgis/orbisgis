package org.gdms.data.db;

import org.gdms.data.AbstractDataSourceCreation;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.driver.DBDriver;
import org.gdms.data.driver.DriverException;
import org.gdms.data.metadata.DriverMetadata;

import com.hardcode.driverManager.Driver;

public class DBSourceCreation extends AbstractDataSourceCreation implements
		DataSourceCreation {

	private DBSource source;

	private DriverMetadata driverMetadata;

	/**
	 * Builds a new DBSourceCreation
	 *
	 * @param driverName
	 *            Name of the driver to be used to create the source
	 * @param source
	 *            information about the table to be created
	 * @param dmd
	 *            Information about the schema of the new source. If the driver
	 *            is a spatial one, this parameter must be a
	 *            SpatialDriverMetadata implementation
	 */
	public DBSourceCreation(DBSource source, DriverMetadata dmd) {
		this.source = source;
		this.driverMetadata = dmd;
	}

	public void create() throws DriverException {
		Driver d = getDataSourceFactory().getDriverManager().getDriver(
				getDataSourceFactory().getDriverName(source.getPrefix()));

		((DBDriver) d).createSource(source, driverMetadata);
	}
}
