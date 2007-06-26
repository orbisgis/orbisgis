package org.gdms.data.edition;

import org.gdms.data.AbstractDataSource;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableDataSourceAdapter;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.driver.DBDriver;
import org.gdms.driver.ReadOnlyDriver;

public class FakeDBTableSourceDefinition extends DBTableSourceDefinition {

	protected Object driver;

	private String prefix;

	public FakeDBTableSourceDefinition(Object driver, String prefix) {
		super(null);
		this.driver = driver;
		this.prefix = prefix;
	}

	@Override
	public DataSource createDataSource(String tableName, String driverName)
			throws DataSourceCreationException {

		((ReadOnlyDriver) driver).setDataSourceFactory(getDataSourceFactory());

		DBSource dbs = new DBSource(null, 0, null, null, null, null, null);
		AbstractDataSource adapter = new DBTableDataSourceAdapter(tableName,
				dbs, (DBDriver) driver);
		adapter.setDataSourceFactory(getDataSourceFactory());

		return adapter;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

}
