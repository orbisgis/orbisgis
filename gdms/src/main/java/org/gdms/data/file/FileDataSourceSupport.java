package org.gdms.data.file;

import java.io.File;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;

public class FileDataSourceSupport {
	private FileDriver driver;

	private File file;

	public FileDataSourceSupport(DataSource ds, File file, FileDriver driver) {
		this.file = file;
		this.driver = driver;
	}

	public FileDriver getDriver() {
		return driver;
	}

	public File getFile() {
		return file;
	}

	public Metadata getMetadata() throws DriverException {
		return driver.getMetadata();
	}
}