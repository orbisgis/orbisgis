package org.gdms.data.db;

import org.gdms.data.AbstractDataSourceCreation;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;

import com.hardcode.driverManager.Driver;

public class DBSourceCreation extends AbstractDataSourceCreation implements
		DataSourceCreation {

	private DBSource source;

	private Metadata metadata;

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
	public DBSourceCreation(DBSource source, Metadata dmd) {
		this.source = source;
		this.metadata = dmd;
	}

	public void create() throws DriverException {
		Driver d = getDataSourceFactory().getDriverManager().getDriver(
				getDataSourceFactory().getDriverName(source.getPrefix()));

		((DBReadWriteDriver) d).createSource(source, metadata);
	}
}