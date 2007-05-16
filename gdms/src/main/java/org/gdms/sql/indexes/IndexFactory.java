/*
 * Created on 21-oct-2004
 */
package org.gdms.sql.indexes;

/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class IndexFactory {
	public static int MEMORY_THRESHOLD = 10; //Debe ser < que el mayor valor soportado por MemoryIndexSet

	/**
	 * DOCUMENT ME!
	 *
	 * @param size DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static FixedIndexSet createFixedIndex(long size) {
		if (size < MEMORY_THRESHOLD) {
			return new FixedMemoryIndexSet((int) size);
		} else {
			return new FixedDiskIndexSet(size);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static VariableIndexSet createVariableIndex() {
		return new IndexSetImpl();
	}
}
