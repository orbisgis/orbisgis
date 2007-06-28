/*
 * Created on 23-oct-2004
 */
package org.gdms.sql.indexes;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public abstract class MemoryIndexSet {
	protected ArrayList<Long> indexes = new ArrayList<Long>();

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#getIndex(long)
	 */
	public long getIndex(long nth) {
		return indexes.get((int) nth);
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#close()
	 */
	public void close() {
	}
}
