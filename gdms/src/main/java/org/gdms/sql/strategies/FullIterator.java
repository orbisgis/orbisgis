/**
 *
 */
package org.gdms.sql.strategies;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.DefaultRow;
import org.gdms.sql.instruction.Row;

public class FullIterator implements Iterator<Row> {

	private DataSource source;

	private int index = 0;

	private long rowCount;

	public FullIterator(DataSource source) throws DriverException {
		this.source = source;
		rowCount = source.getRowCount();
	}

	public boolean hasNext() {
		return index < rowCount;
	}

	public DefaultRow next() {
		DefaultRow ret = new DefaultRow(source, index);
		index++;
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}