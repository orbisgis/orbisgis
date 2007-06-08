package org.gdms.sql.internalExceptions;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class InternalExceptionListenerSupport {
	private ArrayList<InternalExceptionListener> listeners = new ArrayList<InternalExceptionListener>();

	/**
	 * DOCUMENT ME!
	 * 
	 * @param listener
	 *            DOCUMENT ME!
	 */
	public void addInternalExceptionListener(InternalExceptionListener listener) {
		listeners.add(listener);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param listener
	 *            DOCUMENT ME!
	 */
	public void removeInternalExceptionListener(
			InternalExceptionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param arg0
	 *            DOCUMENT ME!
	 */
	public void callExceptionRaised(
			org.gdms.sql.internalExceptions.InternalExceptionEvent arg0) {
		Iterator i = listeners.iterator();

		while (i.hasNext()) {
			((InternalExceptionListener) i.next()).exceptionRaised(arg0);
		}
	}
}
