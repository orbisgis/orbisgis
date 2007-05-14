package org.gdms.sql.internalExceptions;

/**
 * Internal exception event
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class InternalExceptionEvent {
	private Object source;
	private InternalException ie;

	/**
	 *
	 */
	public InternalExceptionEvent(Object source, InternalException ie) {
		this.source = source;
		this.ie = ie;
	}

	/**
	 * gets the originator of the event
	 *
	 * @return originator of the event
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Gets the exception that caused the event
	 *
	 * @return InternalException
	 */
	public InternalException getInternalException() {
		return ie;
	}
}
