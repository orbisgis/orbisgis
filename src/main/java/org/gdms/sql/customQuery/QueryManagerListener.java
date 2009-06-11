package org.gdms.sql.customQuery;

public interface QueryManagerListener {

	/**
	 * Called when a query is registered in the {@link QueryManager}
	 * 
	 * @param functionName
	 */
	void queryAdded(String functionName);

	/**
	 * Called when a query is removed from the {@link QueryManager}
	 * 
	 * @param functionName
	 */
	void queryRemoved(String functionName);

}
