package org.gdms.sql.instruction;

/**
 * @author Fernando Gonz�lez Cort�s
 */
public class EvaluationException extends Exception {
	/**
	 * 
	 */
	public EvaluationException() {
		super();
	}

	/**
	 * @param message
	 */
	public EvaluationException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EvaluationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public EvaluationException(Throwable cause) {
		super(cause);
	}
}
