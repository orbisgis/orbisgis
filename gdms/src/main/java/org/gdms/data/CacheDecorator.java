package org.gdms.data;

import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class CacheDecorator extends AbstractDataSourceDecorator {

	private Metadata metadata;

	private long rc;

	private Envelope extent;

	public CacheDecorator(DataSource internalDataSource) {
		super(internalDataSource);
	}

	@Override
	public void open() throws DriverException {
		rc = -1;
		metadata = null;
		extent = null;
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

	public Number[] getScope(int dimension) throws DriverException {
		if (extent == null) {
			Number[] x = getDataSource().getScope(X);
			Number[] y = getDataSource().getScope(Y);
			if ((x != null) && (y != null)) {
				extent = new Envelope(new Coordinate(x[0].doubleValue(), y[0]
						.doubleValue()), new Coordinate(x[1].doubleValue(),
						y[1].doubleValue()));
			} else {
				return null;
			}
		}

		if (dimension == X) {
			return new Number[] { extent.getMinX(), extent.getMaxX() };
		} else if (dimension == Y) {
			return new Number[] { extent.getMinY(), extent.getMaxY() };
		} else {
			throw new UnsupportedOperationException("Unsupported dimension: "
					+ dimension);
		}

	}
}
