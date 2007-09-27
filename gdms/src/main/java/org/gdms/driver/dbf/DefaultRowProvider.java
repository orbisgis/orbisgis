package org.gdms.driver.dbf;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class DefaultRowProvider implements RowProvider {

	private DataSource dataSource;

	public DefaultRowProvider(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Metadata getMetadata() throws DriverException {
		return dataSource.getMetadata();
	}

	public Value[] getRow(long index) throws DriverException {
		return dataSource.getRow(index);
	}

	public long getRowCount() throws DriverException {
		return dataSource.getRowCount();
	}
}