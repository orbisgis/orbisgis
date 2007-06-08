package org.gdms.data.file;

import java.io.File;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.ReadOnlyDriver;

import com.hardcode.driverManager.Driver;

/**
 * Informacion del driver de fichero
 * 
 * @author Fernando Gonzalez Cortes
 */
public class FileSourceDefinition extends AbstractDataSourceDefinition {
	public File file;

	public FileSourceDefinition(File file) {
		this.file = file;
	}

	public FileSourceDefinition(String fileName) {
		this.file = new File(fileName);
	}

	public DataSource createDataSource(String tableName, String tableAlias,
			String driverName) throws DataSourceCreationException {
		if (!file.exists()) {
			throw new DataSourceCreationException(file + " does not exists");
		}
		Driver d = getDataSourceFactory().getDriverManager().getDriver(
				driverName);
		((ReadOnlyDriver) d).setDataSourceFactory(getDataSourceFactory());

		FileDataSourceAdapter ds = new FileDataSourceAdapter(tableName,
				tableAlias, file, (FileDriver) d);
		return ds;
	}

	public File getFile() {
		return file;
	}
}
