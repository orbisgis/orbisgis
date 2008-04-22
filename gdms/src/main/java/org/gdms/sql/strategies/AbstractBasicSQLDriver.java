package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.TypeDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.source.SourceManager;

public abstract class AbstractBasicSQLDriver implements ObjectDriver {

	public void start() throws DriverException {
	}

	public void stop() throws DriverException {
	}

	public int getType() {
		return SourceManager.SQL;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		return new TypeDefinition[0];
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		return "sql driver";
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

}
