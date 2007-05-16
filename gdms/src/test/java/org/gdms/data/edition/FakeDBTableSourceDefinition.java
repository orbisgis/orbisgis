package org.gdms.data.edition;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableDataSourceAdapter;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.driver.DBDriver;
import org.gdms.driver.GDBMSDriver;

public class FakeDBTableSourceDefinition extends DBTableSourceDefinition {

	protected Object driver;
	private String prefix;

	public FakeDBTableSourceDefinition(Object driver, String prefix) {
		super(null);
		this.driver = driver;
		this.prefix = prefix;
	}

	@Override
	public DataSource createDataSource(String tableName, String tableAlias, String driverName) throws DataSourceCreationException {

		((GDBMSDriver) driver).setDataSourceFactory(getDataSourceFactory());

        DBSource dbs = new DBSource(null, 0, null, null, null, null, null);
		DBTableDataSourceAdapter adapter = new DBTableDataSourceAdapter(
				tableName, tableAlias, dbs, (DBDriver) driver);
		adapter.setDataSourceFactory(getDataSourceFactory());

		getDataSourceFactory().getDelegatingStrategy().registerView(tableName,
				dbs.getTableName());

		return adapter;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}



}
