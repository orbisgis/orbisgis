package org.orbisgis.pluginManager.error;

public interface ErrorListener {

	/**
	 * Invoked when the system has been notified of an error
	 *
	 * @param userMsg
	 *            Error message
	 * @param exception
	 *            Exception that caused the error. May be null
	 */
	public void error(String userMsg, Throwable exception);

	/**
	 * Invoked when the system has been notified of a warning
	 *
	 * @param userMsg
	 *            Warning message
	 * @param exception
	 *            Exception that caused the error. May be null
	 */
	public void warning(String userMsg, Throwable exception);

}
