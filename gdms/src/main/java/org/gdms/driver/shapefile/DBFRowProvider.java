package org.gdms.driver.shapefile;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.dbf.RowProvider;

public class DBFRowProvider implements RowProvider {

	private DataSource dataSource;

	private DBFMetadata metadata;

	public DBFRowProvider(DataSource ds) throws DriverException {
		this.dataSource = ds;
		this.metadata = new DBFMetadata(dataSource.getMetadata());
	}

	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

	public Value[] getRow(long index) throws DriverException {
		Value[] ret = new Value[getMetadata().getFieldCount()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = dataSource.getFieldValue(index, metadata.getMapping().get(i));
		}
		return ret;
	}

	public long getRowCount() throws DriverException {
		return dataSource.getRowCount();
	}

}
