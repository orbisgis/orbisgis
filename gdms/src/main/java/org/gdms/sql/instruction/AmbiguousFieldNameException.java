package org.gdms.sql.instruction;

/**
 * Se da cuando no se puede deducir a qu� tabla pertenece un campo
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class AmbiguousFieldNameException extends SemanticException {
	/**
	 * Creates a new AmbiguousFieldNameException object.
	 */
	public AmbiguousFieldNameException() {
		super();
	}

	/**
	 * Creates a new AmbiguousFieldNameException object.
	 * 
	 * @param arg0
	 */
	public AmbiguousFieldNameException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new AmbiguousFieldNameException object.
	 * 
	 * @param arg0
	 */
	public AmbiguousFieldNameException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Creates a new AmbiguousFieldNameException object.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public AmbiguousFieldNameException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
