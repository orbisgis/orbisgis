package org.gdms.data;

/**
 * Excepci�n que se d� cuando un driver devuelve un tipo de datos no reconocido
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class UnknownTypeException extends Exception {
	/**
	 * Creates a new UnknownTypeException object.
	 */
	public UnknownTypeException() {
		super();
	}

	/**
	 * Creates a new UnknownTypeException object.
	 * 
	 * @param arg0
	 */
	public UnknownTypeException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new UnknownTypeException object.
	 * 
	 * @param arg0
	 */
	public UnknownTypeException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Creates a new UnknownTypeException object.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public UnknownTypeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
