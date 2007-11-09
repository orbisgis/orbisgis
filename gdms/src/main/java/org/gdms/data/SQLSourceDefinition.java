package org.gdms.data;

import java.util.ArrayList;

import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.directory.DefinitionType;
import org.gdms.source.directory.SqlDefinitionType;

public class SQLSourceDefinition extends AbstractDataSourceDefinition implements
		DataSourceDefinition {

	private String sql;

	public SQLSourceDefinition(String sql) {
		this.sql = sql;
	}

	public DataSource createDataSource(String tableName)
			throws DataSourceCreationException {
		try {
			return getDataSourceFactory().executeSQL(tableName, sql);
		} catch (SyntaxException e) {
			throw new DataSourceCreationException(e);
		} catch (DriverLoadException e) {
			throw new DataSourceCreationException(e);
		} catch (NoSuchTableException e) {
			throw new DataSourceCreationException(e);
		} catch (ExecutionException e) {
			throw new DataSourceCreationException(e);
		}
	}

	public void createDataSource(DataSource contents) throws DriverException {
		throw new DriverException("Read only source");
	}

	public DefinitionType getDefinition() {
		SqlDefinitionType ret = new SqlDefinitionType();
		ret.setSql(sql);

		return ret;
	}

	public static DataSourceDefinition createFromXML(
			SqlDefinitionType definitionType) {
		return new SQLSourceDefinition(definitionType.getSql());
	}

	@Override
	protected ReadOnlyDriver getDriverInstance() {
		return null;
	}

	@Override
	public ArrayList<String> getSourceDependencies() {
		ArrayList<String> ret = new ArrayList<String>();
		String[] sources = getDataSourceFactory().getSources(sql);
		for (String source : sources) {
			ret.add(source);
		}

		return ret;
	}

	public ReadOnlyDriver getDriver() {
		return null;
	}
}
