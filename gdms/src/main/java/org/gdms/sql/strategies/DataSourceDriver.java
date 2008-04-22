package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public class DataSourceDriver extends AbstractMetadataSQLDriver implements ObjectDriver {

	private DataSource dataSource;

	public DataSourceDriver(DataSource dataSource) throws DriverException {
		super(dataSource.getMetadata());
		this.dataSource = dataSource;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return dataSource.getFieldValue(rowIndex, fieldId);
	}

	public long getRowCount() throws DriverException {
		return dataSource.getRowCount();
	}

	public DataSource getDataSource() {
		return dataSource;
	}
}
