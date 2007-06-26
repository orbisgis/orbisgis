package org.gdms.data.edition;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.file.FileDataSourceAdapter;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.FileDriver;
import org.gdms.driver.ReadOnlyDriver;

public class FakeFileSourceDefinition extends FileSourceDefinition {

	protected Object driver;

	public FakeFileSourceDefinition(Object driver) {
		super("");
		this.driver = driver;
	}

	@Override
	public DataSource createDataSource(String tableName, String driverName)
			throws DataSourceCreationException {
		((ReadOnlyDriver) driver).setDataSourceFactory(getDataSourceFactory());

		FileDataSourceAdapter ds = new FileDataSourceAdapter(tableName, file,
				(FileDriver) driver);
		return ds;
	}

}
