package org.gdms.data.file;

import java.io.File;

import org.gdms.data.AbstractDataSourceCreation;
import org.gdms.data.driver.DriverException;
import org.gdms.data.driver.FileDriver;
import org.gdms.data.metadata.DriverMetadata;

import com.hardcode.driverManager.Driver;

public class FileSourceCreation extends AbstractDataSourceCreation {

	private File file;

	private DriverMetadata driverMetadata;

	/**
	 * Builds a new FileSourceCreation
	 *
	 * @param driverName
	 *            Name of the driver to be used to create the source
	 * @param file
	 *            Name of the file to create
	 * @param dmd
	 *            Information about the schema of the new source. If the driver
	 *            is a spatial one, this parameter must be a
	 *            SpatialDriverMetadata implementation
	 */
	public FileSourceCreation(File file, DriverMetadata dmd) {
		this.file = file;
		this.driverMetadata = dmd;
	}

	public void create() throws DriverException {
		Driver d = getDataSourceFactory().getDriverManager().getDriver(
				getDataSourceFactory().getDriverName(file));

		if (!file.exists()) {
			((FileDriver) d).createSource(file.getAbsolutePath(),
					driverMetadata);
		}
	}
}