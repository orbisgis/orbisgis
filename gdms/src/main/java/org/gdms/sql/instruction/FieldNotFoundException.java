package org.gdms.sql.instruction;

/**
 * Se da cuando no hay ning�n campo con el nombre dado en las tablas de la
 * cl�usula FROM
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class FieldNotFoundException extends SemanticException {
	/**
	 * Creates a new FieldNotFoundException object.
	 */
	public FieldNotFoundException() {
		super();
	}

	/**
	 * Creates a new FieldNotFoundException object.
	 * 
	 * @param arg0
	 */
	public FieldNotFoundException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new FieldNotFoundException object.
	 * 
	 * @param arg0
	 */
	public FieldNotFoundException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Creates a new FieldNotFoundException object.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public FieldNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
