package org.gdms.data.indexes;

import java.util.Iterator;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.sql.instruction.PhysicalDirectionRow;
import org.gdms.sql.strategies.Row;

public class SpatialIterator implements Iterator<Row> {

	private DataSource ds;

	private List<PhysicalDirection> results;

	private int index = 0;

	public SpatialIterator(DataSource ds, List<PhysicalDirection> results) {
		this.ds = ds;
		this.results = results;
	}

	public boolean hasNext() {
		return results.size() > index;
	}

	public Row next() {
		PhysicalDirectionRow ret = new PhysicalDirectionRow(results.get(index), ds);
		index++;
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
