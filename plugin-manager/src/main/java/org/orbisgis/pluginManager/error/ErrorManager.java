package org.orbisgis.pluginManager.error;

public interface ErrorManager {

	/**
	 * Adds an error listener to the system
	 *
	 * @param listener
	 */
	void addErrorListener(ErrorListener listener);

	/**
	 * Removes an error listener from the system
	 *
	 * @param listener
	 */
	void removeErrorListener(ErrorListener listener);

	/**
	 * Notifies an error to the system. All the listeners will be notified
	 *
	 * @param userMsg
	 */
	void error(String userMsg);

	/**
	 * Notifies a warning to the system. All the listeners will be notified
	 *
	 * @param userMsg
	 */
	void warning(String userMsg, Throwable exception);

	/**
	 * Notifies an error to the system. All the listeners will be notified
	 *
	 * @param userMsg
	 * @param exception
	 */
	void error(String userMsg, Throwable exception);
}
