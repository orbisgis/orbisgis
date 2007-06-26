package org.gdms.data.object;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.ObjectReadWriteDriver;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ObjectSourceDefinition extends AbstractDataSourceDefinition {

	public ObjectDriver driver;

	public ObjectSourceDefinition(ObjectDriver driver) {
		this.driver = driver;
	}

	public DataSource createDataSource(String tableName,
			String driverName) throws DataSourceCreationException {
		ObjectDataSourceAdapter ds;
		ds = new ObjectDataSourceAdapter(tableName, driver);
		return ds;
	}

	public void createDataSource(String driverName, DataSource contents)
			throws DriverException {
		((ObjectReadWriteDriver) driver).write(contents);
	}
}
