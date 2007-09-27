package org.gdms.driver.dbf;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public interface RowProvider {

	public Value[] getRow(long index) throws DriverException;

	public Metadata getMetadata() throws DriverException;

	public long getRowCount() throws DriverException;
}
