package org.gdms.data.indexes;

public interface IndexQuery {

	/**
	 * Get the index identification this query is aimed to
	 *
	 * @return
	 */
	public String getIndexId();

	/**
	 * Gets the field this query is base on
	 *
	 * @return
	 */
	public String getFieldName();
}
