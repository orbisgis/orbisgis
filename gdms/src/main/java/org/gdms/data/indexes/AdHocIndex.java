package org.gdms.data.indexes;


public interface AdHocIndex {

	/**
	 * Gets an iterator that will iterate through the filtered rows in the
	 * DataSource that was used in the buildIndex method
	 *
	 *
	 * @param indexQuery
	 * @return
	 */
	public int[] getIterator(IndexQuery indexQuery)
			throws IndexException;

}
