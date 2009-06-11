package org.gdms.sql.function;

public interface FunctionManagerListener {

	/**
	 * Called when a function is registered in the {@link FunctionManager}
	 * 
	 * @param functionName
	 */
	void functionAdded(String functionName);

	/**
	 * Called when a function is removed from the {@link FunctionManager}
	 * 
	 * @param functionName
	 */
	void functionRemoved(String functionName);

}
