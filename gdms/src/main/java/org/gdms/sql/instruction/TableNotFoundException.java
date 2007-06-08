package org.gdms.sql.instruction;

/**
 * Excepci�n que se lanza cuando no se encuentra una tabla en la instrucci�n
 * introducida por el usuario
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class TableNotFoundException extends SemanticException {
	/**
	 * Creates a new TableNotFoundException object.
	 */
	public TableNotFoundException() {
		super();
	}

	/**
	 * Creates a new TableNotFoundException object.
	 * 
	 * @param arg0
	 */
	public TableNotFoundException(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new TableNotFoundException object.
	 * 
	 * @param arg0
	 */
	public TableNotFoundException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Creates a new TableNotFoundException object.
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public TableNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
