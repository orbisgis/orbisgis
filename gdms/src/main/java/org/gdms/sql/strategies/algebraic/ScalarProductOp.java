package org.gdms.sql.strategies.algebraic;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.strategies.PDataSourceDecorator;

public class ScalarProductOp extends DefaultOperator implements Operator {

	private ArrayList<DataSource> tables = new ArrayList<DataSource>();

	private ArrayList<String> aliases = new ArrayList<String>();

	public void addTable(DataSourceFactory dsf, String tableRef, String alias)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		tables.add(dsf.getDataSource(tableRef));
		aliases.add(alias);
	}

	public DataSource getDataSource() {
		return new PDataSourceDecorator(tables.toArray(new DataSource[0]));
	}

}
