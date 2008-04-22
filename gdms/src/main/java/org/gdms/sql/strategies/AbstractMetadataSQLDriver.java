package org.gdms.sql.strategies;

import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public abstract class AbstractMetadataSQLDriver extends AbstractBasicSQLDriver
		implements ObjectDriver {

	private Metadata metadata;

	public AbstractMetadataSQLDriver(Metadata metadata) {
		this.metadata = metadata;
	}

	public Metadata getMetadata() throws DriverException {
		return metadata;
	}

}
