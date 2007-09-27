package org.gdms.data.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.AbstractDataSource;
import org.gdms.data.AbstractDataSourceDefinition;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.InnerDBUtils;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;

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

	public DataSource createDataSource(String tableName, String driverName)
			throws DataSourceCreationException {

		Driver d = getDataSourceFactory().getDriverManager().getDriver(
				driverName);
		((ReadOnlyDriver) d).setDataSourceFactory(getDataSourceFactory());

		AbstractDataSource adapter = new DBTableDataSourceAdapter(tableName,
				def, (DBDriver) d);
		adapter.setDataSourceFactory(getDataSourceFactory());

		return adapter;
	}

	public DBSource getSourceDefinition() {
		return def;
	}

	public String getPrefix() {
		return def.getPrefix();
	}

	public void createDataSource(String driverName, DataSource contents)
			throws DriverException {
		contents.open();
		DBReadWriteDriver driver = (DBReadWriteDriver) getDataSourceFactory()
				.getDriverManager().getDriver(driverName);
		((ReadOnlyDriver) driver).setDataSourceFactory(getDataSourceFactory());
		Connection con;
		try {
			con = driver.getConnection(def.getHost(), def.getPort(), def
					.getDbName(), def.getUser(), def.getPassword());
		} catch (SQLException e) {
			throw new DriverException(e);
		}
		if (driver instanceof DBReadWriteDriver) {
			try {
				((DBReadWriteDriver) driver).beginTrans(con);
			} catch (SQLException e) {
				throw new DriverException(e);
			}
		}
		contents = getDataSourceWithPK(contents);
		driver.createSource(def, contents.getMetadata());

		for (int i = 0; i < contents.getRowCount(); i++) {
			Value[] row = new Value[contents.getFieldNames().length];
			for (int j = 0; j < row.length; j++) {
				row[j] = contents.getFieldValue(i, j);
			}

			try {
				String sqlInsert = InnerDBUtils.createInsertStatement(driver
						.getReferenceInSQL(def.getTableName()), row,
						getFieldsInSQL(driver, contents.getFieldNames()),
						driver);
				((DBReadWriteDriver) driver).execute(con, sqlInsert);
			} catch (SQLException e) {

				if (driver instanceof DBReadWriteDriver) {
					try {
						((DBReadWriteDriver) driver).rollBackTrans(con);
					} catch (SQLException e1) {
						throw new DriverException(e1);
					}
				}

				throw new DriverException(e);
			}
		}

		if (driver instanceof DBReadWriteDriver) {
			try {
				((DBReadWriteDriver) driver).commitTrans(con);
			} catch (SQLException e) {
				throw new DriverException(e);
			}
		}

		contents.cancel();
	}

	private String[] getFieldsInSQL(DBReadWriteDriver driver,
			String[] fieldNames) {
		String[] ret = new String[fieldNames.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = driver.getReferenceInSQL(fieldNames[i]);
		}

		return ret;
	}

	private DataSource getDataSourceWithPK(DataSource ds)
			throws DriverException {
		Metadata metadata = ds.getMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			Type fieldType = metadata.getFieldType(i);
			if (fieldType.getConstraint(ConstraintNames.PK) != null) {
				return ds;
			}
		}

		return new PKDataSourceAdapter(ds);
	}
}
