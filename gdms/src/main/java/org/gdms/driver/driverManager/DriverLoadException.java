package org.gdms.driver.driverManager;


public class DriverLoadException extends RuntimeException {

	/**
	 * 
	 */
	public DriverLoadException() {
		super();

	}
	/**
	 * @param message
	 */
	public DriverLoadException(String message) {
		super(message);

	}
	/**
	 * @param message
	 * @param cause
	 */
	public DriverLoadException(String message, Throwable cause) {
		super(message, cause);

	}
	/**
	 * @param cause
	 */
	public DriverLoadException(Throwable cause) {
		super(cause);

	}
}
