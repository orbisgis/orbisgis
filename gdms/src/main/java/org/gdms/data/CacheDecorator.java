package org.gdms.data;

import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;

public class CacheDecorator extends AbstractDataSourceDecorator {

	private Metadata metadata;

	private long rc;

	private Number[] scope;

	public CacheDecorator(DataSource internalDataSource) {
		super(internalDataSource);
	}

	@Override
	public void open() throws DriverException {
		rc = -1;
		metadata = null;
		scope = null;
		getDataSource().open();
	}

	public Metadata getMetadata() throws DriverException {
		if (metadata == null) {
			metadata = getDataSource().getMetadata();
		}

		return metadata;
	}

	public long getRowCount() throws DriverException {
		if (rc == -1) {
			rc = getDataSource().getRowCount();
		}

		return rc;
	}

	public Number[] getScope(int dimension)
			throws DriverException {
		if (scope == null) {
			scope = getDataSource().getScope(dimension);
		}

		return scope;
	}
}
