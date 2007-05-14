package org.gdms.sql.internalExceptions;

/**
 * @author Fernando Gonz�lez Cort�s
 */
public interface InternalExceptionListener {
	public void exceptionRaised(InternalExceptionEvent event);
}
