package org.gdms.sql.internalExceptions;

/**
 * Internal exception catcher
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class InternalExceptionCatcher {
	private static InternalExceptionListenerSupport support = new InternalExceptionListenerSupport();

	/**
	 * patron observer
	 * 
	 * @param listener
	 *            DOCUMENT ME!
	 */
	public static void addInternalExceptionListener(
			InternalExceptionListener listener) {
		support.addInternalExceptionListener(listener);
	}

	/**
	 * Notifies the event observers
	 * 
	 * @param arg0
	 *            the event
	 */
	public static void callExceptionRaised(InternalExceptionEvent arg0) {
		support.callExceptionRaised(arg0);
	}

	/**
	 * patron observer
	 * 
	 * @param listener
	 *            DOCUMENT ME!
	 */
	public static void removeInternalExceptionListener(
			InternalExceptionListener listener) {
		support.removeInternalExceptionListener(listener);
	}
}
