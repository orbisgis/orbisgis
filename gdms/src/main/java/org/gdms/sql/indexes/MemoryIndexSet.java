/*
 * Created on 23-oct-2004
 */
package org.gdms.sql.indexes;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public abstract class MemoryIndexSet {
	protected long[] indexes;

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#getIndex(long)
	 */
	public long getIndex(long nth) {
		return indexes[(int) nth];
	}

	/**
	 * @see org.gdms.sql.indexes.VariableIndexSet#close()
	 */
	public void close() {
	}
}
