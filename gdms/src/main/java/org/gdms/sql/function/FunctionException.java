package org.gdms.sql.function;

/**
 * Excepci�n producida en el c�digo de las funciones
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class FunctionException extends Exception {
	/**
	 *
	 */
	public FunctionException() {
		super();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param message
	 */
	public FunctionException(String message) {
		super(message);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param message
	 * @param cause
	 */
	public FunctionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param cause
	 */
	public FunctionException(Throwable cause) {
		super(cause);
	}
}
