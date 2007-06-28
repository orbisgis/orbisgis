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
	// soportado por MemoryIndexSet

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public static VariableIndexSet createVariableIndex() {
		return new IndexSetImpl();
	}
}
