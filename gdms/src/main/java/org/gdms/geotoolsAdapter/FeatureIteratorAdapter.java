package org.gdms.geotoolsAdapter;

import java.util.NoSuchElementException;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureIterator;

public class FeatureIteratorAdapter implements FeatureIterator {

	private SpatialDataSourceDecorator ds;

	private int index;

	public FeatureIteratorAdapter(SpatialDataSourceDecorator ds) {
		index = 0;
		this.ds = ds;
	}

	public void remove() {
		throw new Error();
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