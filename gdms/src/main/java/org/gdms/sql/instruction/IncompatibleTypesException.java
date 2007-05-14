package org.gdms.sql.instruction;

/**
 * Lanzado cuando la operaci�n especificada no est� definida para los tipos de
 * los operandos sobre los que se quiso operar
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class IncompatibleTypesException extends SemanticException {
	/**
	 * Creates a new IncompatibleTypesException object.
	 */
	public IncompatibleTypesException() {
		super();
	}

	/**
	 * Creates a new IncompatibleTypesException object.
	 *
	 * @param arg0
	 */
	public IncompatibleTypesException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new IncompatibleTypesException object.
	 *
	 * @param arg0
	 */
	public IncompatibleTypesException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Creates a new IncompatibleTypesException object.
	 *
	 * @param arg0
	 * @param arg1
	 */
	public IncompatibleTypesException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
