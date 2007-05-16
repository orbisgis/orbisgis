package org.gdms.sql.indexes.hashMap;

import java.io.IOException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface PositionIterator {
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean hasNext();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public int next() throws IOException;
}
