package org.gdms.data.file;

import java.io.File;

import org.gdms.data.AbstractDataSourceCreation;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;

import com.hardcode.driverManager.Driver;

public class FileSourceCreation extends AbstractDataSourceCreation {

	private File file;

	private Metadata driverMetadata;

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
	public FileSourceCreation(File file, Metadata dmd) {
		this.file = file;
		this.driverMetadata = dmd;
	}

	public DataSourceDefinition create() throws DriverException {
		Driver d = getDataSourceFactory().getDriverManager().getDriver(
				getDataSourceFactory().getDriverName(file));

		if (!file.exists()) {
			((FileReadWriteDriver) d).createSource(file.getAbsolutePath(),
					driverMetadata);
		}

		return new FileSourceDefinition(file);
	}

	public DataSourceDefinition create(DataSource contents) throws DriverException {
		Driver d = getDataSourceFactory().getDriverManager().getDriver(
				getDataSourceFactory().getDriverName(file));

		if (!file.exists()) {
			((FileReadWriteDriver) d).writeFile(file, contents);
		} else {
			throw new DriverException(file + " already exists");
		}

		return new FileSourceDefinition(file);
	}
}