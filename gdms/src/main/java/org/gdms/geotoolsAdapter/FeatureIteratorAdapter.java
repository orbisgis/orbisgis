package org.gdms.geotoolsAdapter;

import java.util.NoSuchElementException;

import org.gdms.data.SpatialDataSource;
import org.gdms.data.driver.DriverException;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;

public class FeatureIteratorAdapter implements FeatureIterator {

	private SpatialDataSource ds;

	private int index;

	public FeatureIteratorAdapter(SpatialDataSource ds) {
		index = 0;
		this.ds = ds;
	}

	public void remove() {
		throw new RuntimeException();
	}

	public void close() {

	}

	public boolean hasNext() {
		try {
			return index < ds.getRowCount();
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public Feature next() throws NoSuchElementException {
		index++;
		return new FeatureAdapter(ds, index - 1);
	}

}
