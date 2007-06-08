package org.gdms.sql.instruction;

/**
 * Clase base de las excepciones sem�nticas
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class SemanticException extends Exception {
	/**
	 * Creates a new SemanticException object.
	 */
	public SemanticException() {
		super();
	}

	/**
	 * Creates a new SemanticException object.
	 * 
	 * @param arg0
	 */
	public SemanticException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new SemanticException object.
	 * 
	 * @param arg0
	 */
	public SemanticException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Creates a new SemanticException object.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public SemanticException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
