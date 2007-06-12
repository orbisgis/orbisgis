package org.gdms.sql.instruction;

import java.util.Iterator;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.spatial.FID;
import org.gdms.spatial.SpatialDataSource;

public class SpatialIterator implements Iterator<Row> {

	private SpatialDataSource sds;

	private List<FID> results;

	private int index = 0;

	public SpatialIterator(DataSource ds, List<FID> results) {
		this.sds = (SpatialDataSource) ds;
		this.results = results;
	}

	public boolean hasNext() {
		return results.size() > index;
	}

	public DefaultRow next() {
		DefaultRow ret = new DefaultRow(sds, sds.getRow(results.get(index)));
		index++;
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
