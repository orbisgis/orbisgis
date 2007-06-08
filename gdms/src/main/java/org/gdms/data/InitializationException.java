package org.gdms.data;

/**
 * The system failed while initializing
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class InitializationException extends RuntimeException {

	/**
	 * 
	 */
	public InitializationException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InitializationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InitializationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InitializationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
