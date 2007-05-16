package org.gdms.data.db;

import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.DBDriver;
import org.gdms.driver.GDBMSDriver;

import com.hardcode.driverManager.Driver;

/**
 * @author Fernando Gonzalez Cortes
 */
public class DBTableSourceDefinition extends AbstractDataSourceDefinition {
	protected DBSource def;

	/**
	 * Creates a new DBTableSourceDefinition
	 *
	 * @param driverName
	 *            Name of the driver used to access the data
	 */
	public DBTableSourceDefinition(DBSource def) {
		this.def = def;
	}

	public DataSource createDataSource(String tableName, String tableAlias,
			String driverName) throws DataSourceCreationException {

		Driver d = getDataSourceFactory().getDriverManager().getDriver(
				driverName);
		((GDBMSDriver) d).setDataSourceFactory(getDataSourceFactory());

		DBTableDataSourceAdapter adapter = new DBTableDataSourceAdapter(
				tableName, tableAlias, def, (DBDriver) d);
		adapter.setDataSourceFactory(getDataSourceFactory());

		getDataSourceFactory().getDelegatingStrategy().registerView(tableName,
				def.getTableName());

		return adapter;
	}

	public DBSource getSourceDefinition() {
		return def;
	}

	public String getPrefix() {
		return def.getPrefix();
	}
}
