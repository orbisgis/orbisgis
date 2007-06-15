package org.gdms.data.indexes;

import java.util.Iterator;
import java.util.List;

import org.gdms.data.edition.PhysicalDirection;

public class SpatialIterator implements Iterator<PhysicalDirection> {

	private List<PhysicalDirection> results;

	private int index = 0;

	public SpatialIterator(List<PhysicalDirection> results) {
		this.results = results;
	}

	public boolean hasNext() {
		return results.size() > index;
	}

	public PhysicalDirection next() {
		PhysicalDirection ret = results.get(index);
		index++;
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
